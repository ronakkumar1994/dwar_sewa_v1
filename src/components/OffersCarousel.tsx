import { useEffect, useState } from "react";
import { Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import useEmblaCarousel from "embla-carousel-react";
import { ArrowLeft, ArrowRight, Tag } from "lucide-react";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "./ui/button";
import { useI18n } from "@/lib/i18n";
import offerImage from "@/assets/offers-home.jpg";

export function OffersCarousel() {
  const { t, lang } = useI18n();
  const [ref, api] = useEmblaCarousel({ loop: true });
  const [index, setIndex] = useState(0);
  const [paused, setPaused] = useState(false);
  const { data: offers = [] } = useQuery({ queryKey: ["published-offers"], refetchInterval: 60000, queryFn: async () => {
    const now = new Date().toISOString();
    const { data, error } = await supabase.from("offers").select("*").eq("is_active", true).lte("starts_at", now).or(`ends_at.is.null,ends_at.gt.${now}`).order("sort");
    if (error) throw error; return data ?? [];
  }});
  useEffect(() => {
    if (!api) return;
    const changed = () => setIndex(api.selectedScrollSnap());
    api.on("select", changed); api.on("reInit", changed);
    return () => { api.off("select", changed); api.off("reInit", changed); };
  }, [api]);
  useEffect(() => {
    if (!api || paused || offers.length < 2 || window.matchMedia("(prefers-reduced-motion: reduce)").matches) return;
    const timer = window.setInterval(() => api.scrollNext(), 5000);
    return () => window.clearInterval(timer);
  }, [api, paused, offers.length]);
  return <section className="mt-7" aria-label={t("offers")} onMouseEnter={() => setPaused(true)} onMouseLeave={() => setPaused(false)} onFocusCapture={() => setPaused(true)} onBlurCapture={(e) => { if (!e.currentTarget.contains(e.relatedTarget)) setPaused(false); }}>
    <div className="mb-3 flex items-center justify-between gap-2"><h2 className="text-lg font-bold">{t("offers")}</h2>{offers.length > 1 && <div className="flex gap-1"><Button variant="ghost" size="icon" aria-label={t("previous")} title={t("previous")} onClick={() => api?.scrollPrev()}><ArrowLeft /></Button><Button variant="ghost" size="icon" aria-label={t("next")} title={t("next")} onClick={() => api?.scrollNext()}><ArrowRight /></Button></div>}</div>
    {offers.length ? <><div className="overflow-hidden" ref={ref}><div className="flex">{offers.map((o) => <div key={o.id} className="min-w-0 flex-[0_0_100%] pr-2"><Link to="/offer/$id" params={{ id: o.id }} className="block overflow-hidden rounded-lg border bg-card">
      <img src={offerImage} loading="lazy" width={1280} height={720} alt="" className="aspect-[16/7] w-full object-cover" />
      <div className="p-4"><div className="flex items-start justify-between gap-3"><h3 className="text-xl font-bold leading-tight">{lang === "hi" ? o.title_hi || o.title : o.title}</h3><Tag className="h-5 w-5 shrink-0 text-primary" /></div><p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{lang === "hi" ? o.details_hi || o.details : o.details}</p><p className="mt-3 flex items-center gap-2 text-sm font-semibold text-primary">{t("offerDetails")} <ArrowRight className="h-4 w-4" /></p></div>
    </Link></div>)}</div></div><div className="mt-2 flex justify-center gap-1">{offers.map((o, i) => <Button key={o.id} variant="ghost" size="icon" className="h-7 w-7" aria-label={`${t("offers")} ${i + 1}`} aria-pressed={index === i} onClick={() => api?.scrollTo(i)}><span className={`h-2 w-2 rounded-full ${index === i ? "bg-primary" : "bg-border"}`} /></Button>)}</div></> : <div className="flex min-h-28 items-center gap-4 border-y py-6 text-muted-foreground"><Tag className="h-8 w-8" /><p className="text-sm">{t("noOffers")}</p></div>}
  </section>;
}