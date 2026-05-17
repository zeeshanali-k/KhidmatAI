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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.components.NextStepCard
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultSuccessScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onViewBookingDetails: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val requestState = state.requestState

    if (requestState !is RequestState.Success) {
        return // Handle unexpectedly missing state or navigate back
    }

    val result = requestState.result
    val isEmergency = result.urgency == "emergency"
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        // Status Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isEmergency) ErrorLight else SuccessLight)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    AiOrbView(state = AiOrbState.DONE, size = 24.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEmergency) "EMERGENCY BOOKING" else "Booking Confirmed!",
                        style = AppTypography.titleLarge,
                        color = if (isEmergency) Error else Success
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.message,
                        style = AppTypography.bodyLarge,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[${result.bookingId}]",
                        style = AppTypography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // AI Decision Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Primary))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🤖 Kyun chuna? (AI Decision)", style = AppTypography.labelMedium, color = Primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.provider?.reasoning ?: "AI selected the best provider.",
                                style = AppTypography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Score: 12.16 • Ranked #1 of 3 providers", style = AppTypography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Provider Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
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
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(result.provider?.name ?: "Unknown", style = AppTypography.titleLarge, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("⭐ ${result.provider?.rating ?: "N/A"}", style = AppTypography.labelMedium, color = Warning)
                                }
                                Text(
                                    "${result.detectedService?.replace("_", " ")?.uppercase()} • ${result.provider?.distanceKm ?: 0f} km away • ${result.provider?.experienceYears ?: 0} yrs exp",
                                    style = AppTypography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { /* Call */ },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Text("📞 Call Now", color = TextPrimary)
                            }
                            OutlinedButton(
                                onClick = { /* WhatsApp */ },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Text("💬 WhatsApp", color = TextPrimary)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Map Mock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Border),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍 Map View", color = TextSecondary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Appointment Details
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📅 Appointment Details", style = AppTypography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Border)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        result.appointment?.let { apt ->
                            Text("Booking ID: ${apt.bookingId}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Time: ${apt.timeDisplay}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Location: ${apt.address}", style = AppTypography.bodySmall, color = TextSecondary)
                            Text("Cost: ${apt.currency} ${apt.costPerHour} / hr", style = AppTypography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Agle Steps", style = AppTypography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        items(result.nextSteps) { step ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                NextStepCard(step = step, onActionClick = { /* Handle action */ })
            }
        }
        
        item {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { result.bookingId?.let { onViewBookingDetails(it) } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Text("View Full Booking Details", color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        viewModel.handleIntent(com.corestack.khidmatai.ui.home.ServiceRequestIntent.Reset)
                        onBackToHome()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Back to Home", color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
