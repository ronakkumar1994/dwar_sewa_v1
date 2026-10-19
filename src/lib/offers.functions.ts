import { createServerFn } from "@tanstack/react-start";
import { z } from "zod";
import { requireSupabaseAuth } from "@/integrations/supabase/auth-middleware";
import { offerSchema } from "./offers";

export const saveOffer = createServerFn({ method: "POST" }).middleware([requireSupabaseAuth])
  .inputValidator((data) => z.object({ id: z.string().uuid().optional(), offer: offerSchema }).parse(data))
  .handler(async ({ data, context }) => {
    const { data: admin, error } = await context.supabase.rpc("has_role", { _user_id: context.userId, _role: "admin" });
    if (error || !admin) throw new Error("Forbidden");
    const result = data.id ? await context.supabase.from("offers").update(data.offer).eq("id", data.id).select("id").single()
      : await context.supabase.from("offers").insert(data.offer).select("id").single();
    if (result.error) throw new Error("Offer could not be saved");
    return result.data;
  });