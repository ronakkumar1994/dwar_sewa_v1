import { createFileRoute, Link, redirect, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { LangToggle, useI18n } from "@/lib/i18n";
import { getSessionRole, homeFor } from "@/lib/auth";
import { BrandLogo } from "@/components/BrandLogo";
import { TEST_ACCOUNTS, TEST_OTP, findTestAccount } from "@/lib/test-login";
import { testLogin } from "@/lib/test-login.functions";
import { useServerFn } from "@tanstack/react-start";

export const Route = createFileRoute("/")({
  validateSearch: (s: Record<string, unknown>): { expired?: number; registration?: "customer" | "provider" } => ({ ...(s["expired"] ? { expired: 1 } : {}), ...(s["registration"] === "customer" || s["registration"] === "provider" ? { registration: s["registration"] } : {}) }),
  beforeLoad: async ({ search }) => {
    const r = await getSessionRole();
    if (r && r !== "expired") throw redirect({ to: search.registration && (!r.role || r.role === search.registration) ? (search.registration === "provider" ? "/register/provider" : "/register/customer") : r.role ? homeFor(r.role) : "/choose-role" });
  },
  head: () => ({
    meta: [
      { title: "DwarSewa — Home services in Ranchi" },
      { name: "description", content: "Book AC repair, cleaning, salon, plumber and electrician at home in Ranchi." },
      { property: "og:title", content: "DwarSewa — Home services in Ranchi" },
      { property: "og:description", content: "Trusted home services in Ranchi, in Hindi and English." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: Login,
});

function Login() {
  const { t } = useI18n();
  const { expired, registration } = Route.useSearch();
  const navigate = useNavigate();
  const loginWithTestOtp = useServerFn(testLogin);
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState<"phone" | "otp">("phone");
  const [busy, setBusy] = useState(false);
  const full = `+91${phone}`;
  const isTestAccount = import.meta.env.DEV && findTestAccount(phone) !== undefined;

  async function send() {
    if (!/^[6-9]\d{9}$/.test(phone)) { toast.error(t("invalidPhone")); return; }
    if (isTestAccount) { setOtp(""); setStep("otp"); return; }
    setBusy(true);
    const { error } = await supabase.auth.signInWithOtp({ phone: full });
    setBusy(false);
    if (error) { toast.error(error.message); return; }
    setStep("otp");
  }

  async function verify() {
    setBusy(true);
    try {
      let error;
      if (isTestAccount) {
        const { tokenHash } = await loginWithTestOtp({ data: { phone, otp } });
        ({ error } = await supabase.auth.verifyOtp({ token_hash: tokenHash, type: "magiclink" }));
      } else {
        ({ error } = await supabase.auth.verifyOtp({ phone: full, token: otp, type: "sms" }));
      }
      if (error) { toast.error(error.message); return; }
      const r = await getSessionRole();
      if (r && r !== "expired") await navigate({ to: registration && (!r.role || r.role === registration) ? (registration === "provider" ? "/register/provider" : "/register/customer") : r.role ? homeFor(r.role) : "/choose-role", replace: true });
      else toast.error(t("loginFailed"));
    } catch {
      toast.error(t("loginFailed"));
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="mx-auto flex min-h-screen max-w-md flex-col bg-background px-5 pb-8 pt-5">
      <div className="flex items-center justify-end">
        <LangToggle />
      </div>
      <div className="mt-6 mb-8">
        <BrandLogo className="mb-5 w-full" />
        <h1 className="text-3xl font-bold leading-tight">{t("tagline")}</h1>
      </div>
      <div className="mb-5 grid grid-cols-2 gap-2">
        <Button asChild variant={registration === "customer" ? "default" : "outline"} className="h-auto min-h-12 whitespace-normal text-center"><Link to="/" search={{ registration: "customer" }}>{t("customerRegistration")}</Link></Button>
        <Button asChild variant={registration === "provider" ? "default" : "outline"} className="h-auto min-h-12 whitespace-normal text-center"><Link to="/" search={{ registration: "provider" }}>{t("providerRegistration")}</Link></Button>
      </div>
      {import.meta.env.DEV && (
        <div className="mb-5 border-l-2 border-primary pl-3 text-sm text-muted-foreground">
          <p className="font-semibold text-foreground">{t("testAccess")}</p>
          {TEST_ACCOUNTS.map((a) => (
            <p key={a.phone}>
              <b className="text-foreground">{a.phone}</b> — {t(a.labelKey)}
            </p>
          ))}
          <p>{t("testCode")}: <b>{TEST_OTP}</b> · {t("testRoleNote")}</p>
          <p className="mt-1">
            <Link to="/admin" className="font-semibold text-primary underline">{t("adminAccess")}</Link>
          </p>
        </div>
      )}
      {expired && (
        <p className="mb-4 rounded-xl bg-warning p-3 text-sm text-warning-foreground">{t("sessionExpired")}</p>
      )}
      {step === "phone" ? (
        <div className="space-y-4">
          <label className="text-sm font-semibold text-muted-foreground">{t("phone")}</label>
          <div className="flex h-14 items-center gap-2 rounded-xl border bg-card px-4">
            <span className="font-semibold text-muted-foreground">+91</span>
            <Input
              aria-label={t("phone")}
              inputMode="numeric"
              autoComplete="tel-national"
              maxLength={10}
              value={phone}
              onChange={(e) => setPhone(e.target.value.replace(/\D/g, ""))}
              className="h-12 border-0 p-0 text-lg shadow-none focus-visible:ring-0"
              placeholder="98XXXXXXXX"
            />
          </div>
          <Button size="xl" onClick={send} disabled={busy}>{t("sendOtp")}</Button>
        </div>
      ) : (
        <div className="space-y-5">
          <p className="text-muted-foreground">{t(isTestAccount ? "testOtpPrompt" : "enterOtp")} <b className="text-foreground">{full}</b></p>
          <InputOTP aria-label="OTP" maxLength={6} value={otp} onChange={setOtp} autoFocus>
            <InputOTPGroup>
              {[0, 1, 2, 3, 4, 5].map((i) => (
                <InputOTPSlot key={i} index={i} className="h-12 w-12 text-lg" />
              ))}
            </InputOTPGroup>
          </InputOTP>
          <Button size="xl" onClick={verify} disabled={busy || otp.length < 6}>{t("verify")}</Button>
          <Button variant="ghost" className="w-full" onClick={() => { setStep("phone"); setOtp(""); }}>{t("changeNumber")}</Button>
        </div>
      )}
    </main>
  );
}
