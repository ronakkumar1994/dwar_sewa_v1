import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { ArrowLeft, Tag } from "lucide-react";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { useI18n, LangToggle } from "@/lib/i18n";
import offerImage from "@/assets/offers-home.jpg";
export const Route = createFileRoute("/_customer/offer/$id")({
  head: () => ({ meta: [{ title: "Offer details — DwarSewa" }, { name: "description", content: "Current DwarSewa offer details and terms." }, { property: "og:title", content: "Offer details — DwarSewa" }, { property: "og:description", content: "Explore DwarSewa service offers." }, { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" }] }),
  component: OfferDetails,
});
function OfferDetails() {
  const { id } = Route.useParams(); const { t, lang } = useI18n();
  const { data: offer, isLoading } = useQuery({ queryKey: ["offer", id], queryFn: async () => {
    const now = new Date().toISOString();
    const { data, error } = await supabase.from("offers").select("*").eq("id", id).eq("is_active", true).lte("starts_at", now).or(`ends_at.is.null,ends_at.gt.${now}`).maybeSingle();
    if (error) throw error; return data;
  }});
  return <main className="mx-auto min-h-screen max-w-md pb-8"><header className="flex items-center justify-between px-5 py-4"><Button asChild variant="ghost" size="icon"><Link to="/home" aria-label={t("previous")}><ArrowLeft /></Link></Button><LangToggle /></header>{isLoading ? <p className="px-5">…</p> : !offer ? <p className="px-5">{t("noOffers")}</p> : <>
    <img src={offerImage} width={1280} height={720} alt="" className="aspect-video w-full object-cover" />
    <div className="px-5"><h1 className="mt-6 text-3xl font-bold">{lang === "hi" ? offer.title_hi || offer.title : offer.title}</h1><p className="mt-4 whitespace-pre-wrap break-words">{lang === "hi" ? offer.details_hi || offer.details : offer.details}</p>
      {offer.code && <div className="mt-5 flex items-center gap-3 border-y py-4"><Tag className="text-primary" /><div><p className="text-sm text-muted-foreground">{t("offerCode")}</p><p className="font-bold">{offer.code}</p></div></div>}
      {offer.ends_at && <p className="mt-4 text-sm text-muted-foreground">{t("endDate")}: {new Date(offer.ends_at).toLocaleString(lang === "hi" ? "hi-IN" : "en-IN")}</p>}
      {(offer.terms || offer.terms_hi) && <><h2 className="mt-6 text-lg font-bold">{t("terms")}</h2><p className="mt-2 whitespace-pre-wrap break-words text-sm text-muted-foreground">{lang === "hi" ? offer.terms_hi || offer.terms : offer.terms}</p></>}
      {offer.service_id && <Button asChild size="xl" className="mt-6"><Link to="/service/$id" params={{ id: offer.service_id }}>{t("viewService")}</Link></Button>}
    </div></>}
  </main>;
}