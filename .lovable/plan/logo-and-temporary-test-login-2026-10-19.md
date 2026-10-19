# Logo and temporary test login

- Replace the current logo with your uploaded DwarSewa logo, including the loading screen and browser/app icon.
- Add a dedicated test phone number and dummy OTP, shown on the login page while testing.
- Use a real, isolated test account so role selection and saved data work normally; never unlock an existing account with the dummy code.
- Keep dummy login restricted to the development preview, disabled in published builds. Leave the existing SMS login ready for a provider later.
- Verify login, role selection, and logo display.

## Technical details
Use a development-only server function to issue a genuine session for the fixed test account. Existing role permissions remain unchanged.