<!-- LOVABLE:BEGIN -->
> [!IMPORTANT]
> This project is connected to [Lovable](https://lovable.dev). Avoid rewriting
> published git history — force pushing, or rebasing/amending/squashing commits
> that are already pushed — as it rewrites history on Lovable's side and the
> user will likely lose their project history.
>
> Commits you push to the connected branch sync back to Lovable and show up in
> the editor, so keep the branch in a working state.
<!-- LOVABLE:END -->

- Role-gated pages live under pathless layouts `_customer/`, `_provider/` (ssr:false, `requireRole` in src/lib/auth.ts); `/admin` handles its own login. Why: session lives in phone storage, so gating must run client-side with a splash.
- Roles live in `user_roles`; authenticated registration derives a verified phone server-side and calls the service-only atomic `complete_registration` RPC, serializing role selection and reserving mobile identity in `account_phones`; legacy `choose_role` rejects requests. Why: prevent cross-role accounts, client phone spoofing and partial registration.
- Customer/provider registration pages reuse RegistrationForm and allow existing matching-role accounts to complete details without resetting provider approval. Why: support both initial onboarding and legacy preview accounts.
- Offers are admin-managed through a validated authenticated server function and read through active/date-scoped policies; customer carousel opens a dedicated detail page. Why: prevent customers from publishing or reading unpublished promotions.
- UI strings go through `src/lib/i18n.tsx` (`t()`), EN + HI. Why: every screen must support the language toggle.
- Temporary OTP access uses a development-gated server function and one fixed isolated non-admin identity per testable role (customer, provider), each mapped to its own preview phone number; production rejects it before privileged access. Why: testing must exercise real permissions without unlocking real phone accounts.
- Reuse BrandLogo for displayed branding and derive public icons from the same uploaded asset. Why: keep branding consistent across login, loading and installed app views.
- Customer account subpages use un-nested `account_.*` route files and shared CustomerAccountPages with owner-scoped cached reads; unavailable commercial products remain explicit empty states. Why: preserve full-page navigation without inventing balances, plans or reward rules.
