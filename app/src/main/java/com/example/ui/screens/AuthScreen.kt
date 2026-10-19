package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DwarSewaRepository
import com.example.model.DwarSewaStrings
import com.example.model.Language
import com.example.model.Role
import com.example.theme.*
import com.example.ui.components.BrandLogo
import com.example.ui.components.LangToggle

@Composable
fun AuthScreen(
    onLoginSuccess: (Role) -> Unit,
    onNavigateToRegistration: (Role) -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var step by remember { mutableStateOf("phone") } // "phone" or "otp"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedRoleForRegistration by remember { mutableStateOf<Role?>(null) }
    var showAdminDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Language Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandLogo(textColor = SecondaryTeal)
            LangToggle(
                currentLanguage = lang,
                onLanguageSelected = { DwarSewaRepository.setLanguage(it) }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Hero Tagline
        Text(
            text = DwarSewaStrings.t("tagline", lang),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 32.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Role registration selection buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { onNavigateToRegistration(Role.CUSTOMER) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_customer_registration"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryOrange
                )
            ) {
                Text(
                    text = DwarSewaStrings.t("customerRegistration", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = { onNavigateToRegistration(Role.PROVIDER) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_provider_registration"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SecondaryTeal
                )
            ) {
                Text(
                    text = DwarSewaStrings.t("providerRegistration", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preview Test Accounts Callout
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.08f)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryOrange.copy(alpha = 0.3f)))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⚡ " + DwarSewaStrings.t("testAccess", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryOrange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            DwarSewaRepository.loginWithPhone("9999999999", Role.CUSTOMER)
                            onLoginSuccess(Role.CUSTOMER)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).testTag("chip_test_customer")
                    ) {
                        Text("9999999999 · " + DwarSewaStrings.t("testCustomer", lang), fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            DwarSewaRepository.loginWithPhone("9999999998", Role.PROVIDER)
                            onLoginSuccess(Role.PROVIDER)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).testTag("chip_test_provider")
                    ) {
                        Text("9999999998 · " + DwarSewaStrings.t("testProvider", lang), fontSize = 11.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${DwarSewaStrings.t("testCode", lang)}: 123456",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = DwarSewaStrings.t("adminAccess", lang),
                        fontSize = 12.sp,
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { showAdminDialog = true }
                            .testTag("link_admin_access")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestructiveRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = DestructiveRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (step == "phone") {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = DwarSewaStrings.t("phone", lang),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }.take(10)
                        phone = filtered
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_phone"),
                    leadingIcon = {
                        Text("+91", fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(start = 12.dp))
                    },
                    placeholder = { Text("98XXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (phone.length == 10 && phone.first() in '6'..'9') {
                            step = "otp"
                            otp = if (phone == "9999999999" || phone == "9999999998") "123456" else ""
                        } else {
                            errorMessage = DwarSewaStrings.t("invalidPhone", lang)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_send_otp"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(
                        text = DwarSewaStrings.t("sendOtp", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "${DwarSewaStrings.t("enterOtp", lang)} +91 $phone",
                    fontSize = 14.sp,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }.take(6)
                        otp = filtered
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_otp"),
                    placeholder = { Text("123456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (otp.length == 6) {
                            val role = if (phone == "9999999998") Role.PROVIDER else Role.CUSTOMER
                            val success = DwarSewaRepository.loginWithPhone(phone, role)
                            if (success) {
                                onLoginSuccess(role)
                            }
                        } else {
                            errorMessage = "Enter 6 digit OTP"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_verify_otp"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(
                        text = DwarSewaStrings.t("verify", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        step = "phone"
                        otp = ""
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("btn_change_number")
                ) {
                    Text(
                        text = DwarSewaStrings.t("changeNumber", lang),
                        color = SecondaryTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = { Text("Admin Sign-In", fontWeight = FontWeight.Bold) },
            text = {
                Text("Sign in as DwarSewa platform administrator to manage providers, verify KYC, and publish discount offers in Ranchi.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAdminDialog = false
                        DwarSewaRepository.loginAsAdmin()
                        onLoginSuccess(Role.ADMIN)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                    modifier = Modifier.testTag("btn_confirm_admin_login")
                ) {
                    Text("Enter Admin Dashboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text(DwarSewaStrings.t("cancel", lang))
                }
            }
        )
    }
}
