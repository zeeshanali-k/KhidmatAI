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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.RequestState
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
    val state by viewModel.uiState.collectAsState()
    val requestState = state.requestState

    val result = (requestState as? RequestState.Success)?.result

    if (result == null || result.bookingId != bookingId) {
        Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            Text("Booking Not Found for demo")
            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Go Back")
            }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Background)) {
        item {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "←", 
                    modifier = Modifier.clickable { onBack() }.padding(8.dp),
                    style = AppTypography.titleLarge, 
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Booking Detail", style = AppTypography.titleLarge, modifier = Modifier.weight(1f))
                Text("📤", modifier = Modifier.clickable { /* Share */ }.padding(8.dp))
            }
        }

        item {
            // Status Banner
            Box(modifier = Modifier.fillMaxWidth().background(SuccessLight).padding(16.dp)) {
                Text("Confirmed", color = Success, style = AppTypography.labelMedium)
            }
        }
        
        item {
            Column(modifier = Modifier.padding(16.dp)) {
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
                                    "${result.detectedService?.replace("_", " ")?.uppercase()} • ${result.provider?.distanceKm ?: 0f} km away",
                                    style = AppTypography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Follow up card
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🔔 Follow-up Info", style = AppTypography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Border)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reminder set: ✅ Yes", style = AppTypography.bodySmall, color = TextSecondary)
                        Text("Reminder time: 09:30 AM", style = AppTypography.bodySmall, color = TextSecondary)
                        Text("Status: Booking Confirmed", style = AppTypography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }
        
        // Agent Trace Accordion
        item {
            var expanded by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🤖 AI Agent Log", style = AppTypography.titleLarge, color = TextPrimary)
                    Text(if (expanded) "▼" else "▶", color = TextSecondary)
                }
                
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        result.trace.forEachIndexed { index, traceItem ->
                            TraceRowComponent(item = traceItem, isLast = index == result.trace.size - 1)
                        }
                    }
                }
            }
        }
    }
}
