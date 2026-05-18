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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.TraceRowComponent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requestState = state.requestState

    val result = (requestState as? RequestState.Success)?.result

    if (result == null || result.bookingId != bookingId) {
        Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.booking_detail_not_found))
            Button(onClick = onBack, modifier = Modifier.padding(top = MaterialTheme.spacing.medium)) {
                Text(stringResource(Res.string.booking_detail_go_back))
            }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Background).windowInsetsPadding(WindowInsets.systemBars)) {
        item {
            // App Bar
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
                Text(stringResource(Res.string.booking_detail_title), style = AppTypography.titleLarge, modifier = Modifier.weight(1f))
                Text("📤", modifier = Modifier.clickable { /* Share */ }.padding(MaterialTheme.spacing.small))
            }
        }

        item {
            // Status Banner
            Box(modifier = Modifier.fillMaxWidth().background(SuccessLight).padding(MaterialTheme.spacing.medium)) {
                Text(stringResource(Res.string.booking_detail_confirmed), color = Success, style = AppTypography.labelMedium)
            }
        }
        
        item {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
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
            Column(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)) {
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                        Text(stringResource(Res.string.booking_detail_followup_info), style = AppTypography.titleLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        HorizontalDivider(color = Border)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        Text(stringResource(Res.string.booking_detail_reminder_set), style = AppTypography.bodySmall, color = TextSecondary)
                        Text(stringResource(Res.string.booking_detail_reminder_time), style = AppTypography.bodySmall, color = TextSecondary)
                        Text(stringResource(Res.string.booking_detail_status_confirmed), style = AppTypography.bodySmall, color = TextSecondary)
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
                    Text(stringResource(Res.string.booking_detail_agent_log), style = AppTypography.titleLarge, color = TextPrimary)
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
