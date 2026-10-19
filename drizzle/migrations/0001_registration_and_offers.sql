ALTER TABLE public.profiles ADD COLUMN registration_completed boolean NOT NULL DEFAULT false;
ALTER TABLE public.providers ADD COLUMN business_address text NOT NULL DEFAULT '', ADD COLUMN experience_years integer NOT NULL DEFAULT 0, ADD COLUMN bio text NOT NULL DEFAULT '';
CREATE TABLE public.account_phones (phone text PRIMARY KEY, user_id uuid NOT NULL UNIQUE);
GRANT ALL ON public.account_phones TO service_role;
ALTER TABLE public.account_phones ENABLE ROW LEVEL SECURITY;
INSERT INTO public.account_phones(phone,user_id) SELECT '+91' || right(regexp_replace(u.phone,'[^0-9]','','g'),10),u.id FROM auth.users u WHERE u.phone_confirmed_at IS NOT NULL AND u.phone ~ '^(\+?91)?[6-9][0-9]{9}$' ON CONFLICT DO NOTHING;
CREATE OR REPLACE FUNCTION public.guard_profile_phone() RETURNS trigger LANGUAGE plpgsql SET search_path = public AS $$ BEGIN IF auth.uid() IS NOT NULL AND NEW.phone IS DISTINCT FROM OLD.phone THEN RAISE EXCEPTION 'Mobile number cannot be changed'; END IF; RETURN NEW; END $$;
CREATE TRIGGER guard_profile_phone BEFORE UPDATE ON public.profiles FOR EACH ROW EXECUTE FUNCTION public.guard_profile_phone();
CREATE OR REPLACE FUNCTION public.complete_registration(_user_id uuid,_phone text,_role public.app_role,_name text,_language text,_address text,_locality text,_services uuid[],_areas text[],_experience integer,_bio text) RETURNS void LANGUAGE plpgsql SECURITY DEFINER SET search_path = public AS $$
DECLARE existing_role public.app_role; BEGIN
IF _role NOT IN ('customer','provider') OR _phone !~ '^\+91[6-9][0-9]{9}$' OR length(trim(_name)) NOT BETWEEN 2 AND 100 OR length(trim(_address)) NOT BETWEEN 5 AND 500 OR _language NOT IN ('en','hi') OR _locality NOT IN ('Lalpur','Kanke Road','Morabadi','Harmu','Doranda','Bariatu','Other') THEN RAISE EXCEPTION 'Invalid registration details'; END IF;
PERFORM pg_advisory_xact_lock(hashtextextended(_user_id::text,0));
SELECT role INTO existing_role FROM public.user_roles WHERE user_id = _user_id LIMIT 1;
IF existing_role IS NOT NULL AND existing_role <> _role THEN RAISE EXCEPTION 'This mobile already has another account type'; END IF;
IF _role = 'provider' AND (cardinality(_services) < 1 OR cardinality(_services) > 30 OR cardinality(_areas) < 1 OR cardinality(_areas) > 7 OR _experience NOT BETWEEN 0 AND 60 OR length(_bio) > 1000 OR EXISTS(SELECT 1 FROM unnest(_services) s WHERE NOT EXISTS(SELECT 1 FROM public.services WHERE id=s AND is_active)) OR EXISTS(SELECT 1 FROM unnest(_areas) a WHERE a NOT IN ('Lalpur','Kanke Road','Morabadi','Harmu','Doranda','Bariatu','Other'))) THEN RAISE EXCEPTION 'Invalid provider details'; END IF;
INSERT INTO public.account_phones(phone,user_id) VALUES (_phone,_user_id) ON CONFLICT (user_id) DO UPDATE SET phone=excluded.phone;
UPDATE public.profiles SET full_name=trim(_name),phone=_phone,language=_language,registration_completed=true WHERE id=_user_id;
IF NOT FOUND THEN RAISE EXCEPTION 'Profile unavailable'; END IF;
IF existing_role IS NULL THEN INSERT INTO public.user_roles(user_id,role) VALUES (_user_id,_role); END IF;
IF _role='provider' THEN INSERT INTO public.providers(id,full_name,phone,services,areas,business_address,experience_years,bio) VALUES(_user_id,trim(_name),_phone,_services,_areas,trim(_address),_experience,trim(_bio)) ON CONFLICT(id) DO UPDATE SET full_name=excluded.full_name,phone=excluded.phone,services=excluded.services,areas=excluded.areas,business_address=excluded.business_address,experience_years=excluded.experience_years,bio=excluded.bio;
ELSE
IF EXISTS(SELECT 1 FROM public.addresses WHERE user_id=_user_id) THEN UPDATE public.addresses SET locality=_locality,full_address=trim(_address) WHERE id=(SELECT id FROM public.addresses WHERE user_id=_user_id ORDER BY id LIMIT 1); ELSE INSERT INTO public.addresses(user_id,locality,full_address) VALUES(_user_id,_locality,trim(_address)); END IF;
END IF; END $$;
REVOKE ALL ON FUNCTION public.complete_registration(uuid,text,public.app_role,text,text,text,text,uuid[],text[],integer,text) FROM PUBLIC,anon,authenticated;
GRANT EXECUTE ON FUNCTION public.complete_registration(uuid,text,public.app_role,text,text,text,text,uuid[],text[],integer,text) TO service_role;
CREATE OR REPLACE FUNCTION public.choose_role(_role public.app_role) RETURNS void LANGUAGE plpgsql SECURITY DEFINER SET search_path = public AS $$ BEGIN RAISE EXCEPTION 'Complete registration to choose your account type'; END $$;
CREATE TABLE public.offers (id uuid PRIMARY KEY DEFAULT gen_random_uuid(),title text NOT NULL CHECK(length(title) BETWEEN 2 AND 120),title_hi text NOT NULL DEFAULT '' CHECK(length(title_hi)<=120),details text NOT NULL CHECK(length(details) BETWEEN 5 AND 3000),details_hi text NOT NULL DEFAULT '' CHECK(length(details_hi)<=3000),terms text NOT NULL DEFAULT '' CHECK(length(terms)<=2000),terms_hi text NOT NULL DEFAULT '' CHECK(length(terms_hi)<=2000),code text NOT NULL DEFAULT '' CHECK(length(code)<=40),service_id uuid REFERENCES public.services(id),starts_at timestamptz NOT NULL DEFAULT now(),ends_at timestamptz,is_active boolean NOT NULL DEFAULT false,sort integer NOT NULL DEFAULT 0,created_at timestamptz NOT NULL DEFAULT now());
GRANT SELECT ON public.offers TO anon;
GRANT SELECT,INSERT,UPDATE,DELETE ON public.offers TO authenticated;
GRANT ALL ON public.offers TO service_role;
ALTER TABLE public.offers ENABLE ROW LEVEL SECURITY;
CREATE POLICY "offers published read" ON public.offers FOR SELECT TO anon,authenticated USING(is_active AND starts_at<=now() AND (ends_at IS NULL OR ends_at>now()));
CREATE POLICY "offers admin manage" ON public.offers FOR ALL TO authenticated USING(public.has_role(auth.uid(),'admin')) WITH CHECK(public.has_role(auth.uid(),'admin'));
CREATE OR REPLACE FUNCTION public.validate_offer_dates() RETURNS trigger LANGUAGE plpgsql SET search_path=public AS $$ BEGIN IF NEW.ends_at IS NOT NULL AND NEW.ends_at<=NEW.starts_at THEN RAISE EXCEPTION 'Offer end must follow start'; END IF; RETURN NEW; END $$;
CREATE TRIGGER validate_offer_dates BEFORE INSERT OR UPDATE ON public.offers FOR EACH ROW EXECUTE FUNCTION public.validate_offer_dates();