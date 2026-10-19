import { Link, useNavigate } from "@tanstack/react-router";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { useI18n, type TKey } from "@/lib/i18n";
import { AlertCircle, ClipboardList, Smartphone, Headphones, ClipboardCheck, Wallet, Gift, BadgeCheck, Star, MapPin, CreditCard, Settings, Info, ChevronRight } from "lucide-react";

const quickLinks = [
  { label: "myBookings", to: "/account/bookings", icon: ClipboardList },
  { label: "devices", to: "/account/devices", icon: Smartphone },
  { label: "helpSupport", to: "/account/support", icon: Headphones },
] as const;
const menuLinks = [
  { label: "myPlans", to: "/account/plans", icon: ClipboardCheck },
  { label: "wallet", to: "/account/wallet", icon: Wallet },
  { label: "giftCards", to: "/account/gift-cards", icon: Gift },
  { label: "memberships", to: "/account/membership", icon: BadgeCheck },
  { label: "myRating", to: "/account/ratings", icon: Star },
  { label: "manageAddresses", to: "/account/addresses", icon: MapPin },
  { label: "paymentMethods", to: "/account/payments", icon: CreditCard },
  { label: "settings", to: "/account/settings", icon: Settings },
  { label: "aboutDwarSewa", to: "/account/about", icon: Info },
] as const satisfies readonly { label: TKey; to: string; icon: typeof Info }[];

export function AccountPanel({ userId }: { userId: string }) {
  const { t } = useI18n();
  const qc = useQueryClient();
  const navigate = useNavigate();
  const { data } = useQuery({
    queryKey: ["profile-full", userId],
    queryFn: async () => (await supabase.from("profiles").select("*").eq("id", userId).single()).data,
  });

  async function out() {
    await qc.cancelQueries();
    qc.clear();
    await supabase.auth.signOut();
    navigate({ to: "/", replace: true });
  }

  return (
    <div>
      <section className="px-5 pb-6">
        <div className="flex items-center justify-between gap-3">
          <div className="min-w-0">
            {data && !data.registration_completed && <span className="mb-2 inline-flex items-center gap-1 rounded-full bg-destructive/10 px-2 py-1 text-xs font-medium text-destructive"><AlertCircle className="size-3" />{t("incompleteProfile")}</span>}
            <h2 className="break-words text-2xl font-bold leading-tight">{data?.full_name || t("account")}</h2>
            <p className="mt-1 text-sm text-muted-foreground">{data?.phone ? `+${data.phone.replace(/^\+/, "")}` : ""}</p>
          </div>
          <Button asChild variant="outline" size="sm"><Link to="/register/customer">{t(data?.registration_completed ? "edit" : "complete")}</Link></Button>
        </div>
        <div className="mt-5 grid grid-cols-3 gap-3">
          {quickLinks.map(({ label, to, icon: Icon }) => <Button key={to} asChild variant="outline" className="h-28 min-w-0 flex-col items-start justify-between whitespace-normal bg-card p-3 text-left shadow-none"><Link to={to}><Icon className="size-6! text-secondary" /><span className="text-sm leading-snug">{t(label)}</span></Link></Button>)}
        </div>
      </section>
      <nav className="border-t-8 border-muted px-5 py-4" aria-label={t("account")}>
        {menuLinks.map(({ label, to, icon: Icon }) => <Button key={to} asChild variant="ghost" className="h-auto min-h-13 w-full justify-start whitespace-normal px-0 py-3 font-normal"><Link to={to}><Icon className="mr-3 size-5! text-secondary" /><span className="flex-1 text-left">{t(label)}</span><ChevronRight className="size-5!" /></Link></Button>)}
      </nav>
      <div className="px-5">
        <section className="relative overflow-hidden rounded-lg bg-accent p-5">
          <Gift className="absolute right-4 top-6 size-14 text-primary/60" aria-hidden="true" />
          <h3 className="max-w-[75%] text-lg font-semibold">{t("referFriends")}</h3>
          <p className="mt-1 max-w-[75%] text-sm text-muted-foreground">{t("referSubtitle")}</p>
          <Button asChild className="mt-4" variant="secondary"><Link to="/account/referral">{t("referNow")}</Link></Button>
        </section>
        <Button className="mt-7 h-12 w-full text-destructive" variant="outline" onClick={out}>{t("logout")}</Button>
        <p className="mt-6 text-center text-xs text-muted-foreground">DwarSewa</p>
      </div>
    </div>
  );
}
