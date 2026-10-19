import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { Switch } from "@/components/ui/switch";
import { AccountPanel } from "@/components/AccountPanel";
import { LangToggle, useI18n } from "@/lib/i18n";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/_provider/provider")({
  head: () => ({
    meta: [
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { title: "Provider — DwarSewa" },
      { name: "description", content: "Go online, see jobs and earnings." },
      { property: "og:title", content: "Provider — DwarSewa" },
      { property: "og:description", content: "Go online, see jobs and earnings." },
    ],
  }),
  component: ProviderHome,
});

function ProviderHome() {
  const { t } = useI18n();
  const { userId } = Route.useRouteContext();
  const qc = useQueryClient();
  const [tab, setTab] = useState<"jobs" | "earnings" | "profile">("jobs");

  const { data: me } = useQuery({
    queryKey: ["provider", userId],
    queryFn: async () => (await supabase.from("providers").select("*").eq("id", userId).single()).data,
  });
  const { data: jobs = [] } = useQuery({
    queryKey: ["provider-jobs", userId],
    queryFn: async () => {
      const start = new Date(); start.setHours(0, 0, 0, 0);
      const end = new Date(start); end.setDate(end.getDate() + 1);
      return (
        await supabase.from("bookings").select("id,status,amount,slot_time,packages(name)")
          .eq("provider_id", userId).gte("slot_time", start.toISOString()).lt("slot_time", end.toISOString())
      ).data ?? [];
    },
  });
  const earnings = jobs.filter((j) => j.status === "completed").reduce((a, j) => a + j.amount, 0);
  const approved = me?.status === "approved";

  async function toggle(v: boolean) {
    if (!approved) { toast(t("pendingVerification")); return; }
    const { error } = await supabase.from("providers").update({ is_online: v }).eq("id", userId);
    if (error) { toast.error(error.message); return; }
    qc.invalidateQueries({ queryKey: ["provider", userId] });
  }

  return (
    <main className="mx-auto min-h-screen max-w-md pb-24">
      <header className="bg-secondary px-5 pb-5 pt-5 text-secondary-foreground">
        <div className="flex items-center justify-between">
          <span className="text-lg font-bold">{me?.full_name ?? t("appName")}</span>
          <LangToggle />
        </div>
        <div className="mt-4 flex items-center justify-between rounded-2xl bg-secondary-foreground/10 p-4">
          <span className="text-lg font-bold">{me?.is_online ? `🟢 ${t("online")}` : `⚪ ${t("offline")}`}</span>
          <Switch checked={!!me?.is_online} onCheckedChange={toggle} className="scale-125" aria-label="Online" />
        </div>
      </header>

      {me && me.status === "pending" && (
        <p className="mx-5 mt-4 rounded-xl bg-warning p-3 text-sm font-semibold text-warning-foreground">⏳ {t("pendingVerification")}</p>
      )}
      {me && me.status === "blocked" && (
        <p className="mx-5 mt-4 rounded-xl bg-destructive p-3 text-sm font-semibold text-destructive-foreground">{t("blocked")}</p>
      )}

      <div className="px-5 pt-5">
        {tab === "jobs" && (
          <>
            <div className="grid grid-cols-2 gap-3">
              <Stat label={t("todaysJobs")} value={String(jobs.length)} />
              <Stat label={t("todayEarnings")} value={`₹${earnings}`} />
            </div>
            {jobs.length === 0 ? (
              <div className="mt-8 rounded-2xl border-2 border-dashed p-8 text-center">
                <p className="text-5xl">📭</p>
                <p className="mt-3 text-lg font-bold">{t("noJobs")}</p>
                <p className="text-sm text-muted-foreground">{t("noJobsSub")}</p>
              </div>
            ) : (
              <div className="mt-5 space-y-3">
                {jobs.map((j) => (
                  <div key={j.id} className="rounded-2xl bg-card p-4 shadow-card">
                    <p className="font-bold">{(j.packages as { name: string } | null)?.name}</p>
                    <p className="text-sm text-muted-foreground">{new Date(j.slot_time).toLocaleTimeString()} · {j.status}</p>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
        {tab === "earnings" && (
          <div className="space-y-3">
            <div className="grid grid-cols-2 gap-3">
              <Stat label={t("todayEarnings")} value={`₹${earnings}`} />
              <Stat label={t("jobs")} value={String(me?.jobs_completed ?? 0)} />
            </div>
            <p className="text-center text-muted-foreground">{t("noEarnings")}</p>
          </div>
        )}
        {tab === "profile" && <><AccountPanel userId={userId} /><Button asChild variant="outline" className="mt-5 w-full"><Link to="/register/provider">{t("editRegistration")}</Link></Button><p className="mt-4 text-sm text-muted-foreground">{me?.areas.join(", ")} · {me?.experience_years ?? 0} {t("experience")}</p></>}
      </div>

      <nav className="fixed inset-x-0 bottom-0 mx-auto grid max-w-md grid-cols-3 border-t bg-card">
        {([["jobs", "🧰"], ["earnings", "💰"], ["profile", "👤"]] as const).map(([k, icon]) => (
          <button key={k} onClick={() => setTab(k)} className={`flex flex-col items-center py-3 text-sm font-semibold ${tab === k ? "text-primary" : "text-muted-foreground"}`}>
            <span className="text-xl">{icon}</span>
            {t(k)}
          </button>
        ))}
      </nav>
    </main>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-card p-4 shadow-card">
      <p className="text-sm text-muted-foreground">{label}</p>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}
