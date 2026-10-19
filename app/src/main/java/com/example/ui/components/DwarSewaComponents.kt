package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingStatus
import com.example.model.DwarSewaStrings
import com.example.model.Language
import com.example.theme.*

@Composable
fun LangToggle(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Language.values().forEach { lang ->
            val isSelected = currentLanguage == lang
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PrimaryOrange else Color.Transparent)
                    .clickable { onLanguageSelected(lang) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lang.label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "DwarSewa Home",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Column {
            Text(
                text = "DwarSewa",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "द्वार सेवा · Ranchi",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StatusChip(
    status: BookingStatus,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        BookingStatus.PENDING -> Pair(WarningAmber.copy(alpha = 0.15f), WarningAmber)
        BookingStatus.ASSIGNED, BookingStatus.ACCEPTED -> Pair(SecondaryTeal.copy(alpha = 0.15f), SecondaryTeal)
        BookingStatus.ON_THE_WAY, BookingStatus.STARTED -> Pair(PrimaryOrange.copy(alpha = 0.15f), PrimaryOrange)
        BookingStatus.COMPLETED -> Pair(SuccessGreen.copy(alpha = 0.15f), SuccessGreen)
        BookingStatus.CANCELLED -> Pair(DestructiveRed.copy(alpha = 0.15f), DestructiveRed)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = DwarSewaStrings.t(status.key, lang),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 24.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
