import { createFileRoute, redirect, Link } from "@tanstack/react-router";
import { Button } from "@/components/ui/button";
import { LangToggle, useI18n } from "@/lib/i18n";
import { getSessionRole, homeFor } from "@/lib/auth";

export const Route = createFileRoute("/choose-role")({
  ssr: false,
  beforeLoad: async () => {
    const r = await getSessionRole();
    if (r === null) throw redirect({ to: "/" });
    if (r === "expired") throw redirect({ to: "/", search: { expired: 1 } });
    if (r.role) throw redirect({ to: homeFor(r.role) });
    return { userId: r.userId };
  },
  head: () => ({
    meta: [
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { title: "Get started — DwarSewa" },
      { name: "description", content: "Choose whether you need a service or provide services." },
      { property: "og:title", content: "Get started — DwarSewa" },
      { property: "og:description", content: "Choose how you use DwarSewa." },
    ],
  }),
  component: ChooseRole,
});

function ChooseRole() {
  const { t } = useI18n();

  return (
    <main className="mx-auto min-h-screen max-w-md px-5 py-5">
      <div className="flex justify-end"><LangToggle /></div>
      <h1 className="mt-8 text-2xl font-bold">{t("whoAreYou")}</h1>
      <div className="mt-6 space-y-4">
        {([
          ["customer", "🏠", t("needService"), t("needServiceSub")],
          ["provider", "🧰", t("provideService"), t("provideServiceSub")],
        ] as const).map(([role, icon, title, sub]) => (
          <Button asChild variant="outline"
            key={role}
            className="flex h-auto w-full items-center justify-start gap-4 whitespace-normal rounded-lg border-2 bg-card p-5 text-left"
          >
            <Link to={role === "provider" ? "/register/provider" : "/register/customer"}>
            <span className="text-4xl">{icon}</span>
            <span>
              <span className="block text-lg font-bold">{title}</span>
              <span className="text-sm text-muted-foreground">{sub}</span>
            </span>
            </Link>
          </Button>
        ))}
      </div>
    </main>
  );
}
