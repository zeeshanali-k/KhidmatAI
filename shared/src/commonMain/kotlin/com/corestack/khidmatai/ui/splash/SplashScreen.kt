package com.corestack.khidmatai.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.core.domain.model.LocationPermissionStatus
import com.corestack.khidmatai.core.domain.repository.AuthRepository
import com.corestack.khidmatai.domain.location.LocationService
import com.corestack.khidmatai.ui.theme.AppTypography
import com.corestack.khidmatai.ui.theme.Primary
import com.corestack.khidmatai.ui.theme.PrimaryDark
import com.corestack.khidmatai.ui.theme.PrimaryLight
import com.corestack.khidmatai.ui.theme.Surface
import com.corestack.khidmatai.ui.theme.spacing
import khidmatai.shared.generated.resources.Res
import khidmatai.shared.generated.resources.logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val locationService = koinInject<LocationService>()
    val authRepository = koinInject<AuthRepository>()

    // --- Animation state ---
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    // Track whether we've checked permission so we navigate only once
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 1. Animate logo in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = EaseOutBack)
        )
        // 2. Fade in app name
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        // 3. Fade in tagline
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        // 4. Brief hold so splash feels intentional, then check permission
        delay(800L)

        if (!checked) {
            checked = true
            val status = locationService.checkPermission()
            if (status == LocationPermissionStatus.GRANTED) {
                if (authRepository.isLoggedIn()) {
                    onNavigateToHome()
                } else {
                    onNavigateToLogin()
                }
            } else {
                onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PrimaryDark, Primary, PrimaryLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(MaterialTheme.spacing.large)
        ) {
            // Logo orb
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(logoScale.value)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(Res.drawable.logo), "")
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // App name
            Text(
                text = "KhidmatAI",
                style = AppTypography.displayLarge,
                color = Surface.copy(alpha = textAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Tagline
            Text(
                text = "Your AI-powered service companion",
                style = AppTypography.bodyLarge,
                color = Surface.copy(alpha = taglineAlpha.value * 0.8f),
                textAlign = TextAlign.Center
            )
        }

        // Bottom powered-by label
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered by AI Seekho",
                style = AppTypography.labelMedium,
                color = Surface.copy(alpha = taglineAlpha.value * 0.6f)
            )
        }
    }
}
