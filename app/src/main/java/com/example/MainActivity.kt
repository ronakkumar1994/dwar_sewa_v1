package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.DwarSewaRepository
import com.example.model.Role
import com.example.theme.DwarSewaTheme
import com.example.ui.screens.*

sealed class AppDestination {
    object Auth : AppDestination()
    data class Registration(val role: Role) : AppDestination()
    object CustomerHome : AppDestination()
    data class ServiceDetail(val serviceId: String) : AppDestination()
    data class OfferDetail(val offerId: String) : AppDestination()
    object CustomerAccount : AppDestination()
    object ProviderHome : AppDestination()
    object Admin : AppDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DwarSewaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DwarSewaNavigation()
                }
            }
        }
    }
}

@Composable
fun DwarSewaNavigation() {
    val currentUser by DwarSewaRepository.currentUser.collectAsState()

    var currentDestination by remember {
        mutableStateOf<AppDestination>(
            when (currentUser?.role) {
                Role.CUSTOMER -> AppDestination.CustomerHome
                Role.PROVIDER -> AppDestination.ProviderHome
                Role.ADMIN -> AppDestination.Admin
                null -> AppDestination.Auth
            }
        )
    }

    when (val dest = currentDestination) {
        is AppDestination.Auth -> {
            AuthScreen(
                onLoginSuccess = { role ->
                    currentDestination = when (role) {
                        Role.CUSTOMER -> AppDestination.CustomerHome
                        Role.PROVIDER -> AppDestination.ProviderHome
                        Role.ADMIN -> AppDestination.Admin
                    }
                },
                onNavigateToRegistration = { role ->
                    currentDestination = AppDestination.Registration(role)
                }
            )
        }

        is AppDestination.Registration -> {
            BackHandler {
                currentDestination = AppDestination.Auth
            }
            RegistrationScreen(
                role = dest.role,
                onComplete = { role ->
                    currentDestination = when (role) {
                        Role.CUSTOMER -> AppDestination.CustomerHome
                        Role.PROVIDER -> AppDestination.ProviderHome
                        Role.ADMIN -> AppDestination.Admin
                    }
                },
                onCancel = {
                    currentDestination = AppDestination.Auth
                }
            )
        }

        is AppDestination.CustomerHome -> {
            CustomerHomeScreen(
                onNavigateToService = { serviceId ->
                    currentDestination = AppDestination.ServiceDetail(serviceId)
                },
                onNavigateToOffer = { offerId ->
                    currentDestination = AppDestination.OfferDetail(offerId)
                },
                onNavigateToBookings = {
                    currentDestination = AppDestination.CustomerAccount
                },
                onNavigateToSupport = {
                    currentDestination = AppDestination.CustomerAccount
                },
                onNavigateToProfile = {
                    currentDestination = AppDestination.CustomerAccount
                },
                onNavigateToReferral = {
                    currentDestination = AppDestination.CustomerAccount
                }
            )
        }

        is AppDestination.ServiceDetail -> {
            BackHandler {
                currentDestination = AppDestination.CustomerHome
            }
            ServiceDetailScreen(
                serviceId = dest.serviceId,
                onBack = { currentDestination = AppDestination.CustomerHome },
                onBookingCreated = { currentDestination = AppDestination.CustomerAccount }
            )
        }

        is AppDestination.OfferDetail -> {
            BackHandler {
                currentDestination = AppDestination.CustomerHome
            }
            OfferDetailScreen(
                offerId = dest.offerId,
                onBack = { currentDestination = AppDestination.CustomerHome },
                onNavigateToService = { serviceId ->
                    currentDestination = AppDestination.ServiceDetail(serviceId)
                }
            )
        }

        is AppDestination.CustomerAccount -> {
            BackHandler {
                currentDestination = AppDestination.CustomerHome
            }
            CustomerAccountScreen(
                onBackToHome = { currentDestination = AppDestination.CustomerHome },
                onLogout = { currentDestination = AppDestination.Auth },
                onNavigateToEditRegistration = {
                    currentDestination = AppDestination.Registration(Role.CUSTOMER)
                }
            )
        }

        is AppDestination.ProviderHome -> {
            ProviderHomeScreen(
                onNavigateToEditRegistration = {
                    currentDestination = AppDestination.Registration(Role.PROVIDER)
                },
                onLogout = { currentDestination = AppDestination.Auth }
            )
        }

        is AppDestination.Admin -> {
            AdminScreen(
                onLogout = { currentDestination = AppDestination.Auth }
            )
        }
    }
}
