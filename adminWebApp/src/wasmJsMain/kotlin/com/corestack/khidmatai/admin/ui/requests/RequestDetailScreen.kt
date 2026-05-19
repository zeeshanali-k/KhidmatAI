package com.corestack.khidmatai.admin.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.ui.components.*
import com.corestack.khidmatai.core.domain.model.AdminState
import com.corestack.khidmatai.core.domain.model.TraceItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RequestDetailScreen(requestId: String, navController: NavController) {
    val vm: RequestsViewModel = koinViewModel()
    val state by vm.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) { vm.loadDetail(requestId) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScreenHeader(
                title = "Request Detail",
                subtitle = requestId,
                action = { PrimaryButton("Back", onClick = { navController.popBackStack() }) }
            )
        }

        when (val s = state) {
            is com.corestack.khidmatai.core.domain.model.AdminState.Loading -> item { LoadingBox(Modifier.height(200.dp)) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Error -> item { ErrorBox(s.message, onRetry = { vm.loadDetail(requestId) }) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Success -> {
                val r = s.data

                item {
                    AdminCard(Modifier.fillMaxWidth()) {
                        LabelValue("ID", r.id)
                        LabelValue("User", r.userId)
                        LabelValue("Query", r.rawQuery)
                        LabelValue("Urgency", r.urgency)
                        LabelValue("Intent", r.intent ?: "—")
                        LabelValue("Language", r.language)
                        r.bookingId?.let { LabelValue("Booking ID", it) }
                        LabelValue("Created", r.createdAt)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("Status: ", color = TextSecondary, fontSize = 13.sp)
                            StatusChip(r.status)
                        }
                    }
                }

                if (r.trace.isNotEmpty()) {
                    item {
                        Text(
                            "Trace (${r.trace.size} steps)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(r.trace) { traceItem ->
                        TraceRow(traceItem)
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceRow(item: com.corestack.khidmatai.core.domain.model.TraceItem) {
    val statusColor = when (item.status) {
        "completed" -> Color(0xFF00E676)
        "failed" -> Color(0xFFEF5350)
        "pending" -> Color(0xFFFFC107)
        else -> Color(0xFF888888)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor, RoundedCornerShape(50))
                .padding(top = 4.dp)
        )
        Column {
            Text(item.stage, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(item.message, color = TextPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(value, color = TextPrimary, fontSize = 13.sp)
    }
}
