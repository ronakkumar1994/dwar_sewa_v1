package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DwarSewaRepository
import com.example.model.DwarSewaStrings
import com.example.model.Language
import com.example.theme.*
import com.example.ui.components.LangToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailScreen(
    offerId: String,
    onBack: () -> Unit,
    onNavigateToService: (String) -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val offers by DwarSewaRepository.offers.collectAsState()
    val offer = offers.firstOrNull { it.id == offerId } ?: offers.firstOrNull()

    var copied by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(DwarSewaStrings.t("offers", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_offer")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    LangToggle(
                        currentLanguage = lang,
                        onLanguageSelected = { DwarSewaRepository.setLanguage(it) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryTeal,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (offer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(DwarSewaStrings.t("noOffers", lang), color = TextMuted)
            }
        } else {
            val title = if (lang == Language.HI) offer.titleHi else offer.title
            val details = if (lang == Language.HI) offer.detailsHi else offer.details
            val terms = if (lang == Language.HI) offer.termsHi else offer.terms

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundWarm)
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.offers_home),
                    contentDescription = offer.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = details,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Coupon code banner
                    if (offer.code.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryOrange))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = PrimaryOrange
                                    )
                                    Column {
                                        Text(
                                            text = DwarSewaStrings.t("offerCode", lang),
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = offer.code,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryOrange
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { copied = true },
                                    modifier = Modifier.testTag("btn_copy_offer_code")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = SecondaryTeal
                                    )
                                }
                            }
                        }
                    }

                    if (copied) {
                        Text(
                            text = "✓ " + DwarSewaStrings.t("linkCopied", lang),
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (offer.endsAt != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Ends: ${offer.endsAt}",
                            fontSize = 13.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (terms.isNotBlank()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = DwarSewaStrings.t("terms", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = terms,
                            fontSize = 13.sp,
                            color = TextMuted,
                            lineHeight = 18.sp
                        )
                    }

                    if (offer.serviceId != null) {
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { onNavigateToService(offer.serviceId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_offer_view_service"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                        ) {
                            Text(
                                text = DwarSewaStrings.t("viewService", lang),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
