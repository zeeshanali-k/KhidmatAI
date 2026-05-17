package com.corestack.khidmatai.ui.processing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.components.TraceRowComponent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProcessingScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigateToSuccess: () -> Unit,
    onNavigateToUnavailable: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val requestState = state.requestState

    val isEmergency = state.urgency == "emergency"
    val backgroundColor = if (isEmergency) EmergencyBg else Background
    
    // Flash effect state
    var showFlash by remember { mutableStateOf(false) }

    LaunchedEffect(requestState) {
        if (requestState is RequestState.Success) {
            showFlash = true
            delay(600)
            onNavigateToSuccess()
        } else if (requestState is RequestState.Unavailable) {
            onNavigateToUnavailable()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            AiOrbView(
                state = if (requestState is RequestState.Success) AiOrbState.DONE else AiOrbState.THINKING,
                size = 48.dp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isEmergency) "Emergency Request — Priority Processing 🚨" else "Agent chal raha hai...",
                style = AppTypography.titleLarge,
                color = if (isEmergency) Error else TextPrimary
            )
            
            Text(
                text = "AI is orchestrating your request",
                style = AppTypography.bodySmall,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val traces = if (requestState is RequestState.Processing) requestState.traces else emptyList()
            val completedCount = traces.count { it.status == "completed" }
            val totalCount = traces.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Stage $completedCount of $totalCount", style = AppTypography.bodySmall)
                Text("${(progress * 100).toInt()}%", style = AppTypography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isEmergency) Error else Primary,
                trackColor = Border
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(traces) { index, item ->
                    TraceRowComponent(item = item, isLast = index == traces.size - 1)
                }
            }
        }
        
        // Full screen flash for success
        AnimatedVisibility(
            visible = showFlash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Success.copy(alpha = 0.8f))
            )
        }
    }
}
