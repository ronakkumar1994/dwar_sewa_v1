import { createFileRoute } from "@tanstack/react-router";
import { RegistrationForm } from "@/components/RegistrationForm";
import { registrationGate } from "@/lib/registration-gate";
export const Route = createFileRoute("/register/provider")({
  ssr: false, beforeLoad: () => registrationGate("provider"),
  head: () => ({ meta: [{ title: "Provider registration — DwarSewa" }, { name: "description", content: "Register services, coverage areas and work experience as a DwarSewa provider." }, { property: "og:title", content: "Provider registration — DwarSewa" }, { property: "og:description", content: "Join DwarSewa as a multi-service provider." }, { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" }] }),
  component: () => <RegistrationForm role="provider" userId={Route.useRouteContext().userId} />,
});