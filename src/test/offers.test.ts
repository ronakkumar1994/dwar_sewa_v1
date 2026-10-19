import { expect, it } from "vitest";
import { offerIsVisible, offerSchema } from "@/lib/offers";
const now = new Date("2026-10-19T07:19:00Z");
const active = { is_active: true, starts_at: "2026-10-18T00:00:00Z", ends_at: null };
it("shows published current offers", () => expect(offerIsVisible(active, now)).toBe(true));
it("hides draft offers", () => expect(offerIsVisible({ ...active, is_active: false }, now)).toBe(false));
it("hides future offers", () => expect(offerIsVisible({ ...active, starts_at: "2026-10-20T00:00:00Z" }, now)).toBe(false));
it("hides expired offers", () => expect(offerIsVisible({ ...active, ends_at: "2026-10-19T07:19:00Z" }, now)).toBe(false));
it("rejects offer end before start", () => expect(offerSchema.safeParse({ ...active, ends_at: "2026-10-17T00:00:00Z", title: "Offer", title_hi: "", details: "Offer details", details_hi: "", terms: "", terms_hi: "", code: "", service_id: null, sort: 0 }).success).toBe(false));