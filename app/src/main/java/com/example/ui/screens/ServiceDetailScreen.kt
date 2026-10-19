package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DwarSewaRepository
import com.example.model.*
import com.example.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit,
    onBookingCreated: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val services by DwarSewaRepository.services.collectAsState()
    val packages by DwarSewaRepository.packages.collectAsState()
    val addresses by DwarSewaRepository.addresses.collectAsState()

    val service = services.firstOrNull { it.id == serviceId } ?: services.firstOrNull()
    val servicePackages = packages.filter { it.serviceId == serviceId }.sortedBy { it.price }

    var selectedPackageToBook by remember { mutableStateOf<ServicePackage?>(null) }
    var selectedSlot by remember { mutableStateOf("Today, 4:00 PM") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var selectedAddress by remember { mutableStateOf(addresses.firstOrNull()?.fullAddress ?: "Lalpur, Ranchi") }
    var notes by remember { mutableStateOf("") }
    var bookingSuccessMessage by remember { mutableStateOf<String?>(null) }

    val serviceTitle = if (service != null && lang == Language.HI) service.nameHi else service?.name ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(serviceTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_service")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        ) {
            if (service != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = service.drawableRes),
                        contentDescription = service.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SecondaryTeal.copy(alpha = 0.5f))
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = service.icon, fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = serviceTitle,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${servicePackages.size} packages available",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Text(
                text = DwarSewaStrings.t("packages", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(servicePackages) { pkg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pkg_card_${pkg.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pkg.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⏱ ${pkg.durationMinutes} ${DwarSewaStrings.t("mins", lang)}",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "₹${pkg.price}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryOrange
                                )
                            }

                            Button(
                                onClick = { selectedPackageToBook = pkg },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_book_${pkg.id}")
                            ) {
                                Text(
                                    text = DwarSewaStrings.t("book", lang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Booking Bottom Sheet
    if (selectedPackageToBook != null && service != null) {
        val pkg = selectedPackageToBook!!
        ModalBottomSheet(
            onDismissRequest = { selectedPackageToBook = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Confirm Booking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SecondaryTeal.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = pkg.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "${pkg.durationMinutes} mins · ${service.name}", fontSize = 12.sp, color = TextMuted)
                        }
                        Text(text = "₹${pkg.price}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryOrange)
                    }
                }

                // Slot Time Selection
                Text(text = "Select Preferred Slot", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Today, 4:00 PM", "Tomorrow, 10:00 AM", "Tomorrow, 2:00 PM").forEach { slot ->
                        val isSelected = selectedSlot == slot
                        OutlinedButton(
                            onClick = { selectedSlot = slot },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.15f) else Color.Transparent,
                                contentColor = if (isSelected) PrimaryOrange else TextMuted
                            )
                        ) {
                            Text(text = slot.replace(", ", "\n"), fontSize = 11.sp, maxLines = 2)
                        }
                    }
                }

                // Address field
                OutlinedTextField(
                    value = selectedAddress,
                    onValueChange = { selectedAddress = it },
                    label = { Text("Service Address in Ranchi") },
                    modifier = Modifier.fillMaxWidth().testTag("input_booking_address"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Payment mode
                Text(text = "Payment Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = paymentMode == PaymentMode.CASH,
                            onClick = { paymentMode = PaymentMode.CASH }
                        )
                        Text(DwarSewaStrings.t("cash", lang), fontSize = 14.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = paymentMode == PaymentMode.ONLINE,
                            onClick = { paymentMode = PaymentMode.ONLINE }
                        )
                        Text(DwarSewaStrings.t("onlinePayment", lang), fontSize = 14.sp)
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special instructions (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        DwarSewaRepository.addBooking(
                            pkg = pkg,
                            service = service,
                            slotTime = selectedSlot,
                            paymentMode = paymentMode,
                            address = selectedAddress,
                            notes = notes.ifBlank { null }
                        )
                        selectedPackageToBook = null
                        bookingSuccessMessage = "Booking confirmed for ${pkg.name}!"
                        onBookingCreated()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_confirm_booking"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(text = "Confirm Booking · ₹${pkg.price}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
