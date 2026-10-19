package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DwarSewaRepository
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.LangToggle
import com.example.ui.components.StatCard
import com.example.ui.components.StatusChip

@Composable
fun ProviderHomeScreen(
    onNavigateToEditRegistration: () -> Unit,
    onLogout: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val currentUser by DwarSewaRepository.currentUser.collectAsState()
    val providers by DwarSewaRepository.providers.collectAsState()
    val allBookings by DwarSewaRepository.bookings.collectAsState()

    var activeTab by remember { mutableStateOf("jobs") } // jobs, earnings, profile

    val providerProfile = providers.firstOrNull {
        it.id == currentUser?.id || it.phone == currentUser?.phone
    } ?: providers.firstOrNull { it.phone == "9999999998" }

    val myJobs = allBookings.filter {
        it.providerId == providerProfile?.id || providerProfile?.id == "prov-1"
    }

    val todayEarnings = myJobs
        .filter { it.status == BookingStatus.COMPLETED }
        .sumOf { it.amount }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceCard, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = activeTab == "jobs",
                    onClick = { activeTab = "jobs" },
                    icon = { Text("🧰", fontSize = 18.sp) },
                    label = { Text(DwarSewaStrings.t("jobs", lang), fontSize = 11.sp) },
                    modifier = Modifier.testTag("provider_tab_jobs")
                )
                NavigationBarItem(
                    selected = activeTab == "earnings",
                    onClick = { activeTab = "earnings" },
                    icon = { Text("💰", fontSize = 18.sp) },
                    label = { Text(DwarSewaStrings.t("earnings", lang), fontSize = 11.sp) },
                    modifier = Modifier.testTag("provider_tab_earnings")
                )
                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { activeTab = "profile" },
                    icon = { Text("👤", fontSize = 18.sp) },
                    label = { Text(DwarSewaStrings.t("profile", lang), fontSize = 11.sp) },
                    modifier = Modifier.testTag("provider_tab_profile")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWarm)
                .padding(padding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryTeal)
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = providerProfile?.fullName ?: currentUser?.fullName ?: "DwarSewa Partner",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LangToggle(
                        currentLanguage = lang,
                        onLanguageSelected = { DwarSewaRepository.setLanguage(it) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Online / Offline Switch
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (providerProfile?.isOnline == true) "🟢" else "⚪",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (providerProfile?.isOnline == true) DwarSewaStrings.t("online", lang) else DwarSewaStrings.t("offline", lang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Switch(
                            checked = providerProfile?.isOnline == true,
                            onCheckedChange = { checked ->
                                if (providerProfile != null) {
                                    if (providerProfile.status == ProviderStatus.APPROVED) {
                                        DwarSewaRepository.setProviderOnline(providerProfile.id, checked)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreen
                            ),
                            modifier = Modifier.testTag("switch_provider_online")
                        )
                    }
                }
            }

            // Alerts
            if (providerProfile?.status == ProviderStatus.PENDING) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "⏳ " + DwarSewaStrings.t("pendingVerification", lang),
                        color = WarningAmber,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (providerProfile?.status == ProviderStatus.BLOCKED) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = DestructiveRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "🚫 " + DwarSewaStrings.t("blocked", lang),
                        color = DestructiveRed,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Tabs Content
            when (activeTab) {
                "jobs" -> {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = DwarSewaStrings.t("todaysJobs", lang),
                                value = myJobs.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = DwarSewaStrings.t("todayEarnings", lang),
                                value = "₹$todayEarnings",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (myJobs.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📭", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = DwarSewaStrings.t("noJobs", lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = DwarSewaStrings.t("noJobsSub", lang),
                                        fontSize = 13.sp,
                                        color = TextMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Assigned Jobs in Ranchi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(myJobs) { job ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().testTag("provider_job_${job.id}"),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = job.packageName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                StatusChip(status = job.status, lang = lang)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(text = "📅 ${job.slotTime} · 📍 ${job.address ?: "Ranchi"}", fontSize = 12.sp, color = TextMuted)
                                            Text(text = "₹${job.amount} · ${if (job.paymentMode == PaymentMode.CASH) "Cash" else "Online"}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryOrange)

                                            if (job.notes != null) {
                                                Text(text = "Note: ${job.notes}", fontSize = 12.sp, color = TextMuted)
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Action button to advance status
                                            val nextStatus = when (job.status) {
                                                BookingStatus.PENDING, BookingStatus.ASSIGNED -> BookingStatus.ACCEPTED
                                                BookingStatus.ACCEPTED -> BookingStatus.ON_THE_WAY
                                                BookingStatus.ON_THE_WAY -> BookingStatus.STARTED
                                                BookingStatus.STARTED -> BookingStatus.COMPLETED
                                                BookingStatus.COMPLETED, BookingStatus.CANCELLED -> null
                                            }

                                            if (nextStatus != null) {
                                                Button(
                                                    onClick = { DwarSewaRepository.updateBookingStatus(job.id, nextStatus) },
                                                    modifier = Modifier.fillMaxWidth().testTag("btn_advance_job_${job.id}"),
                                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Advance: ${DwarSewaStrings.t(nextStatus.key, lang)}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "earnings" -> {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = DwarSewaStrings.t("todayEarnings", lang),
                                value = "₹$todayEarnings",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = DwarSewaStrings.t("jobs", lang),
                                value = (providerProfile?.jobsCompleted ?: 0).toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Payout Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Direct UPI & Bank Transfer enabled for Ranchi local partners.", fontSize = 13.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = DwarSewaStrings.t("noEarnings", lang), fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                }

                "profile" -> {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = providerProfile?.fullName ?: "Provider",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "+91 ${providerProfile?.phone ?: ""}", fontSize = 13.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "⭐ Rating: ${providerProfile?.rating ?: 4.8} / 5", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Areas: ${providerProfile?.areas?.joinToString(", ") ?: "Lalpur"}", fontSize = 13.sp, color = TextMuted)
                                Text(text = "Experience: ${providerProfile?.experienceYears ?: 2} years", fontSize = 13.sp, color = TextMuted)
                                if (!providerProfile?.bio.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Bio: ${providerProfile?.bio}", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }

                        Button(
                            onClick = onNavigateToEditRegistration,
                            modifier = Modifier.fillMaxWidth().testTag("btn_provider_edit_reg"),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(DwarSewaStrings.t("editRegistration", lang), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                DwarSewaRepository.logout()
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_provider_logout"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DestructiveRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(DwarSewaStrings.t("logout", lang), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
