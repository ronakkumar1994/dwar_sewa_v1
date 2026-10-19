import { createServerFn } from "@tanstack/react-start";
import { requireSupabaseAuth } from "@/integrations/supabase/auth-middleware";
import { registrationSchema, trustedRegistrationPhone } from "./registration";
import { TEST_ACCOUNTS } from "./test-login";

export const registerAccount = createServerFn({ method: "POST" })
  .middleware([requireSupabaseAuth])
  .inputValidator((data) => registrationSchema.parse(data))
  .handler(async ({ data, context }) => {
    const { data: auth, error } = await context.supabase.auth.getUser();
    if (error || !auth.user || auth.user.id !== context.userId) throw new Error("Sign in again");
    const phone = trustedRegistrationPhone(auth.user, import.meta.env.DEV, TEST_ACCOUNTS);
    const { data: roles, error: rolesError } = await context.supabase.from("user_roles").select("role").eq("user_id", context.userId);
    if (rolesError || roles?.some((r) => r.role !== data.role)) throw new Error("This mobile already has another account type");
    const test = TEST_ACCOUNTS.find((a) => a.id === context.userId);
    if (import.meta.env.DEV && test && ((test.labelKey === "testProvider") !== (data.role === "provider"))) throw new Error("Use the matching preview account");
    const { supabaseAdmin } = await import("@/integrations/supabase/client.server");
    const result = await supabaseAdmin.rpc("complete_registration", {
      _user_id: context.userId, _phone: phone, _role: data.role, _name: data.name,
      _language: data.language, _address: data.address, _locality: data.locality,
      _services: [...new Set(data.services)], _areas: [...new Set(data.areas)], _experience: data.experience, _bio: data.bio,
    });
    if (result.error) throw new Error("Registration could not be saved. Check your details or use your existing account.");
    return { role: data.role };
  });