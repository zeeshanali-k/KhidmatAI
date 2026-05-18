package com.corestack.khidmatai.ui.bookings

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.BadgeVariant
import com.corestack.khidmatai.ui.components.BottomNavBar
import com.corestack.khidmatai.ui.components.StatusBadge
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingsScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigate: (String) -> Unit,
    onBookingClick: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activeResult = (state.requestState as? RequestState.Success)?.result
    var selectedFilter by remember { mutableStateOf("all") }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Background,
        bottomBar = {
            BottomNavBar(currentRoute = "bookings", onNavigate = onNavigate)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Text(s.bookingsTitle, style = AppTypography.displayLarge, modifier = Modifier.padding(MaterialTheme.spacing.medium))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                listOf(
                    "all" to s.bookingsFilterAll,
                    "upcoming" to s.bookingsFilterUpcoming,
                    "completed" to s.bookingsFilterCompleted
                ).forEach { (key, label) ->
                    Text(
                        label,
                        color = if (selectedFilter == key) Primary else TextSecondary,
                        style = AppTypography.labelMedium,
                        modifier = Modifier.clickable { selectedFilter = key }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                if (activeResult != null) {
                    item {
                        BookingListItem(
                            service = activeResult.detectedService
                                ?.replace("_", " ")
                                ?.replaceFirstChar { it.uppercase() } ?: "Service",
                            providerName = activeResult.provider?.name ?: "Unknown",
                            rating = activeResult.provider?.rating ?: 0f,
                            time = activeResult.appointment?.timeDisplay ?: "",
                            location = activeResult.appointment?.address ?: "",
                            status = BadgeVariant.UPCOMING,
                            onClick = { activeResult.bookingId?.let { onBookingClick(it) } }
                        )
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.xxl),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📋", style = AppTypography.displayLarge)
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                                Text(s.bookingsEmptyTitle, style = AppTypography.titleLarge, color = TextPrimary)
                                Text(s.bookingsEmptyDesc, style = AppTypography.bodyLarge, color = TextSecondary)
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                                Button(
                                    onClick = { onNavigate("home") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall)
                                ) {
                                    Text(s.bookingsEmptyBtn, color = Surface, style = AppTypography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingListItem(
    service: String,
    providerName: String,
    rating: Float,
    time: String,
    location: String,
    status: BadgeVariant,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.small)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(MaterialTheme.spacing.medium), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(MaterialTheme.spacing.xxl).clip(CircleShape).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🔧", style = AppTypography.titleLarge)
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Text(service, style = AppTypography.titleLarge, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(providerName, style = AppTypography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                    Text("⭐ $rating", style = AppTypography.bodySmall, color = Warning)
                }
                Text("$time • $location", style = AppTypography.bodySmall, color = TextSecondary)
            }
            StatusBadge(variant = status)
        }
    }
}
