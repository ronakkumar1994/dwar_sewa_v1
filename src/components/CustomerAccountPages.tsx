import { useState, type ReactNode } from "react";
import { Link } from "@tanstack/react-router";
import { useQueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { ArrowLeft, ClipboardList, Gift, MapPin, Plus, Pencil, Trash2, Star, Headphones, Smartphone, Wallet, BadgeCheck, CreditCard, ClipboardCheck, Copy, Share2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { BrandLogo } from "@/components/BrandLogo";
import { LangToggle, LOCALITIES, useI18n, type TKey } from "@/lib/i18n";
import { customerAccountOptions } from "@/lib/customer-account";
import { supabase } from "@/integrations/supabase/client";

export type AccountPage = "bookings" | "devices" | "support" | "plans" | "wallet" | "gift-cards" | "membership" | "ratings" | "addresses" | "payments" | "settings" | "about" | "referral";
const titles: Record<AccountPage, TKey> = { bookings: "myBookings", devices: "devices", support: "helpSupport", plans: "myPlans", wallet: "wallet", "gift-cards": "giftCards", membership: "memberships", ratings: "myRating", addresses: "manageAddresses", payments: "paymentMethods", settings: "settings", about: "aboutDwarSewa", referral: "referFriends" };

export function AccountPageShell({ page, children }: { page: AccountPage; children: ReactNode }) {
  const { t } = useI18n();
  return <main className="mx-auto min-h-screen max-w-md bg-card pb-10">
    <header className="flex items-center gap-3 border-b px-5 py-5">
      <Button asChild variant="outline" size="icon" className="shrink-0 rounded-full"><Link to="/account" aria-label={t("back")}><ArrowLeft /></Link></Button>
      <h1 className="text-xl font-semibold leading-snug">{t(titles[page])}</h1>
    </header>
    <div className="px-5 py-6">{children}</div>
  </main>;
}

function Empty({ icon: Icon, message, browse = false }: { icon: typeof Gift; message: TKey; browse?: boolean }) {
  const { t } = useI18n();
  return <div className="flex flex-col items-center py-12 text-center"><span className="flex size-20 items-center justify-center rounded-full bg-muted"><Icon className="size-9 text-secondary" /></span><p className="mt-5 max-w-64 text-lg font-medium">{t(message)}</p>{browse && <Button asChild variant="secondary" className="mt-6"><Link to="/home">{t("browseServices")}</Link></Button>}</div>;
}

export function AccountError({ reset }: { reset: () => void }) {
  const { t } = useI18n();
  return <div className="mx-auto max-w-md p-8"><p>{t("loadFailed")}</p><Button onClick={reset} className="mt-4">{t("retry")}</Button><Button asChild variant="link"><Link to="/account">{t("back")}</Link></Button></div>;
}

export function CustomerAccountPage({ page, userId }: { page: AccountPage; userId: string }) {
  const { t, lang } = useI18n();
  const { data } = useSuspenseQuery(customerAccountOptions(userId));
  const qc = useQueryClient();
  const [editing, setEditing] = useState<string | null>(null);
  const [address, setAddress] = useState("");
  const [locality, setLocality] = useState(LOCALITIES[0] ?? "Lalpur");
  const [message, setMessage] = useState("");
  const [bookingId, setBookingId] = useState("");
  const [busy, setBusy] = useState(false);
  const date = (value: string) => new Date(value).toLocaleString(lang === "hi" ? "hi-IN" : "en-IN", { dateStyle: "medium", timeStyle: "short" });
  const refresh = () => qc.invalidateQueries({ queryKey: ["customer-account", userId] });

  async function saveAddress(e: React.FormEvent) {
    e.preventDefault();
    if (address.trim().length < 5 || address.trim().length > 500) { toast.error(t("registrationInvalid")); return; }
    setBusy(true);
    try {
      const values = { full_address: address.trim(), locality, user_id: userId };
      const result = editing === "new" ? await supabase.from("addresses").insert(values) : await supabase.from("addresses").update(values).eq("id", editing ?? "").eq("user_id", userId);
      if (result.error) throw result.error;
      await refresh(); setEditing(null); toast.success(t("saved"));
    } catch { toast.error(t("actionFailed")); } finally { setBusy(false); }
  }

  async function removeAddress(id: string) {
    if (!window.confirm(t("removeAddress"))) return;
    setBusy(true);
    try {
      const { error } = await supabase.from("addresses").delete().eq("id", id).eq("user_id", userId);
      if (error) throw error;
      await refresh(); toast.success(t("saved"));
    } catch { toast.error(t("addressInUse")); } finally { setBusy(false); }
  }

  async function sendRequest(e: React.FormEvent) {
    e.preventDefault();
    if (!message.trim()) { toast.error(t("invalidMessage")); return; }
    setBusy(true);
    try {
      const { error } = await supabase.from("complaints").insert({ user_id: userId, message: message.trim(), booking_id: bookingId || null });
      if (error) throw error;
      await refresh(); setMessage(""); toast.success(t("requestSent"));
    } catch { toast.error(t("actionFailed")); } finally { setBusy(false); }
  }

  async function share(copy: boolean) {
    const url = window.location.origin;
    try {
      if (!copy && navigator.share) await navigator.share({ title: "DwarSewa", text: t("aboutCopy"), url });
      else { await navigator.clipboard.writeText(url); toast.success(t("linkCopied")); }
    } catch (error) { if (!(error instanceof DOMException && error.name === "AbortError")) toast.error(t("shareFailed")); }
  }

  let content: ReactNode;
  switch (page) {
    case "bookings": content = data.bookings.length ? <div className="space-y-4">{data.bookings.map((b) => <article key={b.id} className="rounded-lg border p-4"><div className="flex items-start justify-between gap-3"><h2 className="font-semibold">{b.packages?.name ?? t("myBookings")}</h2><span className="rounded-full bg-muted px-2 py-1 text-xs">{t(b.status)}</span></div><p className="mt-2 text-sm text-muted-foreground">{date(b.slot_time)}</p><p className="mt-2 font-semibold">₹{b.amount.toLocaleString("en-IN")} <span className="text-sm font-normal text-muted-foreground">· {t(b.payment_mode === "cash" ? "cash" : "onlinePayment")}</span></p>{b.notes && <p className="mt-2 break-words text-sm">{b.notes}</p>}<Button asChild variant="link" className="mt-2 px-0"><Link to="/account/support">{t("helpSupport")}</Link></Button></article>)}</div> : <Empty icon={ClipboardList} message="noBookings" browse />; break;
    case "ratings": content = data.reviews.length ? <div className="space-y-4">{data.reviews.map((r) => <article key={r.id} className="rounded-lg border p-4"><div className="flex items-center gap-2 text-primary"><Star className="size-5" /><strong>{r.rating} / 5</strong></div>{r.comment && <p className="mt-3 break-words">{r.comment}</p>}<p className="mt-2 text-xs text-muted-foreground">{date(r.created_at)}</p></article>)}</div> : <Empty icon={Star} message="noRatings" />; break;
    case "addresses": content = <div>
      <Button className="mb-5 w-full" variant="secondary" onClick={() => { setEditing("new"); setAddress(""); setLocality(LOCALITIES[0] ?? "Lalpur"); }}><Plus />{t("addAddress")}</Button>
      {editing && <form onSubmit={saveAddress} className="mb-6 space-y-4 border-b pb-6"><label className="block text-sm font-medium">{t("locality")}<select value={locality} onChange={(e) => setLocality(e.target.value)} className="mt-2 h-11 w-full rounded-md border bg-card px-3">{LOCALITIES.map((l) => <option key={l}>{l}</option>)}</select></label><label className="block text-sm font-medium">{t("address")}<Textarea className="mt-2" value={address} onChange={(e) => setAddress(e.target.value)} required minLength={5} maxLength={500} /></label><div className="flex gap-2"><Button disabled={busy} type="submit">{t("save")}</Button><Button type="button" variant="outline" onClick={() => setEditing(null)}>{t("cancel")}</Button></div></form>}
      {data.addresses.length ? <div className="space-y-4">{data.addresses.map((a) => <article key={a.id} className="rounded-lg border p-4"><div className="flex items-start gap-3"><MapPin className="mt-1 size-5 shrink-0 text-secondary" /><div className="min-w-0 flex-1"><h2 className="font-semibold">{a.locality}</h2><p className="mt-1 break-words text-sm text-muted-foreground">{a.full_address}</p></div></div><div className="mt-3 flex justify-end gap-2"><Button variant="ghost" size="sm" onClick={() => { setEditing(a.id); setAddress(a.full_address); setLocality(a.locality); }}><Pencil />{t("edit")}</Button><Button disabled={busy} variant="ghost" size="sm" className="text-destructive" onClick={() => removeAddress(a.id)}><Trash2 />{t("delete")}</Button></div></article>)}</div> : <Empty icon={MapPin} message="noAddresses" />}
    </div>; break;
    case "support": content = <div><form onSubmit={sendRequest} className="space-y-4"><div className="flex items-center gap-3 text-secondary"><Headphones className="size-7" /><h2 className="text-lg font-semibold">{t("supportRequest")}</h2></div><label className="block text-sm font-medium">{t("bookingOptional")}<select className="mt-2 h-11 w-full rounded-md border bg-card px-3" value={bookingId} onChange={(e) => setBookingId(e.target.value)}><option value="">{t("generalRequest")}</option>{data.bookings.map((b) => <option key={b.id} value={b.id}>{b.packages?.name} · {date(b.slot_time)}</option>)}</select></label><label className="block text-sm font-medium">{t("message")}<Textarea className="mt-2 min-h-32" value={message} onChange={(e) => setMessage(e.target.value)} required maxLength={2000} /></label><Button disabled={busy} type="submit" className="w-full" variant="secondary">{t("sendRequest")}</Button></form><h2 className="mt-8 mb-4 font-semibold">{t("myRequests")}</h2>{data.complaints.length ? <div className="space-y-3">{data.complaints.map((c) => <article key={c.id} className="rounded-lg border p-4"><p className="break-words">{c.message}</p><div className="mt-3 flex items-center justify-between gap-2 text-xs text-muted-foreground"><span>{date(c.created_at)}</span><span className="rounded-full bg-muted px-2 py-1">{t(c.status === "resolved" ? "resolved" : c.status === "closed" ? "closed" : "open")}</span></div></article>)}</div> : <p className="text-sm text-muted-foreground">{t("noRequests")}</p>}</div>; break;
    case "settings": content = <div><div className="flex items-center justify-between border-b pb-5"><h2 className="font-medium">{t("language")}</h2><LangToggle /></div><h2 className="mt-6 mb-4 font-semibold">{t("accountDetails")}</h2><p>{data.profile?.full_name}</p><p className="mt-2 text-muted-foreground">{data.profile?.phone}</p><p className="mt-3 text-sm text-muted-foreground">{t("mobileLocked")}</p><Button asChild variant="outline" className="mt-6 w-full"><Link to="/register/customer">{t("editRegistration")}</Link></Button></div>; break;
    case "about": content = <div><BrandLogo className="mx-auto w-64" /><h2 className="mt-5 text-2xl font-bold">{t("appName")}</h2><p className="mt-3 text-muted-foreground">{t("aboutCopy")}</p><Button asChild variant="secondary" className="mt-6"><Link to="/account/support">{t("helpSupport")}</Link></Button></div>; break;
    case "referral": content = <div className="py-8 text-center"><Gift className="mx-auto size-20 text-primary" /><h2 className="mt-5 text-xl font-semibold">{t("referFriends")}</h2><p className="mt-3 text-muted-foreground">{t("referSubtitle")}</p><div className="mt-6 flex justify-center gap-3"><Button variant="secondary" onClick={() => share(false)}><Share2 />{t("share")}</Button><Button variant="outline" onClick={() => share(true)}><Copy />{t("copyLink")}</Button></div><p className="mt-8 text-sm text-muted-foreground">{t("referralUnavailable")}</p></div>; break;
    case "devices": content = <Empty icon={Smartphone} message="noDevices" />; break;
    case "plans": content = <Empty icon={ClipboardCheck} message="noPlans" browse />; break;
    case "wallet": content = <Empty icon={Wallet} message="walletUnavailable" />; break;
    case "gift-cards": content = <Empty icon={Gift} message="giftsUnavailable" />; break;
    case "membership": content = <Empty icon={BadgeCheck} message="membershipsUnavailable" />; break;
    case "payments": content = <><div className="flex items-center gap-3 border-b pb-5"><CreditCard className="size-6 text-secondary" /><span>{t("cash")}</span></div><Empty icon={CreditCard} message="paymentsUnavailable" /></>; break;
  }
  return <AccountPageShell page={page}>{content}</AccountPageShell>;
}