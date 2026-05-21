package com.corestack.khidmatai.admin.ui.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.ui.components.AdminCard
import com.corestack.khidmatai.admin.ui.components.ErrorBox
import com.corestack.khidmatai.admin.ui.components.LoadingBox
import com.corestack.khidmatai.admin.ui.components.PrimaryButton
import com.corestack.khidmatai.admin.ui.components.ScreenHeader
import com.corestack.khidmatai.admin.ui.components.StatusChip
import com.corestack.khidmatai.admin.ui.components.TextPrimary
import com.corestack.khidmatai.admin.ui.components.TextSecondary
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingDetailScreen(bookingId: String, navController: NavController) {
    val vm = koinViewModel<BookingsViewModel>()
    val state by vm.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(bookingId) { vm.loadDetail(bookingId) }

    Column(Modifier.fillMaxSize().padding(28.dp)) {
        ScreenHeader(
            title = "Booking Detail",
            subtitle = bookingId,
            action = {
                PrimaryButton("Back", onClick = { navController.popBackStack() })
            }
        )

        when (val s = state) {
            is AdminState.Loading -> LoadingBox()
            is AdminState.Error -> ErrorBox(
                s.message,
                onRetry = { vm.loadDetail(bookingId) })

            is AdminState.Success -> {
                val b = s.data
                AdminCard(Modifier.fillMaxWidth()) {
                    LabelValue("Booking ID", b.id)
                    LabelValue("User ID", b.userId)
                    LabelValue("Provider ID", b.providerId)
                    LabelValue("Service", b.serviceType)
                    LabelValue("Address", b.address)
                    LabelValue("Scheduled", b.scheduledAt)
                    LabelValue("Cost", b.totalCost?.let { "PKR ${it.toLong()}/hr" } ?: "—")
                    LabelValue("Created", b.createdAt)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Status: ", color = TextSecondary, fontSize = 13.sp)
                        StatusChip(b.status)
                    }
                }

                if (b.status !in listOf("completed", "cancelled")) {
                    Spacer(Modifier.height(16.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Box {
                            PrimaryButton("Update Status", onClick = { expanded = true })
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                remember { BookingStatus.entries }.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.title, color = TextPrimary) },
                                        onClick = {
                                            expanded = false
                                            vm.updateStatus(bookingId, status.value)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
enum class BookingStatus(
    val value: String,
    val title: String,
) {
    Pending("pending", "Pending"),
    ProviderOnTheWay("provider_on_the_way", "Provider on the way"),
    Arrived("arrived", "Arrived"),
    Started("started", "Started"),
    InProgress("in_progress", "In Progress"),
    Completed("completed", "Completed"),
    Cancelled("cancelled", "Cancelled")
}


@Composable
private fun LabelValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(value, color = TextPrimary, fontSize = 13.sp)
    }
}
