package com.corestack.khidmatai.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corestack.khidmatai.core.domain.model.AiOrbState
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultUnavailableScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onRetry: () -> Unit,
    onBackToHome: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val unavailResult = (state.requestState as? RequestState.Unavailable)?.result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WarningLight)
                .padding(MaterialTheme.spacing.large)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AiOrbView(state = AiOrbState.IDLE, size = MaterialTheme.spacing.large)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(text = s.resultUnavailTitle, style = AppTypography.titleLarge, color = Warning)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                Text(
                    text = unavailResult?.message ?: s.resultUnavailDesc,
                    style = AppTypography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text("🔍", style = AppTypography.displayLarge, modifier = Modifier.padding(MaterialTheme.spacing.medium))

        Card(
            shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
            colors = CardDefaults.cardColors(containerColor = Surface),
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium)
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                Text(s.resultUnavailWhatHappened, style = AppTypography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    text = unavailResult?.error ?: s.resultUnavailWhatHappenedDesc,
                    style = AppTypography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Text(s.resultUnavailSuggestion, style = AppTypography.titleLarge, color = TextPrimary)
                Text(s.resultUnavailSuggestionDesc, style = AppTypography.bodyLarge, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
            Button(
                onClick = {
                    viewModel.onAction(ServiceRequestIntent.SubmitRequest)
                    onRetry()
                },
                modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(s.resultUnavailBtnRetry, color = Surface, style = AppTypography.labelMedium)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumSmall))
            OutlinedButton(
                onClick = {
                    viewModel.onAction(ServiceRequestIntent.Reset)
                    onBackToHome()
                },
                modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall)
            ) {
                Text(s.resultUnavailBtnDifferent, color = TextPrimary, style = AppTypography.labelMedium)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        }
    }
}
