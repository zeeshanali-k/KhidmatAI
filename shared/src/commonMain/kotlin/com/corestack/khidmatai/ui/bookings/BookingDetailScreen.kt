package com.corestack.khidmatai.ui.bookings

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.NextStepCard
import com.corestack.khidmatai.ui.components.TraceRowComponent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val result = (state.requestState as? RequestState.Success)?.result

    if (result == null || result.bookingId != bookingId) {
        Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(s.bookingDetailNotFound, color = TextSecondary)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Button(onClick = onBack) { Text(s.bookingDetailGoBack) }
            }
        }
        return
    }

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(modifier = Modifier.fillMaxSize().background(Background).windowInsetsPadding(WindowInsets.systemBars)) {
        // App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "←",
                    modifier = Modifier.clickable { onBack() }.padding(MaterialTheme.spacing.small),
                    style = AppTypography.titleLarge,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                Text(s.bookingDetailTitle, style = AppTypography.titleLarge, modifier = Modifier.weight(1f))
                Text(
                    "📤",
                    modifier = Modifier
                        .clickable {
                            clipboardManager.setText(
                                AnnotatedString(
                                    "Booking ${result.bookingId}\n" +
                                    "Provider: ${result.provider?.name}\n" +
                                    "Time: ${result.appointment?.timeDisplay}\n" +
                                    "Location: ${result.appointment?.address}"
                                )
                            )
                        }
                        .padding(MaterialTheme.spacing.small)
                )
            }
        }

        // Status Banner
        item {
            Box(modifier = Modifier.fillMaxWidth().background(SuccessLight).padding(MaterialTheme.spacing.medium)) {
                Text(s.bookingDetailConfirmed, color = Success, style = AppTypography.labelMedium)
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
                            Text(s.bookingDetailAiDecision, style = AppTypography.labelMedium, color = Primary)
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = result.provider?.reasoning ?: s.resultSuccessAiReasoningFallback,
                                style = AppTypography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                color = TextSecondary
                            )
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
                                modifier = Modifier.size(MaterialTheme.spacing.xxl).clip(CircleShape).background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(result.provider?.name?.take(1) ?: "P", color = Surface, style = AppTypography.titleLarge)
                            }
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(result.provider?.name ?: "Unknown", style = AppTypography.titleLarge, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                    Text("⭐ ${result.provider?.rating ?: "N/A"}", style = AppTypography.labelMedium, color = Warning)
                                }
                                Text(
                                    "${result.detectedService?.replace("_", " ")?.uppercase()} • ${result.provider?.distanceKm ?: 0f} km away",
                                    style = AppTypography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Appointment Details Card
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Text(s.bookingDetailAppointmentDetails, style = AppTypography.titleLarge, color = TextPrimary)
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

                // Next Steps
                if (result.nextSteps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    Text(s.bookingDetailNextSteps, style = AppTypography.titleLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    result.nextSteps.forEach { step ->
                        NextStepCard(step = step, onActionClick = {})
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Follow-up Info Card
                val followup = result.followup
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Text(s.bookingDetailFollowupInfo, style = AppTypography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        HorizontalDivider(color = Border)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        Text(
                            if (followup?.reminderScheduled == true) s.bookingDetailReminderYes else s.bookingDetailReminderNo,
                            style = AppTypography.bodySmall,
                            color = TextSecondary
                        )
                        followup?.reminderTimeDisplay?.let {
                            Text("Reminder time: $it", style = AppTypography.bodySmall, color = TextSecondary)
                        }
                        Text(
                            followup?.statusUpdate ?: s.bookingDetailStatusConfirmed,
                            style = AppTypography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Agent Trace Accordion
        item {
            var expanded by remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(s.bookingDetailAgentLog, style = AppTypography.titleLarge, color = TextPrimary)
                    Text(if (expanded) "▼" else "▶", color = TextSecondary)
                }
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.small)) {
                        result.trace.forEachIndexed { index, traceItem ->
                            TraceRowComponent(item = traceItem, isLast = index == result.trace.size - 1)
                        }
                    }
                }
            }
        }
    }
}
