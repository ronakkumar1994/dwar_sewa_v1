import { createContext, useContext, useEffect, useState, type ReactNode } from "react";

export type Lang = "en" | "hi";

const dict = {
  home: { en: "Home", hi: "होम" },
  forYourHome: { en: "For your home", hi: "आपके घर के लिए" },
  spotlight: { en: "In the spotlight", hi: "खास आपके लिए" },
  mostBooked: { en: "Service packages", hi: "सेवा पैकेज" },
  homeCare: { en: "A little care. A happier home.", hi: "थोड़ी देखभाल। खुशहाल घर।" },
  homeCareSub: { en: "Cleaning, repairs & salon at your doorstep", hi: "सफ़ाई, मरम्मत और सैलून आपके दरवाज़े पर" },
  explore: { en: "Explore", hi: "देखें" },
  seeAll: { en: "See all", hi: "सभी देखें" },
  fromPrice: { en: "From", hi: "शुरुआत" },
  noServices: { en: "No services found", hi: "कोई सेवा नहीं मिली" },
  allBookings: { en: "All", hi: "सभी" },
  back: { en: "Back", hi: "वापस" },
  incompleteProfile: { en: "Incomplete profile", hi: "प्रोफ़ाइल अधूरी है" },
  complete: { en: "Complete", hi: "पूरा करें" },
  myBookings: { en: "My bookings", hi: "मेरी बुकिंग" },
  devices: { en: "My devices", hi: "मेरे उपकरण" },
  helpSupport: { en: "Help & support", hi: "सहायता और सपोर्ट" },
  myPlans: { en: "My plans", hi: "मेरे प्लान" },
  wallet: { en: "Wallet", hi: "वॉलेट" },
  giftCards: { en: "Gift cards", hi: "गिफ़्ट कार्ड" },
  memberships: { en: "Passes & membership", hi: "पास और सदस्यता" },
  myRating: { en: "My ratings", hi: "मेरी रेटिंग" },
  manageAddresses: { en: "Manage addresses", hi: "पते प्रबंधित करें" },
  paymentMethods: { en: "Manage payment methods", hi: "भुगतान के तरीके" },
  settings: { en: "Settings", hi: "सेटिंग्स" },
  aboutDwarSewa: { en: "About DwarSewa", hi: "द्वार सेवा के बारे में" },
  referFriends: { en: "Share DwarSewa", hi: "द्वार सेवा साझा करें" },
  referSubtitle: { en: "Home services for your friends and family.", hi: "आपके दोस्तों और परिवार के लिए घरेलू सेवाएँ।" },
  referNow: { en: "Invite friends", hi: "दोस्तों को बुलाएँ" },
  share: { en: "Share", hi: "साझा करें" },
  copyLink: { en: "Copy link", hi: "लिंक कॉपी करें" },
  linkCopied: { en: "Link copied", hi: "लिंक कॉपी हो गया" },
  shareFailed: { en: "Unable to share. Please try again.", hi: "साझा नहीं हुआ। फिर कोशिश करें।" },
  noBookings: { en: "No bookings yet", hi: "अभी कोई बुकिंग नहीं है" },
  browseServices: { en: "Browse services", hi: "सेवाएँ देखें" },
  noRatings: { en: "No ratings yet", hi: "अभी कोई रेटिंग नहीं है" },
  noAddresses: { en: "No saved addresses", hi: "कोई पता सेव नहीं है" },
  addAddress: { en: "Add address", hi: "पता जोड़ें" },
  delete: { en: "Delete", hi: "हटाएँ" },
  save: { en: "Save", hi: "सेव करें" },
  loading: { en: "Loading…", hi: "लोड हो रहा है…" },
  loadFailed: { en: "Could not load your details", hi: "आपकी जानकारी लोड नहीं हुई" },
  retry: { en: "Try again", hi: "फिर कोशिश करें" },
  noDevices: { en: "Device registration is not available yet", hi: "उपकरण रजिस्ट्रेशन अभी उपलब्ध नहीं है" },
  noPlans: { en: "No service plans available yet", hi: "अभी कोई सेवा प्लान उपलब्ध नहीं है" },
  walletUnavailable: { en: "Wallet payments are not available yet", hi: "वॉलेट भुगतान अभी उपलब्ध नहीं है" },
  giftsUnavailable: { en: "Gift cards are not available yet", hi: "गिफ़्ट कार्ड अभी उपलब्ध नहीं हैं" },
  membershipsUnavailable: { en: "Memberships are not available yet", hi: "सदस्यता अभी उपलब्ध नहीं है" },
  paymentsUnavailable: { en: "Saved payment methods are not available yet", hi: "सेव किए भुगतान के तरीके अभी उपलब्ध नहीं हैं" },
  referralUnavailable: { en: "Referral rewards are not available yet", hi: "रेफ़रल पुरस्कार अभी उपलब्ध नहीं हैं" },
  message: { en: "Message", hi: "संदेश" },
  sendRequest: { en: "Send request", hi: "अनुरोध भेजें" },
  requestSent: { en: "Your request has been sent", hi: "आपका अनुरोध भेज दिया गया है" },
  myRequests: { en: "My support requests", hi: "मेरे सहायता अनुरोध" },
  noRequests: { en: "No support requests", hi: "कोई सहायता अनुरोध नहीं है" },
  bookingOptional: { en: "Booking (optional)", hi: "बुकिंग (वैकल्पिक)" },
  generalRequest: { en: "General request", hi: "सामान्य अनुरोध" },
  invalidMessage: { en: "Enter your message", hi: "अपना संदेश लिखें" },
  actionFailed: { en: "Could not save. Please try again.", hi: "सेव नहीं हुआ। फिर कोशिश करें।" },
  removeAddress: { en: "Delete this address?", hi: "यह पता हटाएँ?" },
  addressInUse: { en: "This address could not be deleted. It may be linked to a booking.", hi: "यह पता नहीं हट सका। यह किसी बुकिंग से जुड़ा हो सकता है।" },
  cash: { en: "Cash", hi: "नकद" },
  onlinePayment: { en: "Online", hi: "ऑनलाइन" },
  pending: { en: "Pending", hi: "लंबित" },
  assigned: { en: "Assigned", hi: "प्रदाता नियुक्त" },
  accepted: { en: "Accepted", hi: "स्वीकार किया गया" },
  on_the_way: { en: "On the way", hi: "रास्ते में" },
  started: { en: "Started", hi: "शुरू" },
  completed: { en: "Completed", hi: "पूरा हुआ" },
  cancelled: { en: "Cancelled", hi: "रद्द" },
  open: { en: "Open", hi: "खुला" },
  resolved: { en: "Resolved", hi: "हल हो गया" },
  closed: { en: "Closed", hi: "बंद" },
  supportRequest: { en: "Support request", hi: "सहायता अनुरोध" },
  accountDetails: { en: "Account details", hi: "खाते की जानकारी" },
  mobileLocked: { en: "Your verified mobile number cannot be changed.", hi: "आपका सत्यापित मोबाइल नंबर बदला नहीं जा सकता।" },
  aboutCopy: { en: "DwarSewa brings home services to your doorstep in Ranchi.", hi: "द्वार सेवा रांची में आपके दरवाज़े तक घरेलू सेवाएँ पहुँचाता है।" },
  register: { en: "Register", hi: "रजिस्टर करें" },
  customerRegistration: { en: "Customer registration", hi: "ग्राहक रजिस्ट्रेशन" },
  providerRegistration: { en: "Provider registration", hi: "सेवा प्रदाता रजिस्ट्रेशन" },
  registrationSaved: { en: "Registration saved", hi: "रजिस्ट्रेशन सेव हो गया" },
  registrationInvalid: { en: "Complete all required details correctly", hi: "सभी जरूरी जानकारी सही भरें" },
  address: { en: "Full address", hi: "पूरा पता" },
  locality: { en: "Locality", hi: "इलाका" },
  serviceAreas: { en: "Service areas", hi: "सेवा के इलाके" },
  experience: { en: "Experience (years)", hi: "अनुभव (वर्ष)" },
  bio: { en: "Work experience / qualifications", hi: "काम का अनुभव / योग्यता" },
  editRegistration: { en: "Registration details", hi: "रजिस्ट्रेशन जानकारी" },
  oneMobile: { en: "One mobile number · One account", hi: "एक मोबाइल नंबर · एक खाता" },
  login: { en: "Sign in", hi: "लॉग इन" },
  offers: { en: "Offers & announcements", hi: "ऑफ़र और घोषणाएँ" },
  offerDetails: { en: "View details", hi: "जानकारी देखें" },
  noOffers: { en: "No current offers", hi: "अभी कोई ऑफ़र नहीं" },
  terms: { en: "Terms & conditions", hi: "नियम और शर्तें" },
  offerCode: { en: "Offer code", hi: "ऑफ़र कोड" },
  viewService: { en: "View service", hi: "सेवा देखें" },
  previous: { en: "Previous", hi: "पिछला" },
  next: { en: "Next", hi: "अगला" },
  newOffer: { en: "New offer / ad", hi: "नया ऑफ़र / विज्ञापन" },
  title: { en: "Title", hi: "शीर्षक" },
  details: { en: "Details", hi: "जानकारी" },
  startDate: { en: "Starts", hi: "शुरुआत" },
  endDate: { en: "Ends (optional)", hi: "समाप्ति (वैकल्पिक)" },
  publish: { en: "Publish", hi: "प्रकाशित करें" },
  saveDraft: { en: "Save draft", hi: "ड्राफ्ट सेव करें" },
  edit: { en: "Edit", hi: "बदलें" },
  unpublish: { en: "Unpublish", hi: "प्रकाशन हटाएँ" },
  cancel: { en: "Cancel", hi: "रद्द करें" },
  saved: { en: "Saved", hi: "सेव हो गया" },
  allServices: { en: "All services", hi: "सभी सेवाएँ" },
  hindi: { en: "Hindi", hi: "हिन्दी" },
  english: { en: "English", hi: "अंग्रेज़ी" },
  sortOrder: { en: "Display order", hi: "दिखाने का क्रम" },
  draft: { en: "Draft", hi: "ड्राफ्ट" },
  appName: { en: "DwarSewa", hi: "द्वार सेवा" },
  testAccess: { en: "Preview test accounts", hi: "प्रीव्यू परीक्षण खातें" },
  testCode: { en: "Test OTP", hi: "परीक्षण OTP" },
  testCustomer: { en: "Customer", hi: "ग्राहक" },
  testProvider: { en: "Provider", hi: "सेवा देने वाला" },
  testRoleNote: { en: "First sign-in asks your role — pick the matching one.", hi: "पहलें लॉग इन में भूमिक़ा पूछी जायगी — सही भूमिक़ा चुनें।" },
  adminAccess: { en: "Admin sign-in", hi: "एडमिन लॉग इन" },
  testOtpPrompt: { en: "Enter the test OTP for", hi: "परीक्षण OTP डालें" },
  loginFailed: { en: "Could not log in. Please try again.", hi: "लॉग इन नहीं हो पाया। फिर कोशिश करें।" },
  tagline: { en: "Trusted home services in Ranchi", hi: "रांची में भरोसेमंद घरेलू सेवाएँ" },
  phone: { en: "Mobile number", hi: "मोबाइल नंबर" },
  sendOtp: { en: "Send OTP", hi: "OTP भेजें" },
  enterOtp: { en: "Enter the 6-digit OTP sent to", hi: "6 अंकों का OTP डालें, भेजा गया" },
  verify: { en: "Verify & continue", hi: "सत्यापित करें" },
  changeNumber: { en: "Change number", hi: "नंबर बदलें" },
  invalidPhone: { en: "Enter a valid 10-digit number", hi: "सही 10 अंकों का नंबर डालें" },
  sessionExpired: { en: "Your session expired. Please log in again.", hi: "सत्र समाप्त हो गया। कृपया फिर से लॉग इन करें।" },
  whoAreYou: { en: "How will you use the app?", hi: "आप ऐप कैसे इस्तेमाल करेंगे?" },
  yourName: { en: "Your name", hi: "आपका नाम" },
  needService: { en: "I need a service", hi: "मुझे सेवा चाहिए" },
  needServiceSub: { en: "Book cleaning, repairs, salon & more", hi: "सफ़ाई, मरम्मत, सैलून बुक करें" },
  provideService: { en: "I provide services", hi: "मैं सेवा देता/देती हूँ" },
  provideServiceSub: { en: "Get jobs near you and earn", hi: "पास में काम पाएँ और कमाएँ" },
  hello: { en: "Hello", hi: "नमस्ते" },
  search: { en: "Search for a service", hi: "सेवा खोजें" },
  services: { en: "Services", hi: "सेवाएँ" },
  activeBooking: { en: "Active booking", hi: "चालू बुकिंग" },
  packages: { en: "Packages", hi: "पैकेज" },
  mins: { en: "min", hi: "मिनट" },
  book: { en: "Book", hi: "बुक करें" },
  comingSoon: { en: "Booking opens soon", hi: "बुकिंग जल्द शुरू होगी" },
  account: { en: "Account", hi: "खाता" },
  logout: { en: "Logout", hi: "लॉग आउट" },
  language: { en: "Language", hi: "भाषा" },
  online: { en: "You are online", hi: "आप ऑनलाइन हैं" },
  offline: { en: "You are offline", hi: "आप ऑफ़लाइन हैं" },
  todaysJobs: { en: "Today's jobs", hi: "आज के काम" },
  earnings: { en: "Earnings", hi: "कमाई" },
  todayEarnings: { en: "Today's earnings", hi: "आज की कमाई" },
  jobs: { en: "Jobs", hi: "काम" },
  profile: { en: "Profile", hi: "प्रोफ़ाइल" },
  noJobs: { en: "No new jobs yet", hi: "अभी कोई नया काम नहीं" },
  noJobsSub: { en: "Stay online — new jobs in your area will show here.", hi: "ऑनलाइन रहें — आपके इलाके के नए काम यहाँ दिखेंगे।" },
  pendingVerification: { en: "Pending verification — you can go online after admin approval.", hi: "सत्यापन बाकी — एडमिन की मंज़ूरी के बाद आप ऑनलाइन हो सकते हैं।" },
  blocked: { en: "Your account is blocked. Contact support.", hi: "आपका खाता ब्लॉक है। सहायता से संपर्क करें।" },
  noEarnings: { en: "Earnings will appear after your first completed job.", hi: "पहला काम पूरा होने के बाद कमाई दिखेगी।" },
} as const;

