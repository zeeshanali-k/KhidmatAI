package com.corestack.khidmatai.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultUnavailableScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onRetry: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WarningLight)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AiOrbView(state = AiOrbState.IDLE, size = 24.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Koi Provider Available Nahi",
                    style = AppTypography.titleLarge,
                    color = Warning
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Filhal is area mein service available nahi hai.",
                    style = AppTypography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text("🔍", style = AppTypography.displayLarge, modifier = Modifier.padding(16.dp))
        
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kya hua?", style = AppTypography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Koi verified provider nahi mila.", style = AppTypography.bodyLarge, color = TextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Mashwara:", style = AppTypography.titleLarge, color = TextPrimary)
                Text("Kuch der baad dobara try karein.", style = AppTypography.bodyLarge, color = TextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = {
                    viewModel.handleIntent(com.corestack.khidmatai.ui.home.ServiceRequestIntent.SubmitRequest)
                    onRetry()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("🔄 Retry Request", color = Surface, style = AppTypography.labelMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    viewModel.handleIntent(com.corestack.khidmatai.ui.home.ServiceRequestIntent.Reset)
                    onBackToHome()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Different Service", color = TextPrimary, style = AppTypography.labelMedium)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
