import { createFileRoute, Outlet } from "@tanstack/react-router";
import { requireRole } from "@/lib/auth";

export const Route = createFileRoute("/_customer")({
  ssr: false,
  beforeLoad: () => requireRole("customer"),
  component: () => <Outlet />,
});
