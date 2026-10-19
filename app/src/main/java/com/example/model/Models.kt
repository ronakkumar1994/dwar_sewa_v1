package com.example.model

enum class Role {
    CUSTOMER, PROVIDER, ADMIN
}

enum class BookingStatus(val key: String) {
    PENDING("pending"),
    ASSIGNED("assigned"),
    ACCEPTED("accepted"),
    ON_THE_WAY("on_the_way"),
    STARTED("started"),
    COMPLETED("completed"),
    CANCELLED("cancelled")
}

enum class PaymentMode {
    CASH, ONLINE
}

enum class ProviderStatus {
    PENDING, APPROVED, BLOCKED
}

enum class Language(val code: String, val label: String) {
    EN("en", "EN"),
    HI("hi", "हिं")
}

val LOCALITIES = listOf(
    "Lalpur",
    "Kanke Road",
    "Morabadi",
    "Harmu",
    "Doranda",
    "Bariatu",
    "Other"
)

data class ServiceItem(
    val id: String,
    val name: String,
    val nameHi: String,
    val icon: String,
    val drawableRes: Int,
    val isActive: Boolean = true,
    val sort: Int = 0
)

data class ServicePackage(
    val id: String,
    val serviceId: String,
    val name: String,
    val price: Int,
    val durationMinutes: Int
)

data class Booking(
    val id: String,
    val customerId: String,
    val providerId: String? = null,
    val packageId: String,
    val packageName: String,
    val serviceName: String,
    val amount: Int,
    val slotTime: String,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String? = null,
    val address: String? = null,
    val createdAt: String = "Just now"
)

data class Address(
    val id: String,
    val userId: String,
    val locality: String,
    val fullAddress: String
)

data class Complaint(
    val id: String,
    val userId: String,
    val bookingId: String? = null,
    val message: String,
    val status: String = "open", // open, resolved, closed
    val createdAt: String = "Today"
)

data class Offer(
    val id: String,
    val title: String,
    val titleHi: String,
    val details: String,
    val detailsHi: String,
    val terms: String,
    val termsHi: String,
    val code: String,
    val serviceId: String? = null,
    val startsAt: String,
    val endsAt: String? = null,
    val isActive: Boolean = true,
    val sort: Int = 0
)

data class ProviderProfile(
    val id: String,
    val fullName: String,
    val phone: String,
    val services: List<String>,
    val areas: List<String>,
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val rating: Double = 4.8,
    val jobsCompleted: Int = 0,
    val status: ProviderStatus = ProviderStatus.PENDING,
    val experienceYears: Int = 0,
    val bio: String = "",
    val businessAddress: String = ""
)

data class UserProfile(
    val id: String,
    val phone: String,
    val fullName: String,
    val language: Language = Language.EN,
    val registrationCompleted: Boolean = false,
    val role: Role? = null
)

