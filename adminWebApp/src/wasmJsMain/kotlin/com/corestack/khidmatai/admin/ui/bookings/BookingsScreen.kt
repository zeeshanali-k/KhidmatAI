package com.corestack.khidmatai.admin.ui.bookings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.AdminRoute
import com.corestack.khidmatai.admin.ui.components.*
import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingsScreen(navController: NavController) {
    val vm = koinViewModel<BookingsViewModel>()
    val state by vm.listState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenHeader(
                title = "Bookings",
                subtitle = "All service bookings"
            )
        }

        when (val s = state) {
            is com.corestack.khidmatai.core.domain.model.AdminState.Loading -> item { LoadingBox(Modifier.height(200.dp)) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Error -> item { ErrorBox(s.message, onRetry = vm::loadAll) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Success -> {
                if (s.data.isEmpty()) {
                    item { Text("No bookings found.", color = TextSecondary, fontSize = 13.sp) }
                } else {
                    items(s.data) { booking ->
                        BookingRow(booking) {
                            navController.navigate("bookings/${booking.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingRow(booking: com.corestack.khidmatai.core.domain.model.AdminBooking, onClick: () -> Unit) {
    AdminCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(booking.id, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("Service: ${booking.serviceType}", color = TextSecondary, fontSize = 12.sp)
                Text("User: ${booking.userId}", color = TextSecondary, fontSize = 12.sp)
                Text("Address: ${booking.address}", color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                StatusChip(booking.status)
                Spacer(Modifier.height(8.dp))
                Text(booking.scheduledAt, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}
