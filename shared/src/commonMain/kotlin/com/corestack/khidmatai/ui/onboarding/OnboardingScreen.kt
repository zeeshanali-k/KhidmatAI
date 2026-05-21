package com.corestack.khidmatai.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.core.domain.model.LocationFetchResult
import com.corestack.khidmatai.core.domain.model.LocationPermissionStatus
import com.corestack.khidmatai.domain.location.LocationService
import com.corestack.khidmatai.ui.location.rememberLocationPermissionState
import com.corestack.khidmatai.ui.notifications.NotificationPermissionStatus
import com.corestack.khidmatai.ui.notifications.rememberNotificationPermissionState
import com.corestack.khidmatai.ui.theme.AppTypography
import com.corestack.khidmatai.ui.theme.Background
import com.corestack.khidmatai.ui.theme.Error
import com.corestack.khidmatai.ui.theme.LocalAppStrings
import com.corestack.khidmatai.ui.theme.Primary
import com.corestack.khidmatai.ui.theme.Surface
import com.corestack.khidmatai.ui.theme.TextSecondary
import com.corestack.khidmatai.ui.theme.spacing
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class OnboardingStep { LOCATION, NOTIFICATION }

@Composable
fun OnboardingScreen(
    onLocationGranted: (String) -> Unit,
    onSkip: () -> Unit
) {
    val s = LocalAppStrings.current
    val locationService = koinInject<LocationService>()
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(OnboardingStep.LOCATION) }
    var isDetecting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resolvedLocation by remember { mutableStateOf<String?>(null) }

    fun fetchAndNavigate() {
        isDetecting = true
        errorMessage = null
        scope.launch {
            when (val result = locationService.fetchCurrentLocation()) {
                is LocationFetchResult.Success -> {
                    resolvedLocation = result.address.displayName
                    // Move to notification step instead of navigating away
                    isDetecting = false
                    currentStep = OnboardingStep.NOTIFICATION
                }
                is LocationFetchResult.PermissionDenied -> {
                    errorMessage = s.onboardingPermissionDenied
                    isDetecting = false
                }
                is LocationFetchResult.Error -> {
                    // Fallback to default location on error — don't block user
                    resolvedLocation = "G-13, Islamabad"
                    isDetecting = false
                    currentStep = OnboardingStep.NOTIFICATION
                }
            }
        }
    }

    val locationPermissionState = rememberLocationPermissionState { status ->
        when (status) {
            LocationPermissionStatus.GRANTED -> fetchAndNavigate()
            LocationPermissionStatus.DENIED -> {
                errorMessage = s.onboardingPermissionDenied
                isDetecting = false
            }
            LocationPermissionStatus.UNKNOWN -> isDetecting = false
        }
    }

    val notifPermissionState = rememberNotificationPermissionState { status ->
        when (status) {
            NotificationPermissionStatus.GRANTED -> {
                // Permission granted — proceed to main app
                onLocationGranted(resolvedLocation ?: "G-13, Islamabad")
            }
            NotificationPermissionStatus.DENIED -> {
                errorMessage = s.onboardingNotifDenied
            }
            NotificationPermissionStatus.UNKNOWN -> { /* no-op */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Step indicator dots ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepDot(isActive = currentStep == OnboardingStep.LOCATION)
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            StepDot(isActive = currentStep == OnboardingStep.NOTIFICATION)
        }

        // ── Animated step content ──
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn())
                    .togetherWith(slideOutHorizontally { -it } + fadeOut())
            },
            modifier = Modifier.weight(1f)
        ) { step ->
            when (step) {
                OnboardingStep.LOCATION -> LocationStep(
                    isDetecting = isDetecting,
                    errorMessage = errorMessage,
                    onRequestPermission = {
                        if (isDetecting) return@LocationStep
                        errorMessage = null
                        when (locationPermissionState.status) {
                            LocationPermissionStatus.GRANTED -> fetchAndNavigate()
                            else -> {
                                isDetecting = true
                                locationPermissionState.launchRequest()
                            }
                        }
                    },
                    onSkip = {
                        resolvedLocation = null
                        currentStep = OnboardingStep.NOTIFICATION
                    }
                )

                OnboardingStep.NOTIFICATION -> NotificationStep(
                    errorMessage = if (currentStep == OnboardingStep.NOTIFICATION) errorMessage else null,
                    onRequestPermission = {
                        errorMessage = null
                        when (notifPermissionState.status) {
                            NotificationPermissionStatus.GRANTED -> {
                                onLocationGranted(resolvedLocation ?: "G-13, Islamabad")
                            }
                            else -> notifPermissionState.launchRequest()
                        }
                    },
                    onSkip = {
                        onLocationGranted(resolvedLocation ?: "G-13, Islamabad")
                    }
                )
            }
        }
    }
}

// ── Step 1: Location ──

@Composable
private fun LocationStep(
    isDetecting: Boolean,
    errorMessage: String?,
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit
) {
    val s = LocalAppStrings.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "📍\n(Map Pin Illustration)",
                style = AppTypography.displayLarge,
                color = Primary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = s.onboardingTitle,
                style = AppTypography.displayLarge,
                color = androidx.compose.ui.graphics.Color(0xFF101828),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = s.onboardingDesc,
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + MaterialTheme.spacing.extraSmall),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isDetecting
            ) {
                if (isDetecting) {
                    CircularProgressIndicator(
                        color = Surface,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(end = MaterialTheme.spacing.small))
                    Text(s.onboardingDetectingLocation, color = Surface, style = AppTypography.labelMedium)
                } else {
                    Text(s.onboardingBtnLocation, color = Surface, style = AppTypography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = AppTypography.bodySmall,
                    color = Error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small)
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            } else {
                Text(
                    text = s.onboardingPrivacy,
                    style = AppTypography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }

            TextButton(onClick = onSkip) {
                Text(s.onboardingSkip, color = TextSecondary, style = AppTypography.labelMedium)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        }
    }
}

// ── Step 2: Notifications ──

@Composable
private fun NotificationStep(
    errorMessage: String?,
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit
) {
    val s = LocalAppStrings.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "🔔\n(Notification Illustration)",
                style = AppTypography.displayLarge,
                color = Primary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = s.onboardingNotifTitle,
                style = AppTypography.displayLarge,
                color = androidx.compose.ui.graphics.Color(0xFF101828),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = s.onboardingNotifDesc,
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + MaterialTheme.spacing.extraSmall),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(s.onboardingBtnNotification, color = Surface, style = AppTypography.labelMedium)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = AppTypography.bodySmall,
                    color = Error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small)
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            } else {
                Text(
                    text = s.onboardingPrivacy,
                    style = AppTypography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }

            TextButton(onClick = onSkip) {
                Text(s.onboardingSkip, color = TextSecondary, style = AppTypography.labelMedium)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        }
    }
}

// ── Step indicator dot ──

@Composable
private fun StepDot(isActive: Boolean) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(if (isActive) 10.dp else 8.dp)
            .clip(CircleShape)
            .background(if (isActive) Primary else TextSecondary.copy(alpha = 0.35f))
    )
}