export type TKey = keyof typeof dict;

const Ctx = createContext<{ lang: Lang; setLang: (l: Lang) => void; t: (k: TKey) => string }>({
  lang: "en",
  setLang: () => {},
  t: (k) => dict[k].en,
});

export function I18nProvider({ children }: { children: ReactNode }) {
  const [lang, setLangState] = useState<Lang>("en");
  useEffect(() => {
    const saved = localStorage.getItem("lang");
    if (saved === "hi" || saved === "en") setLangState(saved);
  }, []);
  const setLang = (l: Lang) => {
    setLangState(l);
    localStorage.setItem("lang", l);
  };
  return <Ctx.Provider value={{ lang, setLang, t: (k) => dict[k][lang] }}>{children}</Ctx.Provider>;
}

export const useI18n = () => useContext(Ctx);

export function LangToggle() {
  const { lang, setLang } = useI18n();
  return (
    <div className="inline-flex rounded-full border bg-card p-1 text-sm font-semibold">
      {(["en", "hi"] as const).map((l) => (
        <button
          key={l}
          onClick={() => setLang(l)}
          className={`rounded-full px-3 py-1 ${lang === l ? "bg-secondary text-secondary-foreground" : "text-muted-foreground"}`}
        >
          {l === "en" ? "EN" : "हिं"}
        </button>
      ))}
    </div>
  );
}

export const LOCALITIES = ["Lalpur", "Kanke Road", "Morabadi", "Harmu", "Doranda", "Bariatu", "Other"];
