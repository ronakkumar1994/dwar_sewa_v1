package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VisibilityOff
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onLogout: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val bookings by DwarSewaRepository.bookings.collectAsState()
    val providers by DwarSewaRepository.providers.collectAsState()
    val offers by DwarSewaRepository.offers.collectAsState()

    val totalBookings = bookings.size
    val assignedBookings = bookings.count { it.providerId != null }
    val unassignedBookings = bookings.count { it.providerId == null && it.status != BookingStatus.CANCELLED }
    val totalRevenue = bookings.filter { it.status == BookingStatus.COMPLETED }.sumOf { it.amount }

    var showOfferDialog by remember { mutableStateOf(false) }
    var editingOffer by remember { mutableStateOf<Offer?>(null) }
    var offerTitle by remember { mutableStateOf("") }
    var offerTitleHi by remember { mutableStateOf("") }
    var offerDetails by remember { mutableStateOf("") }
    var offerCode by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DwarSewa · Admin Operations", fontWeight = FontWeight.Bold) },
                actions = {
                    Button(
                        onClick = {
                            DwarSewaRepository.logout()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                        modifier = Modifier.padding(end = 12.dp).testTag("btn_admin_logout")
                    ) {
                        Text("Logout")
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("Bookings today", totalBookings.toString(), Modifier.weight(1f))
                AdminStatCard("Auto-assigned", assignedBookings.toString(), Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("Unassigned", unassignedBookings.toString(), Modifier.weight(1f))
                AdminStatCard("Revenue", "₹$totalRevenue", Modifier.weight(1f))
            }

            // Providers Section
            Text(
                text = "Partner Providers (KYC Verification)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                providers.forEach { provider ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("admin_provider_${provider.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = provider.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                val (statusBg, statusFg) = when (provider.status) {
                                    ProviderStatus.APPROVED -> Pair(SuccessGreen.copy(alpha = 0.15f), SuccessGreen)
                                    ProviderStatus.PENDING -> Pair(WarningAmber.copy(alpha = 0.15f), WarningAmber)
                                    ProviderStatus.BLOCKED -> Pair(DestructiveRed.copy(alpha = 0.15f), DestructiveRed)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(statusBg, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = provider.status.name,
                                        color = statusFg,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+91 ${provider.phone} · ${provider.areas.joinToString(", ")}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "⭐ ${provider.rating} · ${provider.jobsCompleted} jobs ${if (provider.isOnline) "· 🟢 online" else ""}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { DwarSewaRepository.setProviderStatus(provider.id, ProviderStatus.APPROVED) },
                                    enabled = provider.status != ProviderStatus.APPROVED,
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).testTag("btn_approve_${provider.id}")
                                ) {
                                    Text("Approve", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { DwarSewaRepository.setProviderStatus(provider.id, ProviderStatus.PENDING) },
                                    enabled = provider.status != ProviderStatus.PENDING,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).testTag("btn_reject_${provider.id}")
                                ) {
                                    Text("Reject", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { DwarSewaRepository.setProviderStatus(provider.id, ProviderStatus.BLOCKED) },
                                    enabled = provider.status != ProviderStatus.BLOCKED,
                                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).testTag("btn_block_${provider.id}")
                                ) {
                                    Text("Block", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Offers Management
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Promotional Offers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = {
                        editingOffer = null
                        offerTitle = ""
                        offerTitleHi = ""
                        offerDetails = ""
                        offerCode = ""
                        showOfferDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_admin_new_offer")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Offer", fontSize = 13.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                offers.forEach { offer ->
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
                                Text(text = offer.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "Code: ${offer.code} · ${if (offer.isActive) "Published" else "Draft"}",
                                    fontSize = 12.sp,
                                    color = if (offer.isActive) SuccessGreen else TextMuted
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    editingOffer = offer
                                    offerTitle = offer.title
                                    offerTitleHi = offer.titleHi
                                    offerDetails = offer.details
                                    offerCode = offer.code
                                    showOfferDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SecondaryTeal)
                                }
                                if (offer.isActive) {
                                    IconButton(onClick = { DwarSewaRepository.hideOffer(offer.id) }) {
                                        Icon(Icons.Default.VisibilityOff, contentDescription = "Hide", tint = DestructiveRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showOfferDialog) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = { Text(if (editingOffer == null) "New Offer" else "Edit Offer", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = offerTitle,
                        onValueChange = { offerTitle = it },
                        label = { Text("Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = offerTitleHi,
                        onValueChange = { offerTitleHi = it },
                        label = { Text("Title (Hindi)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = offerDetails,
                        onValueChange = { offerDetails = it },
                        label = { Text("Offer Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = offerCode,
                        onValueChange = { offerCode = it },
                        label = { Text("Promo Code (e.g. FESTIVE20)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newOffer = Offer(
                            id = editingOffer?.id ?: UUID.randomUUID().toString(),
                            title = offerTitle.ifBlank { "Special Offer" },
                            titleHi = offerTitleHi.ifBlank { offerTitle },
                            details = offerDetails.ifBlank { "Great discount on home services" },
                            detailsHi = offerDetails,
                            terms = "Valid for limited time in Ranchi.",
                            termsHi = "रांची में सीमित समय के लिए मान्य।",
                            code = offerCode.uppercase().ifBlank { "RANCHI" },
                            serviceId = "s2",
                            startsAt = "2026-10-19",
                            isActive = true
                        )
                        DwarSewaRepository.saveOffer(newOffer)
                        showOfferDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    modifier = Modifier.testTag("btn_save_offer")
                ) {
                    Text("Publish Offer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
