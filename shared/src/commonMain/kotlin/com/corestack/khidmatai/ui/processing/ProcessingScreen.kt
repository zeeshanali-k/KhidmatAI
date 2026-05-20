package com.corestack.khidmatai.ui.processing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.corestack.khidmatai.core.domain.model.AiOrbState
import com.corestack.khidmatai.core.domain.model.RequestState
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
    onNavigateToUnavailable: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requestState = state.requestState

    val isEmergency = state.urgency == "emergency"
    val backgroundColor = if (isEmergency) EmergencyBg else Background

    var showFlash by remember { mutableStateOf(false) }

    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestState.Success -> {
                showFlash = true
                delay(600)
                onNavigateToSuccess()
            }
            is RequestState.Unavailable -> onNavigateToUnavailable()
            is RequestState.Error       -> onNavigateToUnavailable()
            is RequestState.Idle        -> onNavigateBack()
            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxl))

            AiOrbView(
                state = if (requestState is RequestState.Success) AiOrbState.DONE else AiOrbState.THINKING,
                size = MaterialTheme.spacing.xxl
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = if (isEmergency) s.processingEmergencyTitle else s.processingTitle,
                style = AppTypography.titleLarge,
                color = if (isEmergency) Error else TextPrimary
            )

            val planMsg = (requestState as? RequestState.Processing)?.planMessage
            Text(
                text = planMsg ?: s.processingDesc,
                style = AppTypography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            val traces =
                if (requestState is RequestState.Processing) requestState.traces else emptyList()
            val completedCount = traces.count { it.status == "completed" }
            val totalCount = traces.size
            val progress =
                if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Stage $completedCount of $totalCount", style = AppTypography.bodySmall)
                Text("${(progress * 100).toInt()}%", style = AppTypography.bodySmall)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.small),
                color = if (isEmergency) Error else Primary,
                trackColor = Border
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                itemsIndexed(traces) { index, item ->
                    TraceRowComponent(item = item, isLast = index == traces.size - 1)
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            OutlinedButton(
                onClick = { viewModel.onAction(ServiceRequestIntent.CancelRequest) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isEmergency) Error else TextSecondary),
                border = BorderStroke(
                    MaterialTheme.spacing.extraSmall / 4,
                    if (isEmergency) Error else TextSecondary
                ),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.medium)
            ) {
                Text(s.processingBtnCancel, style = AppTypography.labelMedium)
            }
        }

        AnimatedVisibility(visible = showFlash, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Success.copy(alpha = 0.8f)))
        }
    }
}
