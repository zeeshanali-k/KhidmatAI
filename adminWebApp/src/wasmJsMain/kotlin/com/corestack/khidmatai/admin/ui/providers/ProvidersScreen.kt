package com.corestack.khidmatai.admin.ui.providers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.ui.components.*
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProvidersScreen(navController: NavController) {
    val vm: ProvidersViewModel = koinViewModel()
    val state by vm.listState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenHeader(
                title = "Providers",
                subtitle = "Manage service providers",
                action = {
                    PrimaryButton("+ Add Provider", onClick = { navController.navigate("providers/new") })
                }
            )
        }

        when (val s = state) {
            is com.corestack.khidmatai.core.domain.model.AdminState.Loading -> item { LoadingBox(Modifier.height(200.dp)) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Error -> item { ErrorBox(s.message, onRetry = vm::loadAll) }
            is com.corestack.khidmatai.core.domain.model.AdminState.Success -> {
                if (s.data.isEmpty()) {
                    item { Text("No providers found.", color = TextSecondary, fontSize = 13.sp) }
                } else {
                    items(s.data) { provider ->
                        ProviderCard(
                            provider = provider,
                            onEdit = { navController.navigate("providers/${provider.id}/edit") },
                            onDelete = { vm.delete(provider.id) },
                            onToggle = { vm.toggleAvailability(provider.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderCard(
    provider: com.corestack.khidmatai.core.domain.model.AdminProvider,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    AdminCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(provider.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(provider.serviceType.replace("_", " "), color = AccentGreen, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("⭐ ${provider.rating}", color = TextSecondary, fontSize = 12.sp)
                    Text("${provider.experienceYears} yrs exp", color = TextSecondary, fontSize = 12.sp)
                    Text("PKR ${provider.pricePerHour.toLong()}/hr", color = TextSecondary, fontSize = 12.sp)
                }
                Text(provider.locationAddress, color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (provider.availability) "Available" else "Unavailable",
                        color = if (provider.availability) AccentGreen else TextSecondary,
                        fontSize = 11.sp
                    )
                    Switch(
                        checked = provider.availability,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = AccentGreen,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = Color(0xFF3A3A3A)
                        )
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton("Edit", onClick = onEdit)
                    DangerButton("Delete", onClick = onDelete)
                }
            }
        }
    }
}
