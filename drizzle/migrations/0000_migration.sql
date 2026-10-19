create type public.app_role as enum ('admin','customer','provider');
create type public.provider_status as enum ('pending','approved','blocked');
create type public.booking_status as enum ('pending','assigned','accepted','on_the_way','started','completed','cancelled');
create type public.payment_mode as enum ('cash','online');

create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  full_name text, phone text, language text not null default 'en',
  created_at timestamptz not null default now()
);
create table public.user_roles (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade not null,
  role public.app_role not null, unique(user_id, role)
);
create table public.services (id uuid primary key default gen_random_uuid(), name text not null, name_hi text, icon text not null, is_active boolean not null default true, sort int not null default 0);
create table public.packages (id uuid primary key default gen_random_uuid(), service_id uuid not null references public.services(id) on delete cascade, name text not null, price int not null, duration_minutes int not null);
create table public.providers (
  id uuid primary key, full_name text, phone text,
  services uuid[] not null default '{}', areas text[] not null default '{}',
  is_verified boolean not null default false, is_online boolean not null default false,
  rating numeric(2,1) not null default 0, jobs_completed int not null default 0,
  id_document_url text, status public.provider_status not null default 'pending',
  created_at timestamptz not null default now()
);
create table public.addresses (id uuid primary key default gen_random_uuid(), user_id uuid not null, locality text not null, full_address text not null);
create table public.bookings (
  id uuid primary key default gen_random_uuid(), customer_id uuid not null,
  package_id uuid not null references public.packages(id), address_id uuid references public.addresses(id),
  slot_time timestamptz not null, payment_mode public.payment_mode not null default 'cash',
  status public.booking_status not null default 'pending', provider_id uuid references public.providers(id),
  notes text, amount int not null default 0, created_at timestamptz not null default now()
);
create table public.reviews (id uuid primary key default gen_random_uuid(), booking_id uuid not null references public.bookings(id) on delete cascade, customer_id uuid not null, provider_id uuid, rating int not null, comment text, created_at timestamptz not null default now());
create table public.complaints (id uuid primary key default gen_random_uuid(), booking_id uuid references public.bookings(id) on delete set null, user_id uuid not null, message text not null, status text not null default 'open', created_at timestamptz not null default now());

grant select on public.services, public.packages to anon;
grant select, insert, update, delete on public.profiles, public.services, public.packages, public.providers, public.addresses, public.bookings, public.reviews, public.complaints to authenticated;
grant select on public.user_roles to authenticated;
grant all on public.profiles, public.user_roles, public.services, public.packages, public.providers, public.addresses, public.bookings, public.reviews, public.complaints to service_role;

alter table public.profiles enable row level security;
alter table public.user_roles enable row level security;
alter table public.services enable row level security;
alter table public.packages enable row level security;
alter table public.providers enable row level security;
alter table public.addresses enable row level security;
alter table public.bookings enable row level security;
alter table public.reviews enable row level security;
alter table public.complaints enable row level security;

create or replace function public.has_role(_user_id uuid, _role public.app_role) returns boolean
language sql stable security definer set search_path = public as $$
  select exists (select 1 from public.user_roles where user_id = _user_id and role = _role) $$;

-- choose role once (customer/provider only)
create or replace function public.choose_role(_role public.app_role) returns void
language plpgsql security definer set search_path = public as $$
declare p public.profiles;
begin
  if auth.uid() is null then raise exception 'not authenticated'; end if;
  if _role not in ('customer','provider') then raise exception 'invalid role'; end if;
  if exists (select 1 from public.user_roles where user_id = auth.uid()) then raise exception 'role already set'; end if;
  insert into public.user_roles(user_id, role) values (auth.uid(), _role);
  if _role = 'provider' then
    select * into p from public.profiles where id = auth.uid();
    insert into public.providers(id, full_name, phone) values (auth.uid(), p.full_name, p.phone) on conflict do nothing;
  end if;
end $$;
revoke execute on function public.choose_role(public.app_role) from anon;

create or replace function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = public as $$
begin
  insert into public.profiles(id, phone) values (new.id, new.phone) on conflict do nothing;
  return new;
end $$;
create trigger on_auth_user_created after insert on auth.users for each row execute function public.handle_new_user();

-- provider self-update limited to is_online/areas/services
create or replace function public.guard_provider_update() returns trigger
language plpgsql security definer set search_path = public as $$
begin
  if public.has_role(auth.uid(),'admin') or auth.uid() is null then return new; end if;
  new.status := old.status; new.is_verified := old.is_verified; new.rating := old.rating; new.jobs_completed := old.jobs_completed;
  if new.is_online and old.status <> 'approved' then new.is_online := false; end if;
  return new;
