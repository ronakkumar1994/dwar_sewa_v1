import { createServerFn } from "@tanstack/react-start";
import { z } from "zod";
import { allowTestLogin, findTestAccount } from "./test-login";

/** Never authorizes real phone accounts; production rejects before loading admin access. */
export const testLogin = createServerFn({ method: "POST" })
  .inputValidator((input) => z.object({ phone: z.string(), otp: z.string() }).parse(input))
  .handler(async ({ data }) => {
    if (!allowTestLogin(import.meta.env.DEV, data.phone, data.otp)) {
      throw new Error("Test login is unavailable or the test code is incorrect.");
    }
    const account = findTestAccount(data.phone);
    if (!account) throw new Error("Test account is not configured.");
    const { supabaseAdmin } = await import("@/integrations/supabase/client.server");
    const { data: found } = await supabaseAdmin.auth.admin.getUserById(account.id);
    let user = found.user;
    if (!user) {
      const { data: created, error } = await supabaseAdmin.auth.admin.createUser({
        id: account.id,
        email: account.email,
        email_confirm: true,
        app_metadata: { preview_test_account: true },
      });
      if (error) throw new Error("Could not prepare the test account.");
      user = created.user;
    }
    if (!user || user.email !== account.email || user.app_metadata["preview_test_account"] !== true) {
      throw new Error("Test account identity mismatch.");
    }
    const { data: roles, error: roleError } = await supabaseAdmin
      .from("user_roles")
      .select("role")
      .eq("user_id", account.id);
    if (roleError || roles?.some((r) => r.role === "admin")) {
      throw new Error("Test access is not allowed for this account.");
    }
    const { data: link, error } = await supabaseAdmin.auth.admin.generateLink({
      type: "magiclink",
      email: account.email,
    });
    if (error || !link.properties?.hashed_token || link.user.id !== account.id) {
      throw new Error("Could not start the test session.");
    }
    return { tokenHash: link.properties.hashed_token };
  });
