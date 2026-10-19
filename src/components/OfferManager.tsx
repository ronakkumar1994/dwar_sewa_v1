import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useServerFn } from "@tanstack/react-start";
import { Pencil, Plus, EyeOff } from "lucide-react";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import type { Tables } from "@/integrations/supabase/types";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { useI18n } from "@/lib/i18n";
import { offerSchema } from "@/lib/offers";
import { saveOffer } from "@/lib/offers.functions";

const empty = { title: "", title_hi: "", details: "", details_hi: "", terms: "", terms_hi: "", code: "", service_id: "", starts_at: "", ends_at: "", sort: 0 };
const localDate = (value: string) => { const date = new Date(value); return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 16); };
export function OfferManager() {
  const { t } = useI18n(); const qc = useQueryClient(); const save = useServerFn(saveOffer);
  const [editing, setEditing] = useState(false); const [id, setId] = useState<string>(); const [form, setForm] = useState(empty); const [busy, setBusy] = useState(false);
  const { data: offers = [] } = useQuery({ queryKey: ["admin-offers"], queryFn: async () => { const { data, error } = await supabase.from("offers").select("*").order("sort"); if (error) throw error; return data ?? []; } });
  const { data: services = [] } = useQuery({ queryKey: ["services"], queryFn: async () => (await supabase.from("services").select("*").eq("is_active", true).order("sort")).data ?? [] });
  function open(o?: Tables<"offers">) { setId(o?.id); setForm(o ? { title: o.title, title_hi: o.title_hi, details: o.details, details_hi: o.details_hi, terms: o.terms, terms_hi: o.terms_hi, code: o.code, service_id: o.service_id ?? "", starts_at: localDate(o.starts_at), ends_at: o.ends_at ? localDate(o.ends_at) : "", sort: o.sort } : { ...empty, starts_at: localDate(new Date().toISOString()) }); setEditing(true); }
  async function submit(active: boolean) {
    let offer;
    try { offer = offerSchema.parse({ ...form, service_id: form.service_id || null, starts_at: new Date(form.starts_at).toISOString(), ends_at: form.ends_at ? new Date(form.ends_at).toISOString() : null, is_active: active }); }
    catch { toast.error(t("registrationInvalid")); return; }
    setBusy(true); try { await save({ data: { id, offer } }); await qc.invalidateQueries({ queryKey: ["admin-offers"] }); await qc.invalidateQueries({ queryKey: ["published-offers"] }); setEditing(false); toast.success(t("saved")); } catch { toast.error(t("registrationInvalid")); } finally { setBusy(false); }
  }
  async function hide(o: Tables<"offers">) { setBusy(true); try { await save({ data: { id: o.id, offer: { ...o, is_active: false } } }); await qc.invalidateQueries({ queryKey: ["admin-offers"] }); } catch { toast.error(t("registrationInvalid")); } finally { setBusy(false); } }
  return <section className="mt-10 border-t pt-6"><div className="flex flex-wrap items-center justify-between gap-3"><h2 className="text-xl font-bold">{t("offers")}</h2><Button onClick={() => open()}><Plus />{t("newOffer")}</Button></div>
    {editing && <form className="mt-5 grid gap-4 border-y py-5 md:grid-cols-2" onSubmit={(e) => { e.preventDefault(); submit(true); }}>
      {(["title", "details", "terms"] as const).flatMap((key) => ([key, `${key}_hi`] as const).map((field) => <label key={field} className="block text-sm font-semibold">{t(key)} · {t(field.endsWith("_hi") ? "hindi" : "english")}{field === "title" || field === "details" ? " *" : ""}{key === "title" ? <Input className="mt-2" required={field === "title"} maxLength={120} value={form[field]} onChange={(e) => setForm({ ...form, [field]: e.target.value })} /> : <textarea className="mt-2 w-full rounded-lg border bg-card p-3" rows={3} required={field === "details"} maxLength={key === "details" ? 3000 : 2000} value={form[field]} onChange={(e) => setForm({ ...form, [field]: e.target.value })} />}</label>))}
      <label className="text-sm font-semibold">{t("offerCode")}<Input className="mt-2" maxLength={40} value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} /></label>
      <label className="text-sm font-semibold">{t("services")}<select className="mt-2 h-10 w-full rounded-lg border bg-card px-3" value={form.service_id} onChange={(e) => setForm({ ...form, service_id: e.target.value })}><option value="">{t("allServices")}</option>{services.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}</select></label>
      <label className="text-sm font-semibold">{t("startDate")}<Input className="mt-2" type="datetime-local" required value={form.starts_at} onChange={(e) => setForm({ ...form, starts_at: e.target.value })} /></label>
      <label className="text-sm font-semibold">{t("endDate")}<Input className="mt-2" type="datetime-local" value={form.ends_at} onChange={(e) => setForm({ ...form, ends_at: e.target.value })} /></label>
      <label className="text-sm font-semibold">{t("sortOrder")}<Input className="mt-2" type="number" min={0} max={999} value={form.sort} onChange={(e) => setForm({ ...form, sort: Number(e.target.value) })} /></label>
      <div className="flex flex-wrap items-end gap-2"><Button disabled={busy}>{t("publish")}</Button><Button type="button" variant="outline" disabled={busy} onClick={() => submit(false)}>{t("saveDraft")}</Button><Button type="button" variant="ghost" onClick={() => setEditing(false)}>{t("cancel")}</Button></div>
    </form>}
    <div className="mt-4 space-y-3">{offers.map((o) => <div key={o.id} className="flex items-center justify-between gap-3 rounded-lg border bg-card p-4"><div className="min-w-0"><p className="break-words font-semibold">{o.title}</p><p className="text-sm text-muted-foreground">{o.is_active ? t("publish") : t("draft")}</p></div><div className="flex shrink-0 gap-1"><Button variant="ghost" size="icon" aria-label={t("edit")} title={t("edit")} onClick={() => open(o)}><Pencil /></Button>{o.is_active && <Button variant="ghost" size="icon" disabled={busy} aria-label={t("unpublish")} title={t("unpublish")} onClick={() => hide(o)}><EyeOff /></Button>}</div></div>)}</div>
  </section>;
}