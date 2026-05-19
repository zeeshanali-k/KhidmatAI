package com.corestack.khidmatai.admin.ui.dashboard

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
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(navController: NavController) {
    val vm: DashboardViewModel = koinViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ScreenHeader(
                title = "Dashboard",
                subtitle = "Overview of KhidmatAI platform activity"
            )
        }

        when (val s = state) {
            is com.corestack.khidmatai.core.domain.model.AdminState.Loading -> item { LoadingBox(Modifier.height(200.dp)) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Error -> item { ErrorBox(s.message, onRetry = vm::load) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Success -> {
                val data = s.data

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Total Bookings", data.totalBookings.toString(), Modifier.weight(1f))
                        StatCard("Active Providers", data.activeProviders.toString(), Modifier.weight(1f))
                        StatCard("Total Requests", data.totalRequests.toString(), Modifier.weight(1f))
                    }
                }

                item {
                    Text("Recent Bookings", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                if (data.recentBookings.isEmpty()) {
                    item { Text("No bookings yet.", color = TextSecondary, fontSize = 13.sp) }
                } else {
                    items(data.recentBookings) { booking ->
                        AdminCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(booking.id, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(booking.serviceType, color = TextSecondary, fontSize = 12.sp)
                                }
                                StatusChip(booking.status)
                            }
                        }
                    }
                }

                item {
                    Text("Recent Requests", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                if (data.recentRequests.isEmpty()) {
                    item { Text("No requests yet.", color = TextSecondary, fontSize = 13.sp) }
                } else {
                    items(data.recentRequests) { req ->
                        AdminCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(req.rawQuery, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                                    Text(req.userId, color = TextSecondary, fontSize = 12.sp)
                                }
                                StatusChip(req.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