object DwarSewaStrings {
    private val dict = mapOf(
        "appName" to Pair("DwarSewa", "द्वार सेवा"),
        "tagline" to Pair("Trusted home services in Ranchi", "रांची में भरोसेमंद घरेलू सेवाएँ"),
        "home" to Pair("Home", "होम"),
        "forYourHome" to Pair("For your home", "आपके घर के लिए"),
        "spotlight" to Pair("In the spotlight", "खास आपके लिए"),
        "mostBooked" to Pair("Service packages", "सेवा पैकेज"),
        "homeCare" to Pair("A little care. A happier home.", "थोड़ी देखभाल। खुशहाल घर।"),
        "homeCareSub" to Pair("Cleaning, repairs & salon at your doorstep", "सफ़ाई, मरम्मत और सैलून आपके दरवाज़े पर"),
        "explore" to Pair("Explore", "देखें"),
        "seeAll" to Pair("See all", "सभी देखें"),
        "fromPrice" to Pair("From", "शुरुआत"),
        "noServices" to Pair("No services found", "कोई सेवा नहीं मिली"),
        "allBookings" to Pair("All", "सभी"),
        "back" to Pair("Back", "वापस"),
        "incompleteProfile" to Pair("Incomplete profile", "प्रोफ़ाइल अधूरी है"),
        "complete" to Pair("Complete", "पूरा करें"),
        "myBookings" to Pair("My bookings", "मेरी बुकिंग"),
        "devices" to Pair("My devices", "मेरे उपकरण"),
        "helpSupport" to Pair("Help & support", "सहायता और सपोर्ट"),
        "myPlans" to Pair("My plans", "मेरे प्लान"),
        "wallet" to Pair("Wallet", "वॉलेट"),
        "giftCards" to Pair("Gift cards", "गिफ़्ट कार्ड"),
        "memberships" to Pair("Passes & membership", "पास और सदस्यता"),
        "myRating" to Pair("My ratings", "मेरी रेटिंग"),
        "manageAddresses" to Pair("Manage addresses", "पते प्रबंधित करें"),
        "paymentMethods" to Pair("Manage payment methods", "भुगतान के तरीके"),
        "settings" to Pair("Settings", "सेटिंग्स"),
        "aboutDwarSewa" to Pair("About DwarSewa", "द्वार सेवा के बारे में"),
        "referFriends" to Pair("Share DwarSewa", "द्वार सेवा साझा करें"),
        "referSubtitle" to Pair("Home services for your friends and family.", "आपके दोस्तों और परिवार के लिए घरेलू सेवाएँ।"),
        "referNow" to Pair("Invite friends", "दोस्तों को बुलाएँ"),
        "share" to Pair("Share", "साझा करें"),
        "copyLink" to Pair("Copy link", "लिंक कॉपी करें"),
        "linkCopied" to Pair("Link copied", "लिंक कॉपी हो गया"),
        "noBookings" to Pair("No bookings yet", "अभी कोई बुकिंग नहीं है"),
        "browseServices" to Pair("Browse services", "सेवाएँ देखें"),
        "noRatings" to Pair("No ratings yet", "अभी कोई रेटिंग नहीं है"),
        "noAddresses" to Pair("No saved addresses", "कोई पता सेव नहीं है"),
        "addAddress" to Pair("Add address", "पता जोड़ें"),
        "delete" to Pair("Delete", "हटाएँ"),
        "save" to Pair("Save", "सेव करें"),
        "loading" to Pair("Loading…", "लोड हो रहा है…"),
        "retry" to Pair("Try again", "फिर कोशिश करें"),
        "noDevices" to Pair("Device registration is not available yet", "उपकरण रजिस्ट्रेशन अभी उपलब्ध नहीं है"),
        "noPlans" to Pair("No service plans available yet", "अभी कोई सेवा प्लान उपलब्ध नहीं है"),
        "walletUnavailable" to Pair("Wallet payments are not available yet", "वॉलेट भुगतान अभी उपलब्ध नहीं है"),
        "giftsUnavailable" to Pair("Gift cards are not available yet", "गिफ़्ट कार्ड अभी उपलब्ध नहीं हैं"),
        "membershipsUnavailable" to Pair("Memberships are not available yet", "सदस्यता अभी उपलब्ध नहीं है"),
        "paymentsUnavailable" to Pair("Saved payment methods are not available yet", "सेव किए भुगतान के तरीके अभी उपलब्ध नहीं हैं"),
        "referralUnavailable" to Pair("Referral rewards are not available yet", "रेफ़रल पुरस्कार अभी उपलब्ध नहीं हैं"),
        "message" to Pair("Message", "संदेश"),
        "sendRequest" to Pair("Send request", "अनुरोध भेजें"),
        "requestSent" to Pair("Your request has been sent", "आपका अनुरोध भेज दिया गया है"),
        "myRequests" to Pair("My support requests", "मेरे सहायता अनुरोध"),
        "noRequests" to Pair("No support requests", "कोई सहायता अनुरोध नहीं है"),
        "bookingOptional" to Pair("Booking (optional)", "बुकिंग (वैकल्पिक)"),
        "generalRequest" to Pair("General request", "सामान्य अनुरोध"),
        "cash" to Pair("Cash", "नकद"),
        "onlinePayment" to Pair("Online", "ऑनलाइन"),
        "pending" to Pair("Pending", "लंबित"),
        "assigned" to Pair("Assigned", "प्रदाता नियुक्त"),
        "accepted" to Pair("Accepted", "स्वीकार किया गया"),
        "on_the_way" to Pair("On the way", "रास्ते में"),
        "started" to Pair("Started", "शुरू"),
        "completed" to Pair("Completed", "पूरा हुआ"),
        "cancelled" to Pair("Cancelled", "रद्द"),
        "open" to Pair("Open", "खुला"),
        "resolved" to Pair("Resolved", "हल हो गया"),
        "closed" to Pair("Closed", "बंद"),
        "supportRequest" to Pair("Support request", "सहायता अनुरोध"),
        "accountDetails" to Pair("Account details", "खाते की जानकारी"),
        "mobileLocked" to Pair("Your verified mobile number cannot be changed.", "आपका सत्यापित मोबाइल नंबर बदला नहीं जा सकता।"),
        "aboutCopy" to Pair("DwarSewa brings home services to your doorstep in Ranchi.", "द्वार सेवा रांची में आपके दरवाज़े तक घरेलू सेवाएँ पहुँचाता है।"),
        "register" to Pair("Register", "रजिस्टर करें"),
        "customerRegistration" to Pair("Customer registration", "ग्राहक रजिस्ट्रेशन"),
        "providerRegistration" to Pair("Provider registration", "सेवा प्रदाता रजिस्ट्रेशन"),
        "registrationSaved" to Pair("Registration saved", "रजिस्ट्रेशन सेव हो गया"),
        "address" to Pair("Full address", "पूरा पता"),
        "locality" to Pair("Locality", "इलाका"),
        "serviceAreas" to Pair("Service areas", "सेवा के इलाके"),
        "experience" to Pair("Experience (years)", "अनुभव (वर्ष)"),
        "bio" to Pair("Work experience / qualifications", "काम का अनुभव / योग्यता"),
        "editRegistration" to Pair("Registration details", "रजिस्ट्रेशन जानकारी"),
        "oneMobile" to Pair("One mobile number · One account", "एक मोबाइल नंबर · एक खाता"),
        "login" to Pair("Sign in", "लॉग इन"),
        "offers" to Pair("Offers & announcements", "ऑफ़र और घोषणाएँ"),
        "offerDetails" to Pair("View details", "जानकारी देखें"),
        "noOffers" to Pair("No current offers", "अभी कोई ऑफ़र नहीं"),
        "terms" to Pair("Terms & conditions", "नियम और शर्तें"),
        "offerCode" to Pair("Offer code", "ऑफ़र कोड"),
        "viewService" to Pair("View service", "सेवा देखें"),
        "newOffer" to Pair("New offer / ad", "नया ऑफ़र / विज्ञापन"),
        "title" to Pair("Title", "शीर्षक"),
        "details" to Pair("Details", "जानकारी"),
        "publish" to Pair("Publish", "प्रकाशित करें"),
        "saveDraft" to Pair("Save draft", "ड्राफ्ट सेव करें"),
        "edit" to Pair("Edit", "बदलें"),
        "cancel" to Pair("Cancel", "रद्द करें"),
        "saved" to Pair("Saved", "सेव हो गया"),
        "allServices" to Pair("All services", "सभी सेवाएँ"),
        "hindi" to Pair("Hindi", "हिन्दी"),
        "english" to Pair("English", "अंग्रेज़ी"),
        "draft" to Pair("Draft", "ड्राफ्ट"),
        "testAccess" to Pair("Preview test accounts", "प्रीव्यू परीक्षण खातें"),
        "testCode" to Pair("Test OTP", "परीक्षण OTP"),
        "testCustomer" to Pair("Customer", "ग्राहक"),
        "testProvider" to Pair("Provider", "सेवा देने वाला"),
        "adminAccess" to Pair("Admin sign-in", "एडमिन लॉग इन"),
        "phone" to Pair("Mobile number", "मोबाइल नंबर"),
        "sendOtp" to Pair("Send OTP", "OTP भेजें"),
        "enterOtp" to Pair("Enter the 6-digit OTP sent to", "6 अंकों का OTP डालें, भेजा गया"),
        "verify" to Pair("Verify & continue", "सत्यापित करें"),
        "changeNumber" to Pair("Change number", "नंबर बदलें"),
        "invalidPhone" to Pair("Enter a valid 10-digit number", "सही 10 अंकों का नंबर डालें"),
        "yourName" to Pair("Your name", "आपका नाम"),
        "search" to Pair("Search for a service", "सेवा खोजें"),
        "services" to Pair("Services", "सेवाएँ"),
        "activeBooking" to Pair("Active booking", "चालू बुकिंग"),
        "packages" to Pair("Packages", "पैकेज"),
        "mins" to Pair("min", "मिनट"),
        "book" to Pair("Book", "बुक करें"),
        "account" to Pair("Account", "खाता"),
        "logout" to Pair("Logout", "लॉग आउट"),
        "language" to Pair("Language", "भाषा"),
        "online" to Pair("You are online", "आप ऑनलाइन हैं"),
        "offline" to Pair("You are offline", "आप ऑफ़लाइन हैं"),
        "todaysJobs" to Pair("Today's jobs", "आज के काम"),
        "earnings" to Pair("Earnings", "कमाई"),
        "todayEarnings" to Pair("Today's earnings", "आज की कमाई"),
        "jobs" to Pair("Jobs", "काम"),
        "profile" to Pair("Profile", "प्रोफ़ाइल"),
        "noJobs" to Pair("No new jobs yet", "अभी कोई नया काम नहीं"),
        "noJobsSub" to Pair("Stay online — new jobs in your area will show here.", "ऑनलाइन रहें — आपके इलाके के नए काम यहाँ दिखेंगे।"),
        "pendingVerification" to Pair("Pending verification — you can go online after admin approval.", "सत्यापन बाकी — एडमिन की मंज़ूरी के बाद आप ऑनलाइन हो सकते हैं।"),
        "blocked" to Pair("Your account is blocked. Contact support.", "आपका खाता ब्लॉक है। सहायता से संपर्क करें।"),
        "noEarnings" to Pair("Earnings will appear after your first completed job.", "पहला काम पूरा होने के बाद कमाई दिखेगी।")
    )

    fun t(key: String, lang: Language): String {
        val pair = dict[key] ?: return key
        return if (lang == Language.HI) pair.second else pair.first
    }
}
