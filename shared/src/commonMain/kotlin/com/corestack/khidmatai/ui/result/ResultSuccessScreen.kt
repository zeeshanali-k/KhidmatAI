package com.corestack.khidmatai.ui.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.components.MockPushNotification
import com.corestack.khidmatai.ui.components.NextStepCard
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import com.corestack.khidmatai.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultSuccessScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onViewBookingDetails: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requestState = state.requestState

    if (requestState !is RequestState.Success) return

    val result = requestState.result
    val isEmergency = result.urgency == "emergency"
    val uriHandler = LocalUriHandler.current

    var showNotification by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(3000L)
        showNotification = true
        delay(4000L)
        showNotification = false
    }

    Scaffold(
        Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Background)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                // Status Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isEmergency) ErrorLight else SuccessLight)
                            .padding(MaterialTheme.spacing.large)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AiOrbView(state = AiOrbState.DONE, size = MaterialTheme.spacing.large)
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = if (isEmergency) s.resultSuccessEmergency else s.resultSuccessTitle,
                                style = AppTypography.titleLarge,
                                color = if (isEmergency) Error else Success
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                            Text(
                                text = result.message,
                                style = AppTypography.bodyLarge,
                                color = TextPrimary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
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
                                Box(
                                    modifier = Modifier.width(MaterialTheme.spacing.extraSmall)
                                        .fillMaxHeight().background(Primary)
                                )
                                Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                                    Text(
                                        s.resultSuccessAiDecision,
                                        style = AppTypography.labelMedium,
                                        color = Primary
                                    )
                                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                                    Text(
                                        text = result.provider?.reasoning
                                            ?: s.resultSuccessAiReasoningFallback,
                                        style = AppTypography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
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
                                        modifier = Modifier.size(MaterialTheme.spacing.xxl)
                                            .clip(CircleShape).background(Primary),
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
                                            Text(
                                                result.provider?.name ?: "Unknown",
                                                style = AppTypography.titleLarge,
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                            Text(
                                                "⭐ ${result.provider?.rating ?: "N/A"}",
                                                style = AppTypography.labelMedium,
                                                color = Warning
                                            )
                                        }
                                        Text(
                                            "${
                                                result.detectedService?.replace("_", " ")
                                                    ?.uppercase()
                                            } • ${result.provider?.distanceKm ?: 0f} km away • ${result.provider?.experienceYears ?: 0} yrs exp",
                                            style = AppTypography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                                    OutlinedButton(
                                        onClick = {
                                            result.provider?.phone?.let {
                                                uriHandler.openUri(
                                                    "tel:$it"
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                                        border = BorderStroke(
                                            MaterialTheme.spacing.extraSmall / 4,
                                            Border
                                        )
                                    ) {
                                        Text(s.resultSuccessCallNow, color = TextPrimary)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            result.provider?.phone?.let { phone ->
                                                val digits = phone.replace(Regex("[^\\d]"), "")
                                                uriHandler.openUri("https://wa.me/$digits")
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                                        border = BorderStroke(
                                            MaterialTheme.spacing.extraSmall / 4,
                                            Border
                                        )
                                    ) {
                                        Text(s.resultSuccessWhatsapp, color = TextPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                        // Map placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(MaterialTheme.spacing.mediumSmall))
                                .background(Brush.verticalGradient(listOf(SuccessLight, Border))),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                val step = 40.dp.toPx()
                                val lineColor = Border.copy(alpha = 0.8f)
                                var x = 0f
                                while (x < size.width) {
                                    drawLine(
                                        lineColor,
                                        Offset(x, 0f),
                                        Offset(x, size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    x += step
                                }
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(
                                        lineColor,
                                        Offset(0f, y),
                                        Offset(size.width, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    y += step
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📍", style = AppTypography.displayLarge)
                                Text(
                                    result.appointment?.address ?: s.resultSuccessMapView,
                                    style = AppTypography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
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
                                Text(
                                    s.resultSuccessAppointmentDetails,
                                    style = AppTypography.titleLarge,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                                HorizontalDivider(color = Border)
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                                result.appointment?.let { apt ->
                                    Text(
                                        "Booking ID: ${apt.bookingId}",
                                        style = AppTypography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "Time: ${apt.timeDisplay}",
                                        style = AppTypography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "Location: ${apt.address}",
                                        style = AppTypography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "Cost: ${apt.currency} ${apt.costPerHour} / hr",
                                        style = AppTypography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                        Text(
                            s.resultSuccessNextSteps,
                            style = AppTypography.titleLarge,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    }
                }

                items(result.nextSteps) { step ->
                    Box(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)) {
                        NextStepCard(step = step, onActionClick = { actionValue ->
                            when {
                                actionValue != null && actionValue.startsWith("+") ->
                                    uriHandler.openUri("tel:$actionValue")

                                else -> {}
                            }
                        })
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                        OutlinedButton(
                            onClick = { result.bookingId?.let { onViewBookingDetails(it) } },
                            modifier = Modifier.fillMaxWidth()
                                .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                            shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                            border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border)
                        ) {
                            Text(s.resultSuccessViewDetails, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        TextButton(
                            onClick = {
                                viewModel.onAction(ServiceRequestIntent.Reset)
                                onBackToHome()
                            },
                            modifier = Modifier.fillMaxWidth()
                                .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium)
                        ) {
                            Text(s.resultSuccessBackHome, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
                    }
                }
            }

            MockPushNotification(
                visible = showNotification,
                onTap = { onViewBookingDetails(result.bookingId ?: "") }
            )
        }

    }
}
