import { createFileRoute } from "@tanstack/react-router";
import { CustomerAccountPage, AccountError } from "@/components/CustomerAccountPages";
import { accountHead, customerAccountOptions } from "@/lib/customer-account";

export const Route = createFileRoute("/_customer/account_/devices")({
  head: () => accountHead("My devices", "Your DwarSewa home device account."),
  loader: ({ context }) => context.queryClient.ensureQueryData(customerAccountOptions(context.userId)),
  errorComponent: AccountError,
  component: Page,
});

function Page() {
  const { userId } = Route.useRouteContext();
  return <CustomerAccountPage page="devices" userId={userId} />;
}
