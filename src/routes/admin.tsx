import { createFileRoute, redirect, useNavigate, useRouter } from "@tanstack/react-router";
import type { FormEvent } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { getSessionRole, homeFor } from "@/lib/auth";
import { OfferManager } from "@/components/OfferManager";

export const Route = createFileRoute("/admin")({
  ssr: false,
  beforeLoad: async () => {
    const r = await getSessionRole();
    if (r && r !== "expired" && r.role === "admin") return { isAdmin: true as const };
    if (r && r !== "expired" && r.role) {
      throw redirect({ to: homeFor(r.role) });
    }
    return { isAdmin: false as const, expired: r === "expired" };
  },
  head: () => ({
    meta: [
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { title: "Admin — DwarSewa" },
      { name: "description", content: "DwarSewa operations dashboard." },
      { name: "robots", content: "noindex" },
      { property: "og:title", content: "Admin — DwarSewa" },
      { property: "og:description", content: "DwarSewa operations dashboard." },
    ],
  }),
  component: AdminPage,
});

function AdminPage() {
  const ctx = Route.useRouteContext();
  return ctx.isAdmin ? <Dashboard /> : <AdminLogin expired={"expired" in ctx && !!ctx.expired} />;
}

function AdminLogin({ expired }: { expired: boolean }) {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(e: FormEvent) {
    e.preventDefault();
    setBusy(true);
    const { error } = await supabase.auth.signInWithPassword({ email, password });
    if (error) { setBusy(false); { toast.error(error.message); return; } }
    const r = await getSessionRole();
    setBusy(false);
    if (!r || r === "expired" || r.role !== "admin") {
      await supabase.auth.signOut();
      { toast.error("This account is not an admin."); return; }
    }
    router.invalidate();
  }
  return (
    <main className="mx-auto flex min-h-screen max-w-sm flex-col justify-center px-5">
      <h1 className="text-2xl font-bold">Admin login</h1>
      {expired && <p className="mt-3 rounded-xl bg-warning p-3 text-sm text-warning-foreground">Session expired. Please log in again.</p>}
      <form onSubmit={submit} className="mt-6 space-y-3">
        <Input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} className="h-12" required />
        <Input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} className="h-12" required />
        <Button size="xl" disabled={busy}>Log in</Button>
      </form>
    </main>
  );
}

type ProviderStatus = "pending" | "approved" | "blocked";

function Dashboard() {
  const qc = useQueryClient();
  const navigate = useNavigate();
  const { data: stats } = useQuery({
    queryKey: ["admin-stats"],
    queryFn: async () => {
      const start = new Date(); start.setHours(0, 0, 0, 0);
      const { data } = await supabase.from("bookings").select("status,amount,provider_id").gte("created_at", start.toISOString());
      const b = data ?? [];
      return {
        today: b.length,
        assigned: b.filter((x) => x.provider_id).length,
        unassigned: b.filter((x) => !x.provider_id && x.status !== "cancelled").length,
        revenue: b.filter((x) => x.status === "completed").reduce((a, x) => a + x.amount, 0),
      };
    },
  });
  const { data: providers = [] } = useQuery({
    queryKey: ["admin-providers"],
    queryFn: async () => (await supabase.from("providers").select("*").order("created_at", { ascending: false })).data ?? [],
  });

  async function setStatus(id: string, status: ProviderStatus) {
    const { error } = await supabase.from("providers")
      .update({ status, is_verified: status === "approved", ...(status !== "approved" ? { is_online: false } : {}) })
      .eq("id", id);
    if (error) { toast.error(error.message); return; }
    toast.success(`Provider ${status}`);
    qc.invalidateQueries({ queryKey: ["admin-providers"] });
  }

  async function out() {
    await qc.cancelQueries(); qc.clear();
    await supabase.auth.signOut();
    navigate({ to: "/admin", replace: true });
  }

  const cards = [
    ["Bookings today", stats?.today ?? 0],
    ["Auto-assigned", stats?.assigned ?? 0],
    ["Unassigned", stats?.unassigned ?? 0],
    ["Revenue today", `₹${stats?.revenue ?? 0}`],
  ] as const;

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-5 py-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">DwarSewa · Admin</h1>
        <Button variant="outline" onClick={out}>Logout</Button>
      </div>
      <div className="mt-6 grid grid-cols-2 gap-3 md:grid-cols-4">
        {cards.map(([l, v]) => (
          <div key={l} className="rounded-2xl bg-card p-4 shadow-card">
            <p className="text-sm text-muted-foreground">{l}</p>
            <p className="text-3xl font-bold">{v}</p>
          </div>
        ))}
      </div>
      <h2 className="mt-8 mb-3 text-lg font-bold">Providers</h2>
      <div className="space-y-3">
        {providers.map((p) => (
          <div key={p.id} className="flex flex-wrap items-center justify-between gap-3 rounded-2xl bg-card p-4 shadow-card">
            <div>
              <p className="font-bold">{p.full_name ?? "Unnamed"} <span className="ml-2 rounded-full bg-muted px-2 py-0.5 text-xs font-semibold uppercase">{p.status}</span></p>
              <p className="text-sm text-muted-foreground">{p.phone} · {p.areas.join(", ") || "No areas"} · ⭐ {p.rating} · {p.jobs_completed} jobs {p.is_online ? "· 🟢 online" : ""}</p>
            </div>
            <div className="flex gap-2">
              <Button size="sm" className="bg-success text-success-foreground hover:bg-success/90" disabled={p.status === "approved"} onClick={() => setStatus(p.id, "approved")}>Approve</Button>
              <Button size="sm" variant="outline" disabled={p.status === "pending"} onClick={() => setStatus(p.id, "pending")}>Reject</Button>
              <Button size="sm" variant="destructive" disabled={p.status === "blocked"} onClick={() => setStatus(p.id, "blocked")}>Block</Button>
            </div>
          </div>
        ))}
      </div>
      <OfferManager />
    </main>
  );
}
