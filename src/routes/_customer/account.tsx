import { createFileRoute, Link } from "@tanstack/react-router";
import { AccountPanel } from "@/components/AccountPanel";
import { useI18n } from "@/lib/i18n";

export const Route = createFileRoute("/_customer/account")({
  head: () => ({
    meta: [
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { title: "Account — DwarSewa" },
      { name: "description", content: "Your DwarSewa account." },
      { property: "og:title", content: "Account — DwarSewa" },
      { property: "og:description", content: "Your DwarSewa account." },
    ],
  }),
  component: Account,
});

function Account() {
  const { t } = useI18n();
  const { userId } = Route.useRouteContext();
  return (
    <main className="mx-auto min-h-screen max-w-md bg-card pb-8">
      <header className="flex items-center gap-3 px-5 py-5">
        <Link to="/home" className="flex size-10 items-center justify-center rounded-full border text-xl" aria-label={t("back")}>←</Link>
        <h1 className="text-lg font-semibold">{t("profile")}</h1>
      </header>
      <AccountPanel userId={userId} />
    </main>
  );
}
