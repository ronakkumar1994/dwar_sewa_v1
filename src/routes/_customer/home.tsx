import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { MapPin, Search, UserRound, House, ClipboardList, Headphones, ArrowRight, ChevronDown } from "lucide-react";
import { supabase } from "@/integrations/supabase/client";
import { LangToggle, LOCALITIES, useI18n } from "@/lib/i18n";
import { OffersCarousel } from "@/components/OffersCarousel";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import cleaning from "@/assets/service-cleaning.jpg";
import repairs from "@/assets/service-repairs.jpg";
import salon from "@/assets/service-salon.jpg";

export const Route = createFileRoute("/_customer/home")({
  head: () => ({ meta: [
    { title: "DwarSewa — Home services at your doorstep" },
    { name: "description", content: "Explore cleaning, salon, AC repair, plumbing and electrician packages in Ranchi." },
    { property: "og:title", content: "DwarSewa — Home services at your doorstep" },
    { property: "og:description", content: "Compare home service packages and discover current DwarSewa offers in Ranchi." },
    { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" },
  ] }),
  component: Home,
});

function serviceImage(name: string) {
  return /salon/i.test(name) ? salon : /clean/i.test(name) ? cleaning : repairs;
}

function Home() {
  const { t, lang } = useI18n();
  const { userId } = Route.useRouteContext();
  const [q, setQ] = useState("");
  const [locality, setLocality] = useState("Lalpur");
  useEffect(() => { const saved = localStorage.getItem("locality"); if (saved && LOCALITIES.includes(saved)) setLocality(saved); }, []);
  const { data: services = [], isPending, isError, refetch } = useQuery({ queryKey: ["home-services"], queryFn: async () => {
    const { data, error } = await supabase.from("services").select("*,packages(*)").eq("is_active", true).order("sort");
    if (error) throw error; return data ?? [];
  }});
  const { data: active } = useQuery({ queryKey: ["active-booking", userId], queryFn: async () => (await supabase.from("bookings").select("id,status,slot_time,packages(name)").eq("customer_id", userId).not("status", "in", "(completed,cancelled)").order("slot_time").limit(1).maybeSingle()).data });
  const filtered = services.filter((s) => `${s.name} ${s.name_hi ?? ""}`.toLowerCase().includes(q.toLowerCase()));
  const displayName = (s: typeof services[number]) => lang === "hi" && s.name_hi ? s.name_hi : s.name;
  const featured = [...services].sort((a, b) => Number(/clean/i.test(b.name)) - Number(/clean/i.test(a.name))).slice(0, 3);

  return <main className="mx-auto min-h-screen max-w-md bg-card pb-24">
    <header className="bg-secondary px-5 pb-5 pt-4 text-secondary-foreground">
      <div className="grid grid-cols-[minmax(0,1fr)_auto] items-center gap-3">
        <div className="min-w-0"><p className="text-sm font-bold">DwarSewa</p><div className="mt-1 flex min-w-0 items-center gap-1"><MapPin className="size-4 shrink-0" /><select aria-label={t("locality")} value={locality} onChange={(e) => { setLocality(e.target.value); localStorage.setItem("locality", e.target.value); }} className="min-w-0 max-w-full bg-secondary text-sm font-semibold outline-none">{LOCALITIES.map((l) => <option key={l}>{l}</option>)}</select><ChevronDown className="size-3 shrink-0" /></div></div>
        <div className="flex shrink-0 items-center gap-2"><LangToggle /><Button asChild variant="ghost" size="icon" className="bg-secondary-foreground/10 text-secondary-foreground hover:bg-secondary-foreground/20"><Link to="/account" aria-label={t("profile")}><UserRound /></Link></Button></div>
      </div>
      <div className="relative mt-4"><Search className="absolute left-3 top-3 size-5 text-muted-foreground" /><Input aria-label={t("search")} placeholder={t("search")} value={q} onChange={(e) => setQ(e.target.value)} className="h-11 border-0 bg-card pl-10 text-foreground shadow-none" /></div>
    </header>
    {!q && <section className="relative h-48 overflow-hidden bg-secondary">
      <img src={cleaning} width={1024} height={768} alt="" className="absolute inset-0 h-full w-full object-cover object-right" />
      <div className="absolute inset-0 bg-secondary/70" />
      <div className="relative flex h-full flex-col justify-center px-5 text-secondary-foreground"><h1 className="max-w-56 text-2xl font-bold leading-tight">{t("homeCare")}</h1><p className="mt-2 max-w-56 text-sm">{t("homeCareSub")}</p><Button asChild size="sm" className="mt-3 w-fit"><Link to="/account/bookings">{t("myBookings")}<ArrowRight /></Link></Button></div>
    </section>}
    <section className="px-5 pt-5 pb-6"><h2 className="mb-4 text-lg font-bold">{t("forYourHome")}</h2>
      {isPending && <p className="text-sm text-muted-foreground">{t("loading")}</p>}
      {isError && <Button variant="outline" onClick={() => refetch()}>{t("retry")}</Button>}
      {!isPending && !isError && !filtered.length && <p className="text-muted-foreground">{t("noServices")}</p>}
      <div className="grid grid-cols-3 gap-x-3 gap-y-5">{filtered.map((s) => <Button key={s.id} asChild variant="ghost" className="h-auto min-w-0 flex-col gap-2 whitespace-normal p-0 hover:bg-transparent"><Link to="/service/$id" params={{ id: s.id }}><span className="relative block aspect-[4/3] w-full overflow-hidden rounded-lg bg-muted"><img src={serviceImage(s.name)} loading="lazy" width={1024} height={768} alt="" className="h-full w-full object-cover" /><span className="absolute bottom-1 right-1 flex size-6 items-center justify-center rounded-full bg-card text-sm">{s.icon}</span></span><span className="text-center text-xs font-semibold leading-snug">{displayName(s)}</span></Link></Button>)}</div>
      {active && <Button asChild variant="outline" className="mt-5 h-auto w-full justify-between whitespace-normal p-3 text-left"><Link to="/account/bookings"><span className="min-w-0"><span className="block text-xs text-primary">{t("activeBooking")}</span><span className="block font-semibold">{active.packages?.name}</span><span className="block text-xs text-muted-foreground">{t(active.status)}</span></span><ArrowRight /></Link></Button>}
      <OffersCarousel />
    </section>
    {!q && <section className="border-t-8 border-muted px-5 py-6"><h2 className="mb-4 text-lg font-bold">{t("spotlight")}</h2><div className="flex snap-x gap-3 overflow-x-auto pb-2">{featured.map((s) => <Button key={s.id} asChild variant="ghost" className="relative h-52 w-64 shrink-0 snap-start overflow-hidden rounded-lg p-0"><Link to="/service/$id" params={{ id: s.id }}><img src={serviceImage(s.name)} loading="lazy" width={1024} height={768} alt="" className="h-full w-full object-cover" /><span className="absolute inset-x-0 bottom-0 bg-secondary/90 p-3 text-left text-secondary-foreground"><span className="block text-lg font-semibold">{displayName(s)}</span><span className="text-xs">{t("explore")} →</span></span></Link></Button>)}</div></section>}
    {filtered.map((s) => <section key={s.id} className="border-t-8 border-muted py-6"><div className="mb-4 grid grid-cols-[minmax(0,1fr)_auto] items-center gap-3 px-5"><h2 className="min-w-0 text-lg font-bold">{displayName(s)}</h2><Button asChild size="sm" variant="link" className="px-0 text-secondary"><Link to="/service/$id" params={{ id: s.id }}>{t("seeAll")}</Link></Button></div><div className="flex snap-x gap-3 overflow-x-auto px-5 pb-2">{[...s.packages].sort((a, b) => a.price - b.price).map((p) => <Button key={p.id} asChild variant="ghost" className="h-auto w-40 shrink-0 snap-start flex-col items-start justify-start whitespace-normal rounded-lg p-0 text-left hover:bg-transparent"><Link to="/service/$id" params={{ id: s.id }}><img src={serviceImage(s.name)} loading="lazy" width={1024} height={768} alt="" className="aspect-[4/3] w-full rounded-lg object-cover" /><h3 className="mt-2 min-h-10 text-sm font-semibold leading-snug">{p.name}</h3><span className="text-xs text-muted-foreground">{p.duration_minutes} {t("mins")}</span><span className="text-sm font-semibold">₹{p.price.toLocaleString("en-IN")}</span></Link></Button>)}</div></section>)}
    <section className="border-t-8 border-muted px-5 py-7"><div className="grid grid-cols-[minmax(0,1fr)_auto] items-center gap-4"><div className="min-w-0"><h2 className="text-lg font-bold">{t("referFriends")}</h2><p className="mt-1 text-sm text-muted-foreground">{t("referSubtitle")}</p></div><Button asChild size="icon" variant="secondary"><Link to="/account/referral" aria-label={t("referNow")}><ArrowRight /></Link></Button></div></section>
    <nav aria-label={t("home")} className="fixed inset-x-0 bottom-0 z-20 mx-auto grid max-w-md grid-cols-4 border-t bg-card pb-[env(safe-area-inset-bottom)]">{[{ to: "/home", label: "home", icon: House }, { to: "/account/bookings", label: "myBookings", icon: ClipboardList }, { to: "/account/support", label: "helpSupport", icon: Headphones }, { to: "/account", label: "profile", icon: UserRound }].map(({ to, label, icon: Icon }) => <Button key={to} asChild variant="ghost" className="h-16 min-w-0 flex-col gap-1 whitespace-normal rounded-none px-1 text-muted-foreground"><Link to={to as "/home" | "/account/bookings" | "/account/support" | "/account"} activeProps={{ className: "text-secondary" }} activeOptions={{ exact: true }}><Icon className="size-5!" /><span className="text-[10px] leading-tight">{t(label as "home" | "myBookings" | "helpSupport" | "profile")}</span></Link></Button>)}</nav>
  </main>;
}
