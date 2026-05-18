package com.corestack.khidmatai.ui.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.components.NextStepCard
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultSuccessScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onViewBookingDetails: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requestState = state.requestState

    if (requestState !is RequestState.Success) {
        return // Handle unexpectedly missing state or navigate back
    }

    val result = requestState.result
    val isEmergency = result.urgency == "emergency"
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background).windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Status Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isEmergency) ErrorLight else SuccessLight)
                    .padding(MaterialTheme.spacing.large)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    AiOrbView(state = AiOrbState.DONE, size = MaterialTheme.spacing.large)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = if (isEmergency) stringResource(Res.string.result_success_emergency) else stringResource(Res.string.result_success_title),
                        style = AppTypography.titleLarge,
                        color = if (isEmergency) Error else Success
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                    Text(
                        text = result.message,
                        style = AppTypography.bodyLarge,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = "[${result.bookingId}]",
                        style = AppTypography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                // AI Decision Card
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = PrimaryLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(MaterialTheme.spacing.extraSmall).fillMaxHeight().background(Primary))
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                            Text(stringResource(Res.string.result_success_ai_decision), style = AppTypography.labelMedium, color = Primary)
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = result.provider?.reasoning ?: stringResource(Res.string.result_success_ai_reasoning_fallback),
                                style = AppTypography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text("Score: 12.16 • Ranked #1 of 3 providers", style = AppTypography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Provider Card
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(MaterialTheme.spacing.xxl)
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    result.provider?.name?.take(1) ?: "P",
                                    color = Surface,
                                    style = AppTypography.titleLarge
                                )
                            }
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(result.provider?.name ?: "Unknown", style = AppTypography.titleLarge, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                    Text("⭐ ${result.provider?.rating ?: "N/A"}", style = AppTypography.labelMedium, color = Warning)
                                }
                                Text(
                                    "${result.detectedService?.replace("_", " ")?.uppercase()} • ${result.provider?.distanceKm ?: 0f} km away • ${result.provider?.experienceYears ?: 0} yrs exp",
                                    style = AppTypography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                            OutlinedButton(
                                onClick = { /* Call */ },
                                modifier = Modifier.weight(1f).height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                                shape = RoundedCornerShape(MaterialTheme.spacing.small),
                                border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border)
                            ) {
                                Text(stringResource(Res.string.result_success_call_now), color = TextPrimary)
                            }
                            OutlinedButton(
                                onClick = { /* WhatsApp */ },
                                modifier = Modifier.weight(1f).height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                                shape = RoundedCornerShape(MaterialTheme.spacing.small),
                                border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border)
                            ) {
                                Text(stringResource(Res.string.result_success_whatsapp), color = TextPrimary)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                
                // Map Mock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxl * 4) // Approx 200dp
                        .clip(RoundedCornerShape(MaterialTheme.spacing.mediumSmall))
                        .background(Border),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(Res.string.result_success_map_view), color = TextSecondary)
                }
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                
                // Appointment Details
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Text(stringResource(Res.string.result_success_appointment_details), style = AppTypography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        HorizontalDivider(color = Border)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        
                        result.appointment?.let { apt ->
                            Text("Booking ID: ${apt.bookingId}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Time: ${apt.timeDisplay}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Location: ${apt.address}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Cost: ${apt.currency} ${apt.costPerHour} / hr", style = AppTypography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                
                Text(stringResource(Res.string.result_success_next_steps), style = AppTypography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }
        }
        
        items(result.nextSteps) { step ->
            Box(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)) {
                NextStepCard(step = step, onActionClick = { /* Handle action */ })
            }
        }
        
        item {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                OutlinedButton(
                    onClick = { result.bookingId?.let { onViewBookingDetails(it) } },
                    modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border)
                ) {
                    Text(stringResource(Res.string.result_success_view_details), color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                TextButton(
                    onClick = {
                        viewModel.onAction(com.corestack.khidmatai.ui.home.ServiceRequestIntent.Reset)
                        onBackToHome()
                    },
                    modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium)
                ) {
                    Text(stringResource(Res.string.result_success_back_home), color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            }
        }
    }
}
