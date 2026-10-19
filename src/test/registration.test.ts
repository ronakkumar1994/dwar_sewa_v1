import { describe, expect, it } from "vitest";
import { registrationSchema, trustedRegistrationPhone } from "@/lib/registration";
import { TEST_ACCOUNTS } from "@/lib/test-login";
const valid = { role: "provider", name: "Test Provider", language: "en", address: "Lalpur Ranchi", locality: "Lalpur", services: ["11111111-0000-0000-0000-000000000001", "11111111-0000-0000-0000-000000000005"], areas: ["Lalpur"], experience: 2, bio: "" };
describe("Registration rules", () => {
  it("accepts a provider offering multiple services", () => { expect(registrationSchema.parse(valid).services).toHaveLength(2); });
  it("requires provider service selection", () => { expect(registrationSchema.safeParse({ ...valid, services: [] }).success).toBe(false); });
  it("requires provider coverage areas", () => { expect(registrationSchema.safeParse({ ...valid, areas: [] }).success).toBe(false); });
  it("requires full address for customers", () => { expect(registrationSchema.safeParse({ ...valid, role: "customer", address: "" }).success).toBe(false); });
  it("only accepts a verified mobile identity", () => { expect(() => trustedRegistrationPhone({ id: "other", phone: "919999999999", app_metadata: {} }, false, TEST_ACCOUNTS)).toThrow(); });
  it("normalizes verified Indian numbers", () => { expect(trustedRegistrationPhone({ id: "other", phone: "919876543210", phone_confirmed_at: "2026-10-19", app_metadata: {} }, false, TEST_ACCOUNTS)).toBe("+919876543210"); });
  it("rejects preview identity in production", () => { const a = TEST_ACCOUNTS[0]; if (!a) throw new Error("Missing test identity"); expect(() => trustedRegistrationPhone({ id: a.id, email: a.email, app_metadata: { preview_test_account: true } }, false, TEST_ACCOUNTS)).toThrow(); });
});