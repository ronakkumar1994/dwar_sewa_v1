import { useEffect, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate } from "@tanstack/react-router";
import { useServerFn } from "@tanstack/react-start";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { BrandLogo } from "./BrandLogo";
import { LangToggle, LOCALITIES, useI18n } from "@/lib/i18n";
import { registrationSchema, type Registration } from "@/lib/registration";
import { registerAccount } from "@/lib/registration.functions";
import { TEST_ACCOUNTS } from "@/lib/test-login";
import { homeFor } from "@/lib/auth";

export function RegistrationForm({ role, userId }: { role: "customer" | "provider"; userId: string }) {
  const { t, lang } = useI18n();
  const navigate = useNavigate();
  const qc = useQueryClient();
  const save = useServerFn(registerAccount);
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState<Registration>({ role, name: "", language: lang, address: "", locality: "Lalpur", services: [], areas: [], experience: 0, bio: "" });
  const [phone, setPhone] = useState("");
  const [loaded, setLoaded] = useState(false);
  const { data: saved, isLoading, isError } = useQuery({ queryKey: ["registration", userId, role], queryFn: async () => {
    const [p, a, v, auth] = await Promise.all([
      supabase.from("profiles").select("*").eq("id", userId).single(),
      supabase.from("addresses").select("*").eq("user_id", userId).limit(1).maybeSingle(),
      role === "provider" ? supabase.from("providers").select("*").eq("id", userId).maybeSingle() : Promise.resolve({ data: null, error: null }),
      supabase.auth.getUser(),
    ]);
    if (p.error || a.error || v.error) throw new Error("Could not load registration");
    return { profile: p.data, address: a.data, provider: v.data, phone: auth.data.user?.phone || (import.meta.env.DEV ? TEST_ACCOUNTS.find((x) => x.id === userId)?.phone : "") || p.data.phone || "" };
  }});
  useEffect(() => {
    if (!saved || loaded) return;
    setForm((f) => ({ ...f, name: saved.profile.full_name ?? "", address: saved.provider?.business_address || saved.address?.full_address || "", locality: saved.address?.locality || "Lalpur", services: saved.provider?.services ?? [], areas: saved.provider?.areas ?? [], experience: saved.provider?.experience_years ?? 0, bio: saved.provider?.bio ?? "" }));
    setPhone(saved.phone);
    setLoaded(true);
  }, [saved, loaded]);
  const { data: services = [] } = useQuery({ queryKey: ["services"], queryFn: async () => {
    const { data, error } = await supabase.from("services").select("*").eq("is_active", true).order("sort");
    if (error) throw error; return data ?? [];
  }});
  const update = <K extends keyof Registration>(key: K, value: Registration[K]) => setForm((f) => ({ ...f, [key]: value }));
  const select = (key: "services" | "areas", value: string) => update(key, form[key].includes(value) ? form[key].filter((v) => v !== value) : [...form[key], value]);
  async function submit(e: React.FormEvent) {
    e.preventDefault();
    const parsed = registrationSchema.safeParse({ ...form, language: lang });
    if (!parsed.success) { toast.error(t("registrationInvalid")); return; }
    setBusy(true);
    try {
      await save({ data: parsed.data });
      await qc.invalidateQueries();
      toast.success(t("registrationSaved"));
      await navigate({ to: homeFor(role), replace: true });
    } catch (e) { toast.error(e instanceof Error ? e.message : t("registrationInvalid")); }
    finally { setBusy(false); }
  }
  return <main className="mx-auto min-h-screen max-w-md px-5 py-5">
    <div className="flex items-center justify-between"><BrandLogo className="w-40" /><LangToggle /></div>
    <h1 className="mt-6 text-2xl font-bold">{t(role === "provider" ? "providerRegistration" : "customerRegistration")}</h1>
    <p className="mt-1 text-sm text-muted-foreground">{t("oneMobile")}</p>
    {isLoading ? <p className="mt-8">…</p> : isError ? <p className="mt-8 text-destructive">{t("registrationInvalid")}</p> : <form onSubmit={submit} className="mt-6 space-y-5">
      <label className="block text-sm font-semibold">{t("phone")}<Input className="mt-2 h-12 bg-muted" value={phone} readOnly /></label>
      <label className="block text-sm font-semibold">{t("yourName")} *<Input className="mt-2 h-12" required minLength={2} maxLength={100} value={form.name} onChange={(e) => update("name", e.target.value)} autoComplete="name" /></label>
      <label className="block text-sm font-semibold">{t("address")} *<textarea className="mt-2 w-full rounded-lg border bg-card p-3" rows={3} required minLength={5} maxLength={500} value={form.address} onChange={(e) => update("address", e.target.value)} /></label>
      {role === "customer" ? <label className="block text-sm font-semibold">{t("locality")} *<select className="mt-2 h-12 w-full rounded-lg border bg-card px-3" value={form.locality} onChange={(e) => update("locality", e.target.value)}>{LOCALITIES.map((l) => <option key={l}>{l}</option>)}</select></label> : <>
        <fieldset><legend className="mb-3 text-sm font-semibold">{t("services")} *</legend><div className="grid grid-cols-2 gap-2">{services.map((s) => <label key={s.id} className="flex items-center gap-2 rounded-lg border bg-card p-3 text-sm"><input type="checkbox" checked={form.services.includes(s.id)} onChange={() => select("services", s.id)} className="accent-primary" />{s.icon} {lang === "hi" ? s.name_hi || s.name : s.name}</label>)}</div></fieldset>
        <fieldset><legend className="mb-3 text-sm font-semibold">{t("serviceAreas")} *</legend><div className="grid grid-cols-2 gap-2">{LOCALITIES.map((l) => <label key={l} className="flex items-center gap-2 text-sm"><input type="checkbox" checked={form.areas.includes(l)} onChange={() => select("areas", l)} className="accent-primary" />{l}</label>)}</div></fieldset>
        <label className="block text-sm font-semibold">{t("experience")} *<Input className="mt-2 h-12" type="number" min={0} max={60} required value={form.experience} onChange={(e) => update("experience", Number(e.target.value))} /></label>
        <label className="block text-sm font-semibold">{t("bio")}<textarea className="mt-2 w-full rounded-lg border bg-card p-3" rows={3} maxLength={1000} value={form.bio} onChange={(e) => update("bio", e.target.value)} /></label>
      </>}
      <Button size="xl" disabled={busy}>{busy ? "…" : t("register")}</Button>
    </form>}
    <Button asChild variant="ghost" className="mt-4 w-full"><Link to={homeFor(role)}>{t("cancel")}</Link></Button>
  </main>;
}