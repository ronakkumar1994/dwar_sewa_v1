import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { useI18n } from "@/lib/i18n";

export const Route = createFileRoute("/_customer/service/$id")({
  head: () => ({
    meta: [
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { title: "Service packages — DwarSewa" },
      { name: "description", content: "Compare packages, prices and durations." },
      { property: "og:title", content: "Service packages — DwarSewa" },
      { property: "og:description", content: "Compare packages, prices and durations." },
    ],
  }),
  component: ServiceDetail,
});

function ServiceDetail() {
  const { id } = Route.useParams();
  const { t, lang } = useI18n();
  const { data } = useQuery({
    queryKey: ["service", id],
    queryFn: async () =>
      (await supabase.from("services").select("*, packages(*)").eq("id", id).single()).data,
  });
  const pkgs = [...(data?.packages ?? [])].sort((a, b) => a.price - b.price);

  return (
    <main className="mx-auto min-h-screen max-w-md pb-10">
      <header className="bg-secondary px-5 pb-6 pt-5 text-secondary-foreground">
        <Link to="/home" className="text-2xl" aria-label="Back">←</Link>
        <div className="mt-3 flex items-center gap-3">
          <span className="text-5xl">{data?.icon}</span>
          <h1 className="text-2xl font-bold">{lang === "hi" && data?.name_hi ? data.name_hi : data?.name}</h1>
        </div>
      </header>
      <section className="space-y-3 px-5 pt-5">
        <h2 className="text-lg font-bold">{t("packages")}</h2>
        {pkgs.map((p) => (
          <div key={p.id} className="flex items-center justify-between rounded-2xl bg-card p-4 shadow-card">
            <div>
              <p className="font-bold">{p.name}</p>
              <p className="text-sm text-muted-foreground">⏱ {p.duration_minutes} {t("mins")}</p>
              <p className="mt-1 text-xl font-bold text-primary">₹{p.price}</p>
            </div>
            <Button className="h-11 rounded-xl px-5 font-semibold" onClick={() => toast(t("comingSoon"))}>
              {t("book")}
            </Button>
          </div>
        ))}
      </section>
    </main>
  );
}
