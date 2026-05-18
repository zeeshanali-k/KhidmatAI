package com.corestack.khidmatai.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    onLocationGranted: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top 40% Illustration (Mocked with Box for now)
        Box(
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

        // Middle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.onboarding_title),
                style = AppTypography.displayLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = stringResource(Res.string.onboarding_desc),
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = onLocationGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + MaterialTheme.spacing.extraSmall),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(stringResource(Res.string.onboarding_btn_location), color = Surface, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            
            Text(
                text = stringResource(Res.string.onboarding_privacy),
                style = AppTypography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            
            TextButton(onClick = onSkip) {
                Text(stringResource(Res.string.onboarding_skip), color = TextSecondary, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        }
    }
}
