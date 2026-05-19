package com.corestack.khidmatai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.corestack.khidmatai.data.location.LocationPreferences
import com.corestack.khidmatai.ui.BookingDetail
import com.corestack.khidmatai.ui.Splash
import com.corestack.khidmatai.ui.Login
import com.corestack.khidmatai.ui.Register
import com.corestack.khidmatai.ui.Bookings
import com.corestack.khidmatai.ui.Home
import com.corestack.khidmatai.ui.LocationPicker
import com.corestack.khidmatai.ui.Onboarding
import com.corestack.khidmatai.ui.Profile
import com.corestack.khidmatai.ui.ServiceRequestProcessing
import com.corestack.khidmatai.ui.ServiceResultSuccess
import com.corestack.khidmatai.ui.ServiceResultUnavailable
import com.corestack.khidmatai.ui.VoiceInput
import com.corestack.khidmatai.ui.bookings.BookingDetailScreen
import com.corestack.khidmatai.ui.bookings.BookingsScreen
import com.corestack.khidmatai.ui.auth.LoginScreen
import com.corestack.khidmatai.ui.auth.RegisterScreen
import com.corestack.khidmatai.ui.home.HomeScreen
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.location.LocationPickerScreen
import com.corestack.khidmatai.ui.onboarding.OnboardingScreen
import com.corestack.khidmatai.ui.processing.ProcessingScreen
import com.corestack.khidmatai.ui.profile.ProfileScreen
import com.corestack.khidmatai.ui.result.ResultSuccessScreen
import com.corestack.khidmatai.ui.result.ResultUnavailableScreen
import com.corestack.khidmatai.ui.theme.AppTheme
import com.corestack.khidmatai.ui.theme.EnglishStrings
import com.corestack.khidmatai.ui.theme.LocalAppStrings
import com.corestack.khidmatai.ui.theme.UrduStrings
import com.corestack.khidmatai.ui.splash.SplashScreen
import com.corestack.khidmatai.ui.voice.VoiceInputScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    AppTheme {
        var selectedLanguage by remember { mutableStateOf("EN") }
        val strings = when (selectedLanguage) {
            "اردو" -> UrduStrings
            else -> EnglishStrings
        }
        val locationPreferences = koinInject<LocationPreferences>()

        CompositionLocalProvider(LocalAppStrings provides strings) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Splash,
            ) {
                composable<Splash> {
                    SplashScreen(
                        onNavigateToHome = {
                            navController.navigate(Login) {
                                popUpTo(Splash) { inclusive = true }
                            }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate(Onboarding) {
                                popUpTo(Splash) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Onboarding> {
                    OnboardingScreen(
                        onLocationGranted = { address ->
                            locationPreferences.detectedLocation = address
                            navController.navigate(Login) {
                                popUpTo(Onboarding) { inclusive = true }
                            }
                        },
                        onSkip = {
                            navController.navigate(Login) {
                                popUpTo(Onboarding) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Login> {
                    LoginScreen(
                        onNavigateToRegister = {
                            navController.navigate(Register)
                        },
                        onLoginSuccess = {
                            navController.navigate(Home) {
                                popUpTo(Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Register> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.popBackStack()
                        },
                        onRegisterSuccess = {
                            navController.navigate(Home) {
                                popUpTo(Login) { inclusive = true } // Clear up to Login since it came from there
                            }
                        }
                    )
                }

                composable<Home> { homeEntry ->
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToProcessing = {
                            navController.navigate(ServiceRequestProcessing)
                        },
                        onNavigateToBookings = {
                            navController.navigate(Bookings)
                        },
                        onNavigateToVoice = {
                            navController.navigate(VoiceInput)
                        },
                        onNavigateToProfile = {
                            navController.navigate(Profile)
                        },
                        onNavigateToLocationPicker = {
                            navController.navigate(LocationPicker)
                        },
                        onLanguageChange = { selectedLanguage = it }
                    )
                }

                composable<LocationPicker> {
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

                    LocationPickerScreen(
                        currentLocation = homeState.location,
                        onLocationSelected = { loc ->
                            locationPreferences.detectedLocation = loc
                            homeViewModel.onAction(ServiceRequestIntent.UpdateLocation(loc))
                            navController.popBackStack()
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable<VoiceInput> {
                    VoiceInputScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<ServiceRequestProcessing> {
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    ProcessingScreen(
                        viewModel = homeViewModel,
                        onNavigateToSuccess = {
                            navController.navigate(ServiceResultSuccess) {
                                popUpTo(Home) { inclusive = false }
                            }
                        },
                        onNavigateToUnavailable = {
                            navController.navigate(ServiceResultUnavailable) {
                                popUpTo(Home) { inclusive = false }
                            }
                        }
                    )
                }

                composable<ServiceResultSuccess> {
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    ResultSuccessScreen(
                        viewModel = homeViewModel,
                        onViewBookingDetails = { bookingId ->
                            navController.navigate(BookingDetail(bookingId))
                        },
                        onBackToHome = {
                            navController.navigate(Home) {
                                popUpTo(Home) { inclusive = true }
                            }
                        }
                    )
                }

                composable<ServiceResultUnavailable> {
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    ResultUnavailableScreen(
                        viewModel = homeViewModel,
                        onRetry = {
                            navController.navigate(ServiceRequestProcessing) {
                                popUpTo(ServiceResultUnavailable) { inclusive = true }
                            }
                        },
                        onBackToHome = {
                            navController.navigate(Home) { popUpTo(0) }
                        }
                    )
                }

                composable<BookingDetail> { backStackEntry ->
                    val bookingDetail = backStackEntry.toRoute<BookingDetail>()
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    BookingDetailScreen(
                        bookingId = bookingDetail.bookingId,
                        viewModel = homeViewModel,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<Bookings> { bookingsEntry ->
                    val homeEntry = remember(navController) {
                        navController.getBackStackEntry<Home>()
                    }
                    val homeViewModel: ServiceRequestViewModel = koinViewModel(viewModelStoreOwner = homeEntry)
                    BookingsScreen(
                        viewModel = homeViewModel,
                        onNavigate = { route ->
                            when (route) {
                                "home" -> navController.navigate(Home) { popUpTo(0) }
                                "profile" -> navController.navigate(Profile)
                            }
                        },
                        onBookingClick = { bookingId ->
                            navController.navigate(BookingDetail(bookingId))
                        }
                    )
                }

                composable<Profile> {
                    ProfileScreen(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = { selectedLanguage = it },
                        onNavigate = { route ->
                            when (route) {
                                "home" -> navController.navigate(Home) { popUpTo(0) }
                                "bookings" -> navController.navigate(Bookings)
                            }
                        }
                    )
                }
            }
        }
    }
}
