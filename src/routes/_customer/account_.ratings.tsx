import { createFileRoute } from "@tanstack/react-router";
import { CustomerAccountPage, AccountError } from "@/components/CustomerAccountPages";
import { accountHead, customerAccountOptions } from "@/lib/customer-account";

export const Route = createFileRoute("/_customer/account_/ratings")({
  head: () => accountHead("My ratings", "Ratings you have shared for DwarSewa services."),
  loader: ({ context }) => context.queryClient.ensureQueryData(customerAccountOptions(context.userId)),
  errorComponent: AccountError,
  component: Page,
});

function Page() {
  const { userId } = Route.useRouteContext();
  return <CustomerAccountPage page="ratings" userId={userId} />;
}
