package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object DwarSewaRepository {

    private val _currentLanguage = MutableStateFlow(Language.EN)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _selectedLocality = MutableStateFlow("Lalpur")
    val selectedLocality: StateFlow<String> = _selectedLocality.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceItem>>(emptyList())
    val services: StateFlow<List<ServiceItem>> = _services.asStateFlow()

    private val _packages = MutableStateFlow<List<ServicePackage>>(emptyList())
    val packages: StateFlow<List<ServicePackage>> = _packages.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    private val _providers = MutableStateFlow<List<ProviderProfile>>(emptyList())
    val providers: StateFlow<List<ProviderProfile>> = _providers.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val seededServices = listOf(
            ServiceItem("s1", "AC Repair", "एसी रिपेयर", "❄️", R.drawable.service_repairs, true, 1),
            ServiceItem("s2", "Home Cleaning", "घर की सफ़ाई", "🧹", R.drawable.service_cleaning, true, 2),
            ServiceItem("s3", "Salon at Home", "घर पर सैलून", "💇", R.drawable.service_salon, true, 3),
            ServiceItem("s4", "Plumber", "प्लंबर", "🔧", R.drawable.service_maintenance, true, 4),
            ServiceItem("s5", "Electrician", "इलेक्ट्रीशियन", "💡", R.drawable.service_repairs, true, 5)
        )
        _services.value = seededServices

        _packages.value = listOf(
            ServicePackage("p1", "s1", "AC Service (Split)", 499, 60),
            ServicePackage("p2", "s1", "Gas Refill", 2499, 90),
            ServicePackage("p3", "s1", "AC Installation", 1499, 120),
            ServicePackage("p4", "s2", "Bathroom Cleaning", 399, 60),
            ServicePackage("p5", "s2", "Kitchen Deep Clean", 999, 150),
            ServicePackage("p6", "s2", "Full Home Deep Clean", 3499, 300),
            ServicePackage("p7", "s3", "Haircut", 299, 30),
            ServicePackage("p8", "s3", "Facial & Cleanup", 799, 60),
            ServicePackage("p9", "s3", "Waxing (Full)", 999, 75),
            ServicePackage("p10", "s4", "Tap Repair", 149, 30),
            ServicePackage("p11", "s4", "Leakage Fix", 299, 45),
            ServicePackage("p12", "s4", "Water Tank Cleaning", 699, 90),
            ServicePackage("p13", "s5", "Switch / Socket Repair", 99, 20),
            ServicePackage("p14", "s5", "Fan Installation", 199, 40),
            ServicePackage("p15", "s5", "House Wiring Check", 599, 90)
        )

        _offers.value = listOf(
            Offer(
                id = "off1",
                title = "Diwali Cleaning Special",
                titleHi = "दिवाली सफ़ाई विशेष छूट",
                details = "Flat 20% off on Full Home Deep Clean and Bathroom sanitization across Ranchi.",
                detailsHi = "पूरे घर की गहरी सफ़ाई और बाथरूम की सफ़ाई पर 20% की विशेष छूट।",
                terms = "Valid for bookings completed before 31st October. Cannot be combined with other coupons.",
                termsHi = "31 अक्टूबर से पहले की बुकिंग के लिए मान्य। अन्य कूपन के साथ नहीं जोड़ा जा सकता।",
                code = "RANCHI20",
                serviceId = "s2",
                startsAt = "2026-10-01",
                endsAt = "2026-10-31",
                isActive = true,
                sort = 1
            ),
            Offer(
                id = "off2",
                title = "AC Summer Ready Inspection",
                titleHi = "एसी सर्विस और चेकअप",
                details = "Complete 15-point checkup and jet wash filter cleaning at just ₹399.",
                detailsHi = "15-पॉइंट चेकअप और जेट वॉश फ़िल्टर सफ़ाई सिर्फ़ ₹399 में।",
                terms = "Applicable on Split & Window ACs. Spare parts extra if required.",
                termsHi = "स्प्लिट और विंडो एसी पर लागू। अतिरिक्त पार्ट्स का शुल्क अलग होगा।",
                code = "COOLHOME",
                serviceId = "s1",
                startsAt = "2026-10-10",
                endsAt = "2026-11-15",
                isActive = true,
                sort = 2
            )
        )

        _providers.value = listOf(
            ProviderProfile(
                id = "prov-1",
                fullName = "Ravi Kumar",
                phone = "9999999998",
                services = listOf("s1", "s5"),
                areas = listOf("Lalpur", "Morabadi"),
                isVerified = true,
                isOnline = true,
                rating = 4.8,
                jobsCompleted = 132,
                status = ProviderStatus.APPROVED,
                experienceYears = 4,
                bio = "Certified AC technician with 4 years experience in home appliance repairs.",
                businessAddress = "Circular Road, Lalpur, Ranchi"
            ),
            ProviderProfile(
                id = "prov-2",
                fullName = "Sunita Devi",
                phone = "9800000002",
                services = listOf("s2", "s3"),
                areas = listOf("Harmu", "Doranda", "Kanke Road"),
                isVerified = true,
                isOnline = false,
                rating = 4.9,
                jobsCompleted = 89,
                status = ProviderStatus.APPROVED,
                experienceYears = 6,
                bio = "Professional home cleaning and beautician services in Ranchi.",
                businessAddress = "Harmu Housing Colony, Ranchi"
            ),
            ProviderProfile(
                id = "prov-3",
                fullName = "Amit Oraon",
                phone = "9800000003",
                services = listOf("s4"),
                areas = listOf("Bariatu", "Lalpur"),
                isVerified = false,
                isOnline = false,
                rating = 0.0,
                jobsCompleted = 0,
                status = ProviderStatus.PENDING,
                experienceYears = 2,
                bio = "Experienced plumber handling pipeline fittings and water tanks.",
                businessAddress = "Bariatu Road, Ranchi"
            )
        )

        // Seed customer user with sample addresses and booking
        val testCustomerId = "cust-1"
        _addresses.value = listOf(
            Address("a1", testCustomerId, "Lalpur", "Flat 302, Green Valley Apts, Circular Road, Lalpur"),
            Address("a2", testCustomerId, "Morabadi", "House 14, Near Oxygen Park, Morabadi, Ranchi")
        )

        _bookings.value = listOf(
            Booking(
                id = "b1",
                customerId = testCustomerId,
                providerId = "prov-1",
                packageId = "p1",
                packageName = "AC Service (Split)",
                serviceName = "AC Repair",
                amount = 499,
                slotTime = "Today, 4:00 PM",
                paymentMode = PaymentMode.CASH,
                status = BookingStatus.ON_THE_WAY,
                notes = "Please call before arriving",
                address = "Flat 302, Green Valley Apts, Circular Road, Lalpur"
            )
        )

        _complaints.value = listOf(
            Complaint("c1", testCustomerId, "b1", "Technician arrived right on time, great service!", "resolved", "Yesterday")
        )
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
    }

    fun setLocality(locality: String) {
        _selectedLocality.value = locality
    }

    fun loginWithPhone(phone: String, roleHint: Role? = null): Boolean {
        val cleanPhone = phone.trim().takeLast(10)
        when (cleanPhone) {
            "9999999999" -> { // Customer Preview
                _currentUser.value = UserProfile(
                    id = "cust-1",
                    phone = "9999999999",
                    fullName = "Pooja Gupta",
                    language = _currentLanguage.value,
                    registrationCompleted = true,
                    role = Role.CUSTOMER
                )
                return true
            }
            "9999999998" -> { // Provider Preview
                _currentUser.value = UserProfile(
                    id = "prov-1",
                    phone = "9999999998",
                    fullName = "Ravi Kumar",
                    language = _currentLanguage.value,
                    registrationCompleted = true,
                    role = Role.PROVIDER
                )
                return true
            }
            else -> {
                val assignedRole = roleHint ?: Role.CUSTOMER
                _currentUser.value = UserProfile(
                    id = UUID.randomUUID().toString(),
                    phone = cleanPhone,
                    fullName = "",
                    language = _currentLanguage.value,
                    registrationCompleted = false,
                    role = assignedRole
                )
                return true
            }
        }
    }

    fun loginAsAdmin() {
        _currentUser.value = UserProfile(
            id = "admin-1",
            phone = "9999999990",
            fullName = "DwarSewa Admin",
            language = _currentLanguage.value,
            registrationCompleted = true,
            role = Role.ADMIN
        )
    }

    fun logout() {
        _currentUser.value = null
    }

    fun completeCustomerRegistration(name: String, address: String, locality: String) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(
            fullName = name.trim(),
            registrationCompleted = true,
            role = Role.CUSTOMER
        )
        val newAddress = Address(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            locality = locality,
            fullAddress = address.trim()
        )
        _addresses.value = _addresses.value + newAddress
        _selectedLocality.value = locality
    }

    fun completeProviderRegistration(
        name: String,
        address: String,
        services: List<String>,
        areas: List<String>,
        experience: Int,
        bio: String
    ) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(
            fullName = name.trim(),
            registrationCompleted = true,
            role = Role.PROVIDER
        )
        val existingIndex = _providers.value.indexOfFirst { it.id == user.id || it.phone == user.phone }
        val newProfile = ProviderProfile(
            id = user.id,
            fullName = name.trim(),
            phone = user.phone,
            services = services,
            areas = areas,
            isVerified = true,
            isOnline = true,
            status = ProviderStatus.APPROVED,
            experienceYears = experience,
            bio = bio,
            businessAddress = address
        )
        if (existingIndex >= 0) {
            val list = _providers.value.toMutableList()
            list[existingIndex] = newProfile
            _providers.value = list
        } else {
            _providers.value = _providers.value + newProfile
        }
    }

    fun addBooking(
        pkg: ServicePackage,
        service: ServiceItem,
        slotTime: String,
        paymentMode: PaymentMode,
        address: String,
        notes: String?
    ): Booking {
        val userId = _currentUser.value?.id ?: "guest"
        val booking = Booking(
            id = "b-" + UUID.randomUUID().toString().take(8),
            customerId = userId,
            providerId = "prov-1",
            packageId = pkg.id,
            packageName = pkg.name,
            serviceName = service.name,
            amount = pkg.price,
            slotTime = slotTime,
            paymentMode = paymentMode,
            status = BookingStatus.PENDING,
            notes = notes,
            address = address
        )
        _bookings.value = listOf(booking) + _bookings.value
        return booking
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        _bookings.value = _bookings.value.map {
            if (it.id == bookingId) it.copy(status = newStatus) else it
        }
    }

    fun addAddress(locality: String, fullAddress: String) {
        val userId = _currentUser.value?.id ?: "cust-1"
        val newAddr = Address(UUID.randomUUID().toString(), userId, locality, fullAddress.trim())
        _addresses.value = _addresses.value + newAddr
    }

    fun updateAddress(id: String, locality: String, fullAddress: String) {
        _addresses.value = _addresses.value.map {
            if (it.id == id) it.copy(locality = locality, fullAddress = fullAddress.trim()) else it
        }
    }

    fun deleteAddress(id: String) {
        _addresses.value = _addresses.value.filter { it.id != id }
    }

    fun submitComplaint(bookingId: String?, message: String) {
        val userId = _currentUser.value?.id ?: "cust-1"
        val c = Complaint(UUID.randomUUID().toString(), userId, bookingId, message.trim(), "open", "Just now")
        _complaints.value = listOf(c) + _complaints.value
    }

    fun setProviderOnline(providerId: String, isOnline: Boolean) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) it.copy(isOnline = isOnline) else it
        }
    }

    fun setProviderStatus(providerId: String, status: ProviderStatus) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                it.copy(
                    status = status,
                    isVerified = status == ProviderStatus.APPROVED,
                    isOnline = if (status != ProviderStatus.APPROVED) false else it.isOnline
                )
            } else it
        }
    }

    fun saveOffer(offer: Offer) {
        val existingIndex = _offers.value.indexOfFirst { it.id == offer.id }
        if (existingIndex >= 0) {
            val list = _offers.value.toMutableList()
            list[existingIndex] = offer
            _offers.value = list
        } else {
            _offers.value = _offers.value + offer
        }
    }

    fun hideOffer(offerId: String) {
        _offers.value = _offers.value.map {
            if (it.id == offerId) it.copy(isActive = false) else it
        }
    }
}
