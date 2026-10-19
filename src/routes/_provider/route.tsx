import { createFileRoute, Outlet } from "@tanstack/react-router";
import { requireRole } from "@/lib/auth";

export const Route = createFileRoute("/_provider")({
  ssr: false,
  beforeLoad: () => requireRole("provider"),
  component: () => <Outlet />,
});
