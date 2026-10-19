import { createFileRoute } from "@tanstack/react-router";
import { RegistrationForm } from "@/components/RegistrationForm";
import { registrationGate } from "@/lib/registration-gate";
export const Route = createFileRoute("/register/customer")({
  ssr: false, beforeLoad: () => registrationGate("customer"),
  head: () => ({ meta: [{ title: "Customer registration — DwarSewa" }, { name: "description", content: "Register your verified mobile and home address with DwarSewa." }, { property: "og:title", content: "Customer registration — DwarSewa" }, { property: "og:description", content: "Create your DwarSewa customer account." }, { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" }] }),
  component: () => <RegistrationForm role="customer" userId={Route.useRouteContext().userId} />,
});