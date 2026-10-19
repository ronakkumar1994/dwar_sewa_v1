package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DwarSewaRepository
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.BrandLogo
import com.example.ui.components.LangToggle
import com.example.ui.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onNavigateToService: (String) -> Unit,
    onNavigateToOffer: (String) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReferral: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val locality by DwarSewaRepository.selectedLocality.collectAsState()
    val services by DwarSewaRepository.services.collectAsState()
    val packages by DwarSewaRepository.packages.collectAsState()
    val offers by DwarSewaRepository.offers.collectAsState()
    val bookings by DwarSewaRepository.bookings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var localityExpanded by remember { mutableStateOf(false) }

    val activeBooking = bookings.firstOrNull {
        it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED
    }

    val filteredServices = services.filter {
        val sName = if (lang == Language.HI) it.nameHi else it.name
        sName.contains(searchQuery, ignoreCase = true) || it.name.contains(searchQuery, ignoreCase = true)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(DwarSewaStrings.t("home", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SecondaryTeal,
                        selectedTextColor = SecondaryTeal,
                        indicatorColor = SecondaryTeal.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToBookings,
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Bookings") },
                    label = { Text(DwarSewaStrings.t("myBookings", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_bookings")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSupport,
                    icon = { Icon(Icons.Default.Headphones, contentDescription = "Support") },
                    label = { Text(DwarSewaStrings.t("helpSupport", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_support")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(DwarSewaStrings.t("profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWarm)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryTeal)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DwarSewa",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { localityExpanded = true }
                                .padding(top = 2.dp)
                                .testTag("btn_select_locality")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locality,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = localityExpanded,
                            onDismissRequest = { localityExpanded = false }
                        ) {
                            LOCALITIES.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc, fontWeight = if (loc == locality) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        DwarSewaRepository.setLocality(loc)
                                        localityExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LangToggle(
                            currentLanguage = lang,
                            onLanguageSelected = { DwarSewaRepository.setLanguage(it) }
                        )
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("btn_profile_header")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(DwarSewaStrings.t("search", lang), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_service"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (searchQuery.isEmpty()) {
                // Hero Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.service_cleaning),
                            contentDescription = "Cleaning service",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SecondaryTeal.copy(alpha = 0.75f))
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = DwarSewaStrings.t("homeCare", lang),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = DwarSewaStrings.t("homeCareSub", lang),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToBookings,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_hero_bookings")
                            ) {
                                Text(
                                    text = DwarSewaStrings.t("myBookings", lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Categories Section: "For your home"
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = DwarSewaStrings.t("forYourHome", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 3 Column Grid for services
                val chunked = filteredServices.chunked(3)
                chunked.forEach { rowServices ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowServices.forEach { s ->
                            val sName = if (lang == Language.HI) s.nameHi else s.name
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToService(s.id) }
                                    .testTag("service_card_${s.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = s.drawableRes),
                                            contentDescription = s.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceCard),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = s.icon, fontSize = 12.sp)
                                        }
                                    }
                                    Text(
                                        text = sName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(8.dp),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                        // Fill remaining spaces in row
                        repeat(3 - rowServices.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Active Booking Banner (if exists)
            if (activeBooking != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clickable { onNavigateToBookings() }
                        .testTag("card_active_booking"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryOrange))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚡ " + DwarSewaStrings.t("activeBooking", lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeBooking.packageName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusChip(status = activeBooking.status, lang = lang)
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View",
                            tint = TextMuted
                        )
                    }
                }
            }

            // Offers Section / Carousel
            if (offers.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "🎁 " + DwarSewaStrings.t("offers", lang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(offers) { offer ->
                            val offerTitle = if (lang == Language.HI) offer.titleHi else offer.title
                            val offerDetails = if (lang == Language.HI) offer.detailsHi else offer.details
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable { onNavigateToOffer(offer.id) }
                                    .testTag("card_offer_${offer.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                                        Image(
                                            painter = painterResource(id = R.drawable.offers_home),
                                            contentDescription = offer.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PrimaryOrange)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = offer.code,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = offerTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = offerDetails,
                                            fontSize = 12.sp,
                                            color = TextMuted,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = DwarSewaStrings.t("offerDetails", lang),
                                                color = PrimaryOrange,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = PrimaryOrange,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Spotlight Featured Horizontal Scroll
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = DwarSewaStrings.t("spotlight", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(services.take(3)) { s ->
                        val sName = if (lang == Language.HI) s.nameHi else s.name
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .height(130.dp)
                                .clickable { onNavigateToService(s.id) },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = s.drawableRes),
                                    contentDescription = s.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SecondaryTeal.copy(alpha = 0.8f))
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        text = "${s.icon} $sName",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${DwarSewaStrings.t("explore", lang)} →",
                                        color = PrimaryLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Service Packages Horizontal List
            services.forEach { service ->
                val sPackages = packages.filter { it.serviceId == service.id }
                if (sPackages.isNotEmpty()) {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        val sName = if (lang == Language.HI) service.nameHi else service.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            TextButton(onClick = { onNavigateToService(service.id) }) {
                                Text(
                                    text = DwarSewaStrings.t("seeAll", lang),
                                    color = SecondaryTeal,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(sPackages) { pkg ->
                                Card(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable { onNavigateToService(service.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = service.drawableRes),
                                            contentDescription = pkg.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxWidth().height(90.dp)
                                        )
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = pkg.name,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "⏱ ${pkg.durationMinutes} ${DwarSewaStrings.t("mins", lang)}",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "₹${pkg.price}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = PrimaryOrange
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Refer Friends Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clickable { onNavigateToReferral() }
                    .testTag("banner_refer_friends"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryTeal.copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SecondaryTeal.copy(alpha = 0.3f)))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🤝 " + DwarSewaStrings.t("referFriends", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryTeal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DwarSewaStrings.t("referSubtitle", lang),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(
                        onClick = onNavigateToReferral,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SecondaryTeal)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Invite",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
