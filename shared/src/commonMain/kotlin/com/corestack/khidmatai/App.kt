package com.corestack.khidmatai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.corestack.khidmatai.ui.Home
import com.corestack.khidmatai.ui.Onboarding
import com.corestack.khidmatai.ui.Profile
import com.corestack.khidmatai.ui.VoiceInput
import com.corestack.khidmatai.ui.bookings.BookingDetailScreen
import com.corestack.khidmatai.ui.bookings.BookingsScreen
import com.corestack.khidmatai.ui.home.HomeScreen
import com.corestack.khidmatai.ui.onboarding.OnboardingScreen
import com.corestack.khidmatai.ui.processing.ProcessingScreen
import com.corestack.khidmatai.ui.profile.ProfileScreen
import com.corestack.khidmatai.ui.result.ResultSuccessScreen
import com.corestack.khidmatai.ui.result.ResultUnavailableScreen
import com.corestack.khidmatai.ui.theme.AppTheme
import com.corestack.khidmatai.ui.theme.EnglishStrings
import com.corestack.khidmatai.ui.theme.LocalAppStrings
import com.corestack.khidmatai.ui.theme.UrduStrings
import com.corestack.khidmatai.ui.voice.VoiceInputScreen

@Composable
@Preview
fun App() {
    AppTheme {
        var selectedLanguage by remember { mutableStateOf("EN") }
        val strings = when (selectedLanguage) {
            "اردو" -> UrduStrings
            else -> EnglishStrings
        }

        CompositionLocalProvider(LocalAppStrings provides strings) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Onboarding,
            ) {
                composable<Onboarding> {
                    OnboardingScreen(
                        onLocationGranted = {
                            navController.navigate(Home) {
                                popUpTo(Onboarding) { inclusive = true }
                            }
                        },
                        onSkip = {
                            navController.navigate(Home) {
                                popUpTo(Onboarding) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Home> {
                    HomeScreen(
                        onNavigateToProcessing = {
                            navController.navigate(com.corestack.khidmatai.ui.Processing)
                        },
                        onNavigateToBookings = {
                            navController.navigate(com.corestack.khidmatai.ui.Bookings)
                        },
                        onNavigateToVoice = {
                            navController.navigate(VoiceInput)
                        },
                        onNavigateToProfile = {
                            navController.navigate(Profile)
                        },
                        onLanguageChange = { selectedLanguage = it }
                    )
                }

                composable<VoiceInput> {
                    VoiceInputScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<com.corestack.khidmatai.ui.Processing> {
                    ProcessingScreen(
                        onNavigateToSuccess = {
                            navController.navigate(com.corestack.khidmatai.ui.ResultSuccess) {
                                popUpTo(Home) { inclusive = false }
                            }
                        },
                        onNavigateToUnavailable = {
                            navController.navigate(com.corestack.khidmatai.ui.ResultUnavailable) {
                                popUpTo(Home) { inclusive = false }
                            }
                        }
                    )
                }

                composable<com.corestack.khidmatai.ui.ResultSuccess> {
                    ResultSuccessScreen(
                        onViewBookingDetails = { bookingId ->
                            navController.navigate(com.corestack.khidmatai.ui.BookingDetail(bookingId))
                        },
                        onBackToHome = {
                            navController.navigate(Home) {
                                popUpTo(Home) { inclusive = true }
                            }
                        }
                    )
                }

                composable<com.corestack.khidmatai.ui.ResultUnavailable> {
                    ResultUnavailableScreen(
                        onRetry = {
                            navController.navigate(com.corestack.khidmatai.ui.Processing) {
                                popUpTo(com.corestack.khidmatai.ui.ResultUnavailable) { inclusive = true }
                            }
                        },
                        onBackToHome = {
                            navController.navigate(Home) { popUpTo(0) }
                        }
                    )
                }

                composable<com.corestack.khidmatai.ui.BookingDetail> { backStackEntry ->
                    val bookingDetail = backStackEntry.toRoute<com.corestack.khidmatai.ui.BookingDetail>()
                    BookingDetailScreen(
                        bookingId = bookingDetail.bookingId,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<com.corestack.khidmatai.ui.Bookings> {
                    BookingsScreen(
                        onNavigate = { route ->
                            when (route) {
                                "home" -> navController.navigate(Home) { popUpTo(0) }
                                "profile" -> navController.navigate(Profile)
                            }
                        },
                        onBookingClick = { bookingId ->
                            navController.navigate(com.corestack.khidmatai.ui.BookingDetail(bookingId))
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
                                "bookings" -> navController.navigate(com.corestack.khidmatai.ui.Bookings)
                            }
                        }
                    )
                }
            }
        }
    }
}
