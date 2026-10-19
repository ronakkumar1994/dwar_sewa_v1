package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DwarSewaRepository
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.BrandLogo
import com.example.ui.components.LangToggle
import com.example.ui.components.StatusChip

enum class AccountSubPage {
    MAIN, BOOKINGS, DEVICES, SUPPORT, PLANS, WALLET, GIFT_CARDS, MEMBERSHIP, RATINGS, ADDRESSES, PAYMENTS, SETTINGS, ABOUT, REFERRAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerAccountScreen(
    onBackToHome: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditRegistration: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val currentUser by DwarSewaRepository.currentUser.collectAsState()
    val bookings by DwarSewaRepository.bookings.collectAsState()
    val addresses by DwarSewaRepository.addresses.collectAsState()
    val complaints by DwarSewaRepository.complaints.collectAsState()

    var currentSubPage by remember { mutableStateOf(AccountSubPage.MAIN) }

    // Address form state
    var editingAddressId by remember { mutableStateOf<String?>(null) } // null = not editing, "new" = new address, id = edit existing
    var addressLocality by remember { mutableStateOf(LOCALITIES[0]) }
    var addressFull by remember { mutableStateOf("") }
    var localityDropdownExpanded by remember { mutableStateOf(false) }

    // Support form state
    var supportBookingId by remember { mutableStateOf<String?>(null) }
    var supportMessage by remember { mutableStateOf("") }
    var supportSentMessage by remember { mutableStateOf<String?>(null) }

    // Referral state
    var referralCopied by remember { mutableStateOf(false) }

    if (currentSubPage == AccountSubPage.MAIN) {
        val scrollState = rememberScrollState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(DwarSewaStrings.t("profile", lang), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackToHome, modifier = Modifier.testTag("btn_back_account")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SecondaryTeal,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundWarm)
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Profile header card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (currentUser?.registrationCompleted == false) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DestructiveRed.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = DestructiveRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = DwarSewaStrings.t("incompleteProfile", lang),
                                    color = DestructiveRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentUser?.fullName?.ifBlank { DwarSewaStrings.t("account", lang) } ?: DwarSewaStrings.t("account", lang),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentUser?.phone?.let { "+91 $it" } ?: "+91 9999999999",
                                    fontSize = 14.sp,
                                    color = TextMuted
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToEditRegistration,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_edit_profile")
                            ) {
                                Text(
                                    text = DwarSewaStrings.t(if (currentUser?.registrationCompleted == true) "edit" else "complete", lang),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Links (Bookings, Devices, Support)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.ListAlt,
                        label = DwarSewaStrings.t("myBookings", lang),
                        modifier = Modifier.weight(1f).testTag("quick_bookings")
                    ) { currentSubPage = AccountSubPage.BOOKINGS }

                    QuickActionCard(
                        icon = Icons.Default.PhoneAndroid,
                        label = DwarSewaStrings.t("devices", lang),
                        modifier = Modifier.weight(1f).testTag("quick_devices")
                    ) { currentSubPage = AccountSubPage.DEVICES }

                    QuickActionCard(
                        icon = Icons.Default.Headphones,
                        label = DwarSewaStrings.t("helpSupport", lang),
                        modifier = Modifier.weight(1f).testTag("quick_support")
                    ) { currentSubPage = AccountSubPage.SUPPORT }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Menu items
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        AccountMenuItem(icon = Icons.Default.Checklist, title = DwarSewaStrings.t("myPlans", lang)) { currentSubPage = AccountSubPage.PLANS }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.AccountBalanceWallet, title = DwarSewaStrings.t("wallet", lang)) { currentSubPage = AccountSubPage.WALLET }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.CardGiftcard, title = DwarSewaStrings.t("giftCards", lang)) { currentSubPage = AccountSubPage.GIFT_CARDS }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.Verified, title = DwarSewaStrings.t("memberships", lang)) { currentSubPage = AccountSubPage.MEMBERSHIP }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.Star, title = DwarSewaStrings.t("myRating", lang)) { currentSubPage = AccountSubPage.RATINGS }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.LocationOn, title = DwarSewaStrings.t("manageAddresses", lang)) { currentSubPage = AccountSubPage.ADDRESSES }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.CreditCard, title = DwarSewaStrings.t("paymentMethods", lang)) { currentSubPage = AccountSubPage.PAYMENTS }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.Settings, title = DwarSewaStrings.t("settings", lang)) { currentSubPage = AccountSubPage.SETTINGS }
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        AccountMenuItem(icon = Icons.Default.Info, title = DwarSewaStrings.t("aboutDwarSewa", lang)) { currentSubPage = AccountSubPage.ABOUT }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Refer Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentSubPage = AccountSubPage.REFERRAL }
                        .testTag("account_refer_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentYellow.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "🎁 " + DwarSewaStrings.t("referFriends", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = DwarSewaStrings.t("referSubtitle", lang), fontSize = 12.sp, color = TextMuted)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PrimaryOrange)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout button
                OutlinedButton(
                    onClick = {
                        DwarSewaRepository.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_logout"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DestructiveRed)
                ) {
                    Text(text = DwarSewaStrings.t("logout", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    } else {
        // Subpage Screen
        val pageTitle = when (currentSubPage) {
            AccountSubPage.BOOKINGS -> DwarSewaStrings.t("myBookings", lang)
            AccountSubPage.DEVICES -> DwarSewaStrings.t("devices", lang)
            AccountSubPage.SUPPORT -> DwarSewaStrings.t("helpSupport", lang)
            AccountSubPage.PLANS -> DwarSewaStrings.t("myPlans", lang)
            AccountSubPage.WALLET -> DwarSewaStrings.t("wallet", lang)
            AccountSubPage.GIFT_CARDS -> DwarSewaStrings.t("giftCards", lang)
            AccountSubPage.MEMBERSHIP -> DwarSewaStrings.t("memberships", lang)
            AccountSubPage.RATINGS -> DwarSewaStrings.t("myRating", lang)
            AccountSubPage.ADDRESSES -> DwarSewaStrings.t("manageAddresses", lang)
            AccountSubPage.PAYMENTS -> DwarSewaStrings.t("paymentMethods", lang)
            AccountSubPage.SETTINGS -> DwarSewaStrings.t("settings", lang)
            AccountSubPage.ABOUT -> DwarSewaStrings.t("aboutDwarSewa", lang)
            AccountSubPage.REFERRAL -> DwarSewaStrings.t("referFriends", lang)
            AccountSubPage.MAIN -> ""
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(pageTitle, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { currentSubPage = AccountSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SecondaryTeal,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundWarm)
                    .padding(padding)
            ) {
                when (currentSubPage) {
                    AccountSubPage.BOOKINGS -> {
                        if (bookings.isEmpty()) {
                            EmptyAccountState(Icons.Default.ListAlt, DwarSewaStrings.t("noBookings", lang))
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(bookings) { b ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("booking_item_${b.id}")
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = b.packageName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                StatusChip(status = b.status, lang = lang)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(text = "📅 ${b.slotTime}", fontSize = 13.sp, color = TextMuted)
                                            Text(text = "📍 ${b.address ?: "Ranchi"}", fontSize = 13.sp, color = TextMuted)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "₹${b.amount} · ${if (b.paymentMode == PaymentMode.CASH) DwarSewaStrings.t("cash", lang) else DwarSewaStrings.t("onlinePayment", lang)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryOrange)
                                                TextButton(onClick = { currentSubPage = AccountSubPage.SUPPORT }) {
                                                    Text(DwarSewaStrings.t("helpSupport", lang), fontSize = 12.sp, color = SecondaryTeal)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AccountSubPage.ADDRESSES -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Button(
                                onClick = {
                                    editingAddressId = "new"
                                    addressLocality = LOCALITIES[0]
                                    addressFull = ""
                                },
                                modifier = Modifier.fillMaxWidth().testTag("btn_add_address"),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(DwarSewaStrings.t("addAddress", lang), fontWeight = FontWeight.Bold)
                            }

                            if (editingAddressId != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(text = "Enter Address", fontWeight = FontWeight.Bold)

                                        ExposedDropdownMenuBox(
                                            expanded = localityDropdownExpanded,
                                            onExpandedChange = { localityDropdownExpanded = !localityDropdownExpanded }
                                        ) {
                                            OutlinedTextField(
                                                value = addressLocality,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text(DwarSewaStrings.t("locality", lang)) },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = localityDropdownExpanded) },
                                                modifier = Modifier.fillMaxWidth().menuAnchor()
                                            )
                                            ExposedDropdownMenu(
                                                expanded = localityDropdownExpanded,
                                                onDismissRequest = { localityDropdownExpanded = false }
                                            ) {
                                                LOCALITIES.forEach { loc ->
                                                    DropdownMenuItem(
                                                        text = { Text(loc) },
                                                        onClick = {
                                                            addressLocality = loc
                                                            localityDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = addressFull,
                                            onValueChange = { addressFull = it },
                                            label = { Text(DwarSewaStrings.t("address", lang)) },
                                            modifier = Modifier.fillMaxWidth().testTag("input_address_full"),
                                            minLines = 2
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    if (addressFull.isNotBlank()) {
                                                        if (editingAddressId == "new") {
                                                            DwarSewaRepository.addAddress(addressLocality, addressFull)
                                                        } else {
                                                            DwarSewaRepository.updateAddress(editingAddressId!!, addressLocality, addressFull)
                                                        }
                                                        editingAddressId = null
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                                modifier = Modifier.testTag("btn_save_address")
                                            ) {
                                                Text(DwarSewaStrings.t("save", lang))
                                            }
                                            OutlinedButton(onClick = { editingAddressId = null }) {
                                                Text(DwarSewaStrings.t("cancel", lang))
                                            }
                                        }
                                    }
                                }
                            }

                            if (addresses.isEmpty()) {
                                EmptyAccountState(Icons.Default.LocationOn, DwarSewaStrings.t("noAddresses", lang))
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                                    items(addresses) { a ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = a.locality, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Text(text = a.fullAddress, fontSize = 13.sp, color = TextMuted)
                                                }
                                                Row {
                                                    IconButton(onClick = {
                                                        editingAddressId = a.id
                                                        addressLocality = a.locality
                                                        addressFull = a.fullAddress
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SecondaryTeal)
                                                    }
                                                    IconButton(onClick = { DwarSewaRepository.deleteAddress(a.id) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DestructiveRed)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AccountSubPage.SUPPORT -> {
                        val scroll = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(scroll),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = DwarSewaStrings.t("supportRequest", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)

                            OutlinedTextField(
                                value = supportMessage,
                                onValueChange = { supportMessage = it },
                                label = { Text(DwarSewaStrings.t("message", lang)) },
                                placeholder = { Text("How can we help you with your DwarSewa service in Ranchi?") },
                                modifier = Modifier.fillMaxWidth().testTag("input_support_msg"),
                                minLines = 3
                            )

                            Button(
                                onClick = {
                                    if (supportMessage.isNotBlank()) {
                                        DwarSewaRepository.submitComplaint(supportBookingId, supportMessage)
                                        supportMessage = ""
                                        supportSentMessage = DwarSewaStrings.t("requestSent", lang)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("btn_send_support"),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(DwarSewaStrings.t("sendRequest", lang), fontWeight = FontWeight.Bold)
                            }

                            if (supportSentMessage != null) {
                                Text(
                                    text = "✓ $supportSentMessage",
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = DwarSewaStrings.t("myRequests", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)

                            if (complaints.isEmpty()) {
                                Text(DwarSewaStrings.t("noRequests", lang), color = TextMuted, fontSize = 13.sp)
                            } else {
                                complaints.forEach { c ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(text = c.message, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = c.createdAt, fontSize = 11.sp, color = TextMuted)
                                                Text(text = c.status.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (c.status == "resolved") SuccessGreen else WarningAmber)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AccountSubPage.RATINGS -> {
                        EmptyAccountState(Icons.Default.Star, DwarSewaStrings.t("noRatings", lang))
                    }

                    AccountSubPage.SETTINGS -> {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = DwarSewaStrings.t("language", lang), fontWeight = FontWeight.Bold)
                                        LangToggle(
                                            currentLanguage = lang,
                                            onLanguageSelected = { DwarSewaRepository.setLanguage(it) }
                                        )
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                    Text(text = DwarSewaStrings.t("accountDetails", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = currentUser?.fullName ?: "Customer", fontSize = 14.sp)
                                    Text(text = currentUser?.phone?.let { "+91 $it" } ?: "", fontSize = 13.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = DwarSewaStrings.t("mobileLocked", lang), fontSize = 12.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(14.dp))
                                    OutlinedButton(
                                        onClick = onNavigateToEditRegistration,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(DwarSewaStrings.t("editRegistration", lang))
                                    }
                                }
                            }
                        }
                    }

                    AccountSubPage.ABOUT -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            BrandLogo(textColor = SecondaryTeal)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = DwarSewaStrings.t("aboutCopy", lang),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 15.sp,
                                color = TextMuted,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { currentSubPage = AccountSubPage.SUPPORT },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal)
                            ) {
                                Text(DwarSewaStrings.t("helpSupport", lang))
                            }
                        }
                    }

                    AccountSubPage.REFERRAL -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = DwarSewaStrings.t("referFriends", lang), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = DwarSewaStrings.t("referSubtitle", lang),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { referralCopied = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                modifier = Modifier.testTag("btn_share_referral")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(DwarSewaStrings.t("share", lang))
                            }
                            if (referralCopied) {
                                Text(
                                    text = "✓ " + DwarSewaStrings.t("linkCopied", lang),
                                    color = SuccessGreen,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = DwarSewaStrings.t("referralUnavailable", lang), fontSize = 12.sp, color = TextMuted)
                        }
                    }

                    AccountSubPage.DEVICES -> EmptyAccountState(Icons.Default.PhoneAndroid, DwarSewaStrings.t("noDevices", lang))
                    AccountSubPage.PLANS -> EmptyAccountState(Icons.Default.Checklist, DwarSewaStrings.t("noPlans", lang))
                    AccountSubPage.WALLET -> EmptyAccountState(Icons.Default.AccountBalanceWallet, DwarSewaStrings.t("walletUnavailable", lang))
                    AccountSubPage.GIFT_CARDS -> EmptyAccountState(Icons.Default.CardGiftcard, DwarSewaStrings.t("giftsUnavailable", lang))
                    AccountSubPage.MEMBERSHIP -> EmptyAccountState(Icons.Default.Verified, DwarSewaStrings.t("membershipsUnavailable", lang))
                    AccountSubPage.PAYMENTS -> EmptyAccountState(Icons.Default.CreditCard, DwarSewaStrings.t("paymentsUnavailable", lang))
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = label, tint = SecondaryTeal, modifier = Modifier.size(24.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 15.sp, color = TextPrimary, fontWeight = FontWeight.Normal)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun EmptyAccountState(icon: ImageVector, message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SecondaryTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 15.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
