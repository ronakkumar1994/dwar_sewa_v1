import { redirect } from "@tanstack/react-router";
import { getSessionRole, homeFor } from "./auth";
export async function registrationGate(role: "customer" | "provider") {
  const result = await getSessionRole();
  if (!result || result === "expired") throw redirect({ to: "/", search: { registration: role } });
  if (result.role && result.role !== role) throw redirect({ to: homeFor(result.role) });
  return { userId: result.userId };
}