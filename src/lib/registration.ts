import { z } from "zod";
import { LOCALITIES } from "./i18n";

export const registrationSchema = z.object({
  role: z.enum(["customer", "provider"]),
  name: z.string().trim().min(2).max(100),
  language: z.enum(["en", "hi"]),
  address: z.string().trim().min(5).max(500),
  locality: z.string().refine((v) => LOCALITIES.includes(v)),
  services: z.array(z.string().uuid()).max(30),
  areas: z.array(z.string().refine((v) => LOCALITIES.includes(v))).max(7),
  experience: z.number().int().min(0).max(60),
  bio: z.string().trim().max(1000),
}).superRefine((data, ctx) => {
  if (data.role === "provider" && (!data.services.length || !data.areas.length)) {
    ctx.addIssue({ code: z.ZodIssueCode.custom, message: "Select services and areas" });
  }
});
export type Registration = z.infer<typeof registrationSchema>;

export function trustedRegistrationPhone(user: { id: string; phone?: string; phone_confirmed_at?: string; email?: string; app_metadata: Record<string, unknown> }, development: boolean, tests: readonly { id: string; email: string; phone: string }[]) {
  if (user.phone && user.phone_confirmed_at) {
    const digits = user.phone.replace(/\D/g, "");
    if (/^91[6-9]\d{9}$/.test(digits)) return `+${digits}`;
  }
  const test = development && user.app_metadata["preview_test_account"] === true
    ? tests.find((a) => a.id === user.id && a.email === user.email) : undefined;
  if (test) return `+91${test.phone}`;
  throw new Error("Verify your mobile number first");
}