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
import com.corestack.khidmatai.ui.components.BadgeVariant
import com.corestack.khidmatai.ui.components.BottomNavBar
import com.corestack.khidmatai.ui.components.StatusBadge
import com.corestack.khidmatai.ui.theme.*

@Composable
fun BookingsScreen(
    onNavigate: (String) -> Unit,
    onBookingClick: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Background,
        bottomBar = {
            BottomNavBar(currentRoute = "bookings", onNavigate = onNavigate)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Text("My Bookings", style = AppTypography.displayLarge, modifier = Modifier.padding(MaterialTheme.spacing.medium))
            
            // Filters
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium), horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                Text("All", color = Primary, style = AppTypography.labelMedium)
                Text("Upcoming", color = TextSecondary, style = AppTypography.labelMedium)
                Text("Completed", color = TextSecondary, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    BookingListItem(
                        service = "AC Technician",
                        providerName = "Kamran Khan",
                        rating = 4.7f,
                        time = "10:30 AM, 17 May",
                        location = "G-13, Islamabad",
                        status = BadgeVariant.UPCOMING,
                        onClick = { onBookingClick("BK-1747391234") }
                    )
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.small).clickable { onClick() }
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
