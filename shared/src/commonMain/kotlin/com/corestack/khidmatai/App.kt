package com.corestack.khidmatai

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.corestack.khidmatai.ui.Home
import com.corestack.khidmatai.ui.Onboarding
import com.corestack.khidmatai.ui.onboarding.OnboardingScreen
import com.corestack.khidmatai.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val startDestination = Onboarding

        NavHost(
            navController = navController,
            startDestination = startDestination,
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
                com.corestack.khidmatai.ui.home.HomeScreen(
                    onNavigateToProcessing = {
                        navController.navigate(com.corestack.khidmatai.ui.Processing)
                    },
                    onNavigateToBookings = {
                        navController.navigate(com.corestack.khidmatai.ui.Bookings)
                    }
                )
            }
            
            composable<com.corestack.khidmatai.ui.Processing> {
                com.corestack.khidmatai.ui.processing.ProcessingScreen(
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
                com.corestack.khidmatai.ui.result.ResultSuccessScreen(
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
            
            composable<com.corestack.khidmatai.ui.BookingDetail> { backStackEntry ->
                val bookingDetail = backStackEntry.toRoute<com.corestack.khidmatai.ui.BookingDetail>()
                com.corestack.khidmatai.ui.bookings.BookingDetailScreen(
                    bookingId = bookingDetail.bookingId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable<com.corestack.khidmatai.ui.Bookings> {
                com.corestack.khidmatai.ui.bookings.BookingsScreen(
                    onNavigate = { route ->
                        if (route == "home") {
                            navController.navigate(Home) {
                                popUpTo(0)
                            }
                        }
                    },
                    onBookingClick = { bookingId ->
                        navController.navigate(com.corestack.khidmatai.ui.BookingDetail(bookingId))
                    }
                )
            }
        }
    }
}