package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.BrandLogo
import com.example.ui.components.LangToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    role: Role,
    onComplete: (Role) -> Unit,
    onCancel: () -> Unit
) {
    val lang by DwarSewaRepository.currentLanguage.collectAsState()
    val currentUser by DwarSewaRepository.currentUser.collectAsState()
    val services by DwarSewaRepository.services.collectAsState()

    var name by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var address by remember { mutableStateOf("") }
    var selectedLocality by remember { mutableStateOf(LOCALITIES[0]) }
    var localityExpanded by remember { mutableStateOf(false) }

    // Provider fields
    var selectedServices by remember { mutableStateOf(setOf<String>()) }
    var selectedAreas by remember { mutableStateOf(setOf<String>()) }
    var experience by remember { mutableStateOf("2") }
    var bio by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val titleKey = if (role == Role.PROVIDER) "providerRegistration" else "customerRegistration"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Top header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.testTag("btn_back_registration")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            LangToggle(
                currentLanguage = lang,
                onLanguageSelected = { DwarSewaRepository.setLanguage(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = DwarSewaStrings.t(titleKey, lang),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = DwarSewaStrings.t("oneMobile", lang),
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestructiveRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = DestructiveRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Verified Phone Display
        OutlinedTextField(
            value = currentUser?.phone?.let { "+91 $it" } ?: "+91 98XXXXXXXX",
            onValueChange = {},
            readOnly = true,
            label = { Text(DwarSewaStrings.t("phone", lang)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorMessage = null },
            label = { Text("${DwarSewaStrings.t("yourName", lang)} *") },
            placeholder = { Text("e.g. Ramesh Kumar") },
            modifier = Modifier.fillMaxWidth().testTag("input_reg_name"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it; errorMessage = null },
            label = { Text("${DwarSewaStrings.t("address", lang)} *") },
            placeholder = { Text("House/Flat, Road, Landmark") },
            modifier = Modifier.fillMaxWidth().testTag("input_reg_address"),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (role == Role.CUSTOMER) {
            // Locality Exposed Dropdown
            ExposedDropdownMenuBox(
                expanded = localityExpanded,
                onExpandedChange = { localityExpanded = !localityExpanded }
            ) {
                OutlinedTextField(
                    value = selectedLocality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("${DwarSewaStrings.t("locality", lang)} *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = localityExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("select_reg_locality"),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = localityExpanded,
                    onDismissRequest = { localityExpanded = false }
                ) {
                    LOCALITIES.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                selectedLocality = loc
                                localityExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            // Provider specific inputs
            // Services checklist
            Text(
                text = "${DwarSewaStrings.t("services", lang)} *",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                services.forEach { service ->
                    val isChecked = selectedServices.contains(service.id)
                    val sName = if (lang == Language.HI) service.nameHi else service.name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedServices = if (isChecked) selectedServices - service.id else selectedServices + service.id
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) PrimaryOrange.copy(alpha = 0.1f) else SurfaceCard
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    selectedServices = if (it) selectedServices + service.id else selectedServices - service.id
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = service.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = sName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Service Areas checklist
            Text(
                text = "${DwarSewaStrings.t("serviceAreas", lang)} *",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LOCALITIES.take(4).forEach { loc ->
                        val isChecked = selectedAreas.contains(loc)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selectedAreas = if (isChecked) selectedAreas - loc else selectedAreas + loc
                            }
                        ) {
                            Checkbox(checked = isChecked, onCheckedChange = {
                                selectedAreas = if (it) selectedAreas + loc else selectedAreas - loc
                            })
                            Text(loc, fontSize = 13.sp)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LOCALITIES.drop(4).forEach { loc ->
                        val isChecked = selectedAreas.contains(loc)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selectedAreas = if (isChecked) selectedAreas - loc else selectedAreas + loc
                            }
                        ) {
                            Checkbox(checked = isChecked, onCheckedChange = {
                                selectedAreas = if (it) selectedAreas + loc else selectedAreas - loc
                            })
                            Text(loc, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Experience Years
            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it.filter { ch -> ch.isDigit() } },
                label = { Text("${DwarSewaStrings.t("experience", lang)} *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("input_reg_experience"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(DwarSewaStrings.t("bio", lang)) },
                placeholder = { Text("Details about work experience and certifications...") },
                modifier = Modifier.fillMaxWidth().testTag("input_reg_bio"),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 4
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.trim().length < 2) {
                    errorMessage = "Please enter your name"
                    return@Button
                }
                if (address.trim().length < 5) {
                    errorMessage = "Please enter complete address (min 5 characters)"
                    return@Button
                }

                if (role == Role.CUSTOMER) {
                    DwarSewaRepository.completeCustomerRegistration(
                        name = name,
                        address = address,
                        locality = selectedLocality
                    )
                    onComplete(Role.CUSTOMER)
                } else {
                    if (selectedServices.isEmpty()) {
                        errorMessage = "Please select at least one service offered"
                        return@Button
                    }
                    if (selectedAreas.isEmpty()) {
                        errorMessage = "Please select at least one service area in Ranchi"
                        return@Button
                    }
                    val exp = experience.toIntOrNull() ?: 1
                    DwarSewaRepository.completeProviderRegistration(
                        name = name,
                        address = address,
                        services = selectedServices.toList(),
                        areas = selectedAreas.toList(),
                        experience = exp,
                        bio = bio
                    )
                    onComplete(Role.PROVIDER)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_submit_registration"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (role == Role.PROVIDER) SecondaryTeal else PrimaryOrange
            )
        ) {
            Text(
                text = DwarSewaStrings.t("register", lang),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("btn_cancel_registration")
        ) {
            Text(
                text = DwarSewaStrings.t("cancel", lang),
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
