import { z } from "zod";
export const offerSchema = z.object({
  title: z.string().trim().min(2).max(120), title_hi: z.string().trim().max(120),
  details: z.string().trim().min(5).max(3000), details_hi: z.string().trim().max(3000),
  terms: z.string().trim().max(2000), terms_hi: z.string().trim().max(2000),
  code: z.string().trim().max(40), service_id: z.string().uuid().nullable(),
  starts_at: z.string().datetime(), ends_at: z.string().datetime().nullable(),
  is_active: z.boolean(), sort: z.number().int().min(0).max(999),
}).refine((o) => !o.ends_at || new Date(o.ends_at) > new Date(o.starts_at), { message: "End must follow start" });
export function offerIsVisible(offer: { is_active: boolean; starts_at: string; ends_at: string | null }, now: Date) {
  return offer.is_active && new Date(offer.starts_at) <= now && (!offer.ends_at || new Date(offer.ends_at) > now);
}