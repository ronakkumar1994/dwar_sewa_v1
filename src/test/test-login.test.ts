import { describe, expect, it } from "vitest";
import { TEST_ACCOUNTS, allowTestLogin, findTestAccount } from "@/lib/test-login";

describe("Isolated preview OTP", () => {
  it("accepts the customer test phone with the dummy OTP in development", () => {
    expect(allowTestLogin(true, "9999999999", "123456")).toBe(true);
  });
  it("accepts the provider test phone with the dummy OTP in development", () => {
    expect(allowTestLogin(true, "9999999998", "123456")).toBe(true);
  });
  it("maps each test phone to its own isolated identity", () => {
    const customer = findTestAccount("9999999999");
    const provider = findTestAccount("9999999998");
    expect(customer?.labelKey).toBe("testCustomer");
    expect(provider?.labelKey).toBe("testProvider");
    expect(provider?.id).not.toBe(customer?.id);
    expect(TEST_ACCOUNTS).toHaveLength(2);
  });
  it("never enables dummy login in production", () => {
    expect(allowTestLogin(false, "9999999999", "123456")).toBe(false);
    expect(allowTestLogin(false, "9999999998", "123456")).toBe(false);
  });
  it("does not unlock another phone with the shared code", () => {
    expect(allowTestLogin(true, "9876543210", "123456")).toBe(false);
  });
  it("rejects an incorrect OTP", () => {
    expect(allowTestLogin(true, "9999999999", "000000")).toBe(false);
    expect(allowTestLogin(true, "9999999998", "000000")).toBe(false);
  });
});
