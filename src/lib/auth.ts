import { redirect } from "@tanstack/react-router";
import { supabase } from "@/integrations/supabase/client";

export type Role = "customer" | "provider" | "admin";

export const homeFor = (role: Role) =>
  role === "admin" ? "/admin" : role === "provider" ? "/provider" : "/home";

/** Restores the stored session, validates it with the server, and returns the user's role. */
export async function getSessionRole(): Promise<{ userId: string; role: Role | null } | "expired" | null> {
  const { data: s } = await supabase.auth.getSession();
  if (!s.session) return null;
  const { data, error } = await supabase.auth.getUser();
  if (error || !data.user) {
    await supabase.auth.signOut();
    return "expired";
  }
  const { data: roles } = await supabase.from("user_roles").select("role").eq("user_id", data.user.id);
  const list = (roles ?? []).map((r) => r.role as Role);
  const role = list.includes("admin") ? "admin" : (list[0] ?? null);
  return { userId: data.user.id, role };
}

/** Use in beforeLoad of a role-gated layout. */
export async function requireRole(role: Role) {
  const r = await getSessionRole();
  if (r === null) throw redirect({ to: role === "admin" ? "/admin" : "/" });
  if (r === "expired") throw redirect({ to: "/", search: { expired: 1 } });
  if (!r.role) throw redirect({ to: "/choose-role" });
  if (r.role !== role) throw redirect({ to: homeFor(r.role) });
  return { userId: r.userId };
}

export async function logout() {
  await supabase.auth.signOut();
}
