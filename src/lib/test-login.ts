export const TEST_OTP = "123456";

export type TestAccount = {
  phone: string;
  email: string;
  id: string;
  labelKey: "testCustomer" | "testProvider";
};

/** Preview-only identities: isolated, non-admin, never reachable outside development. */
export const TEST_ACCOUNTS: readonly TestAccount[] = [
  {
    phone: "9999999999",
    email: "dwarsewa-preview@example.invalid",
    id: "57a47bd6-e0e7-4c12-99cb-1f9156884b2a",
    labelKey: "testCustomer",
  },
  {
    phone: "9999999998",
    email: "dwarsewa-preview-provider@example.invalid",
    id: "732b36a0-3373-4e54-b631-da4015608e98",
    labelKey: "testProvider",
  },
];

export const findTestAccount = (phone: string) => TEST_ACCOUNTS.find((a) => a.phone === phone);

export function allowTestLogin(development: boolean, phone: string, otp: string) {
  return development && findTestAccount(phone) !== undefined && otp === TEST_OTP;
}
