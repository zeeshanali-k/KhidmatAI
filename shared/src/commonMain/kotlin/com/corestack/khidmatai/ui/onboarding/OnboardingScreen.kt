package com.corestack.khidmatai.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.ui.theme.*

@Composable
fun OnboardingScreen(
    onLocationGranted: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
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
                text = "Pehle apni location batayein",
                style = AppTypography.displayLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Taake hum aapke qareeb ke\nbest service providers dhundh sakein",
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
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("📍 Location use karne ki ijazat dein", color = Surface, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sirf service matching ke liye use hogi.\nKoi data share nahi hoga.",
                style = AppTypography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onSkip) {
                Text("Skip for now", color = TextSecondary, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
