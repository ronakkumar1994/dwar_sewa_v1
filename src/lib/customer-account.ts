import { queryOptions } from "@tanstack/react-query";
import { supabase } from "@/integrations/supabase/client";

export const customerAccountOptions = (userId: string) => queryOptions({
  queryKey: ["customer-account", userId],
  queryFn: async () => {
    const results = await Promise.all([
      supabase.from("profiles").select("*").eq("id", userId).single(),
      supabase.from("bookings").select("*,packages(name)").eq("customer_id", userId).order("created_at", { ascending: false }),
      supabase.from("addresses").select("*").eq("user_id", userId),
      supabase.from("reviews").select("*").eq("customer_id", userId).order("created_at", { ascending: false }),
      supabase.from("complaints").select("*").eq("user_id", userId).order("created_at", { ascending: false }),
    ]);
    for (const result of results) if (result.error) throw result.error;
    return { profile: results[0].data, bookings: results[1].data ?? [], addresses: results[2].data ?? [], reviews: results[3].data ?? [], complaints: results[4].data ?? [] };
  },
});

export function accountHead(title: string, description: string) {
  return { meta: [
    { title: `${title} — DwarSewa` },
    { name: "description", content: description },
    { property: "og:title", content: `${title} — DwarSewa` },
    { property: "og:description", content: description },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ] };
}