end $$;
create trigger guard_provider_update before update on public.providers for each row execute function public.guard_provider_update();

create policy "own profile read" on public.profiles for select to authenticated using (id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "own profile update" on public.profiles for update to authenticated using (id = auth.uid()) with check (id = auth.uid());
create policy "own roles read" on public.user_roles for select to authenticated using (user_id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "services read" on public.services for select to anon, authenticated using (true);
create policy "services admin" on public.services for all to authenticated using (public.has_role(auth.uid(),'admin')) with check (public.has_role(auth.uid(),'admin'));
create policy "packages read" on public.packages for select to anon, authenticated using (true);
create policy "packages admin" on public.packages for all to authenticated using (public.has_role(auth.uid(),'admin')) with check (public.has_role(auth.uid(),'admin'));
create policy "provider own read" on public.providers for select to authenticated using (id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "provider own update" on public.providers for update to authenticated using (id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "addresses own" on public.addresses for all to authenticated using (user_id = auth.uid() or public.has_role(auth.uid(),'admin')) with check (user_id = auth.uid());
create policy "bookings read" on public.bookings for select to authenticated using (customer_id = auth.uid() or provider_id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "bookings insert" on public.bookings for insert to authenticated with check (customer_id = auth.uid());
create policy "bookings admin update" on public.bookings for update to authenticated using (public.has_role(auth.uid(),'admin'));
create policy "reviews read" on public.reviews for select to authenticated using (customer_id = auth.uid() or provider_id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "reviews insert" on public.reviews for insert to authenticated with check (customer_id = auth.uid());
create policy "complaints own" on public.complaints for select to authenticated using (user_id = auth.uid() or public.has_role(auth.uid(),'admin'));
create policy "complaints insert" on public.complaints for insert to authenticated with check (user_id = auth.uid());
create policy "complaints admin" on public.complaints for update to authenticated using (public.has_role(auth.uid(),'admin'));

-- seed
insert into public.services(id,name,name_hi,icon,sort) values
('11111111-0000-0000-0000-000000000001','AC Repair','एसी रिपेयर','❄️',1),
('11111111-0000-0000-0000-000000000002','Home Cleaning','घर की सफ़ाई','🧹',2),
('11111111-0000-0000-0000-000000000003','Salon at Home','घर पर सैलून','💇',3),
('11111111-0000-0000-0000-000000000004','Plumber','प्लंबर','🔧',4),
('11111111-0000-0000-0000-000000000005','Electrician','इलेक्ट्रीशियन','💡',5);
insert into public.packages(service_id,name,price,duration_minutes) values
('11111111-0000-0000-0000-000000000001','AC Service (Split)',499,60),
('11111111-0000-0000-0000-000000000001','Gas Refill',2499,90),
('11111111-0000-0000-0000-000000000001','AC Installation',1499,120),
('11111111-0000-0000-0000-000000000002','Bathroom Cleaning',399,60),
('11111111-0000-0000-0000-000000000002','Kitchen Deep Clean',999,150),
('11111111-0000-0000-0000-000000000002','Full Home Deep Clean',3499,300),
('11111111-0000-0000-0000-000000000003','Haircut',299,30),
('11111111-0000-0000-0000-000000000003','Facial & Cleanup',799,60),
('11111111-0000-0000-0000-000000000003','Waxing (Full)',999,75),
('11111111-0000-0000-0000-000000000004','Tap Repair',149,30),
('11111111-0000-0000-0000-000000000004','Leakage Fix',299,45),
('11111111-0000-0000-0000-000000000004','Water Tank Cleaning',699,90),
('11111111-0000-0000-0000-000000000005','Switch / Socket Repair',99,20),
('11111111-0000-0000-0000-000000000005','Fan Installation',199,40),
('11111111-0000-0000-0000-000000000005','House Wiring Check',599,90);
insert into public.providers(id,full_name,phone,services,areas,is_verified,is_online,rating,jobs_completed,status) values
(gen_random_uuid(),'Ravi Kumar','+919800000001',array['11111111-0000-0000-0000-000000000001','11111111-0000-0000-0000-000000000005']::uuid[],array['Lalpur','Morabadi'],true,true,4.7,132,'approved'),
(gen_random_uuid(),'Sunita Devi','+919800000002',array['11111111-0000-0000-0000-000000000002','11111111-0000-0000-0000-000000000003']::uuid[],array['Harmu','Doranda','Kanke Road'],true,false,4.8,89,'approved'),
(gen_random_uuid(),'Amit Oraon','+919800000003',array['11111111-0000-0000-0000-000000000004']::uuid[],array['Bariatu','Lalpur'],false,false,0,0,'pending');