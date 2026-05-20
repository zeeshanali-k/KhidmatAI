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
import com.corestack.khidmatai.ui.components.NextStepCard
import com.corestack.khidmatai.ui.components.TraceRowComponent
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: BookingsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(bookingId) {
        viewModel.onAction(BookingsIntent.LoadBookingDetail(bookingId))
    }

    if (state.isDetailLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    val booking = state.activeBooking
    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.detailError ?: s.bookingDetailNotFound, color = TextSecondary)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Button(onClick = onBack) { Text(s.bookingDetailGoBack) }
            }
        }
        return
    }

    val providerName = if (booking.providerId == "p1") "Kamran Khan" else "Verified Provider"
    val providerPhone = "+923001234567"
    val providerRating = 4.7f
    val distanceKm = 1.2f
    val costPerHour = booking.totalCost?.toInt() ?: 1500
    val currency = "PKR"

    val nextSteps = listOf(
        com.corestack.khidmatai.core.domain.model.NextStep(
            id = 1,
            title = s.bookingDetailNextSteps,
            description = "$providerName will contact you for the service details.",
            type = "action",
            actionValue = providerPhone,
            actionLabel = "Call Now"
        ),
        com.corestack.khidmatai.core.domain.model.NextStep(
            id = 2,
            title = "Prepare Area",
            description = "Please ensure the area is accessible for the technician.",
            type = "info",
            actionValue = null,
            actionLabel = null
        )
    )

    val traces = state.activeBookingTraces.ifEmpty {
        listOf(
            com.corestack.khidmatai.core.domain.model.TraceItem("intent_detection", "Request parsed successfully", "completed"),
            com.corestack.khidmatai.core.domain.model.TraceItem("provider_ranking", "$providerName matched as best provider", "completed"),
            com.corestack.khidmatai.core.domain.model.TraceItem("booking_execution", "Booking status is: ${booking.status.uppercase()}", "completed")
        )
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
                                    "Booking ${booking.id}\n" +
                                    "Provider: $providerName\n" +
                                    "Time: ${booking.scheduledAt}\n" +
                                    "Location: ${booking.address}"
                                )
                            )
                        }
                        .padding(MaterialTheme.spacing.small)
                )
            }
        }

        // Status Banner
        item {
            val bannerBg = when (booking.status.lowercase()) {
                "completed", "done" -> SuccessLight
                "cancelled" -> ErrorLight
                else -> SuccessLight
            }
            val bannerText = when (booking.status.lowercase()) {
                "completed", "done" -> "Completed"
                "cancelled" -> "Cancelled"
                else -> s.bookingDetailConfirmed
            }
            val bannerColor = when (booking.status.lowercase()) {
                "completed", "done" -> Success
                "cancelled" -> Error
                else -> Success
            }
            Box(modifier = Modifier.fillMaxWidth().background(bannerBg).padding(MaterialTheme.spacing.medium)) {
                Text(bannerText, color = bannerColor, style = AppTypography.labelMedium)
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
                                text = "$providerName was selected based on rating ($providerRating) and close proximity ($distanceKm km) to your location.",
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
                                Text(providerName.take(1), color = Surface, style = AppTypography.titleLarge)
                            }
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(providerName, style = AppTypography.titleLarge, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                    Text("⭐ $providerRating", style = AppTypography.labelMedium, color = Warning)
                                }
                                Text(
                                    "${booking.serviceType.replace("_", " ").uppercase()} • $distanceKm km away",
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
                        Text("Booking ID: ${booking.id}", style = AppTypography.bodySmall, color = TextSecondary)
                        Text("Time: ${booking.scheduledAt}", style = AppTypography.bodySmall, color = TextSecondary)
                        Text("Location: ${booking.address}", style = AppTypography.bodySmall, color = TextSecondary)
                        Text("Cost: $currency $costPerHour / hr", style = AppTypography.bodySmall, color = TextSecondary)
                    }
                }

                // Next Steps
                if (nextSteps.isNotEmpty() && booking.status.lowercase() in listOf("confirmed", "pending", "upcoming")) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    Text(s.bookingDetailNextSteps, style = AppTypography.titleLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    nextSteps.forEach { step ->
                        NextStepCard(step = step, onActionClick = {})
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Follow-up Info Card
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
                            s.bookingDetailReminderYes,
                            style = AppTypography.bodySmall,
                            color = TextSecondary
                        )
                        Text("Status update: ${booking.status.replaceFirstChar { it.uppercase() }}", style = AppTypography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        // Cancel Button Action
        if (booking.status.lowercase() in listOf("confirmed", "pending", "upcoming")) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onAction(BookingsIntent.CancelBooking(booking.id)) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                        border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Error),
                        shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Booking", style = AppTypography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
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
                        traces.forEachIndexed { index, traceItem ->
                            TraceRowComponent(item = traceItem, isLast = index == traces.size - 1)
                        }
                    }
                }
            }
        }
    }
}
