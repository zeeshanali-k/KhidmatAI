package com.corestack.khidmatai.admin.ui.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.ui.components.*
import com.corestack.khidmatai.core.domain.model.AdminRequest
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RequestsScreen(navController: NavController) {
    val vm: RequestsViewModel = koinViewModel()
    val state by vm.listState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenHeader(title = "Requests", subtitle = "User service request history")
        }

        when (val s = state) {
            is com.corestack.khidmatai.core.domain.model.AdminState.Loading -> item { LoadingBox(Modifier.height(200.dp)) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Error -> item { ErrorBox(s.message, onRetry = vm::loadAll) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Success -> {
                if (s.data.isEmpty()) {
                    item { Text("No requests found.", color = TextSecondary, fontSize = 13.sp) }
                } else {
                    items(s.data) { req ->
                        RequestRow(req) {
                            navController.navigate("requests/${req.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestRow(req: com.corestack.khidmatai.core.domain.model.AdminRequest, onClick: () -> Unit) {
    AdminCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(req.rawQuery, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("User: ${req.userId}", color = TextSecondary, fontSize = 12.sp)
                    Text("Urgency: ${req.urgency}", color = TextSecondary, fontSize = 12.sp)
                    req.intent?.let { Text("Intent: $it", color = TextSecondary, fontSize = 12.sp) }
                }
                Text(req.createdAt, color = TextSecondary, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(req.status)
                Text("${req.trace.size} trace steps", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}
