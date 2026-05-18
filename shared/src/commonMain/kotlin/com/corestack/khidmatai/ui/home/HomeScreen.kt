package com.corestack.khidmatai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigateToProcessing: () -> Unit,
    onNavigateToBookings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.requestState) {
        if (state.requestState is RequestState.Processing) {
            onNavigateToProcessing()
        }
    }

    val isEmergency = state.urgency == "emergency"
    val backgroundColor = if (isEmergency) EmergencyBg else Background

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        containerColor = backgroundColor,
        topBar = {
            HomeAppBar(selectedLanguage = state.selectedLanguage) { newLang ->
                viewModel.onAction(ServiceRequestIntent.UpdateLanguage(newLang))
            }
        },
        bottomBar = {
            com.corestack.khidmatai.ui.components.BottomNavBar(
                currentRoute = "home",
                onNavigate = { route ->
                    if (route == "bookings") {
                        onNavigateToBookings()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MaterialTheme.spacing.medium)
        ) {
            Text("Assalam o Alaikum!", style = AppTypography.titleLarge, color = TextPrimary)
            Text("Aaj kya chahiye aapko?", style = AppTypography.bodyLarge, color = TextSecondary)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            if (isEmergency) {
                EmergencyBanner()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }

            // Main Input Card
            Card(
                shape = RoundedCornerShape(MaterialTheme.spacing.medium),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.spacing.extraSmall / 2),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        Text(
                            "🎤",
                            modifier = Modifier.size(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.small).clickable { /* TODO: Voice Input */ })
                    }
                    TextField(
                        value = state.query,
                        onValueChange = { viewModel.onAction(ServiceRequestIntent.UpdateQuery(it)) },
                        placeholder = { Text("Apni zaroorat likhen...\nUrdu, Roman Urdu ya English") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${state.query.length} / 300",
                            style = AppTypography.bodySmall,
                            color = TextSecondary
                        )
                        if (state.query.isNotEmpty()) {
                            Text(
                                "Clear ✕",
                                modifier = Modifier.clickable {
                                    viewModel.onAction(ServiceRequestIntent.UpdateQuery(""))
                                },
                                style = AppTypography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumSmall))

            // Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                    .background(Surface)
                    .border(MaterialTheme.spacing.extraSmall / 4, Border, RoundedCornerShape(MaterialTheme.spacing.medium))
                    .padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📍 ${state.location}", style = AppTypography.bodyLarge)
                Text("Change", color = Primary, style = AppTypography.labelMedium)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Urgency
            Text("Kitni zaroorat hai?", style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            val urgencies = listOf(
                "low" to "🟢 Low",
                "medium" to "🟡 Medium",
                "high" to "🔴 High",
                "emergency" to "🚨 Emergency"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                items(urgencies) { (key, label) ->
                    val isSelected = state.urgency == key
                    val chipBgColor = if (isSelected) {
                        if (key == "emergency") Error else Primary
                    } else Surface
                    val textColor = if (isSelected) Surface else TextPrimary
                    val border =
                        if (!isSelected) Border else if (key == "emergency") Error else Primary

                    Box(
                        modifier = Modifier
                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.extraSmall)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .background(chipBgColor)
                            .border(MaterialTheme.spacing.extraSmall / 4, border, RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .clickable {
                                viewModel.onAction(
                                    ServiceRequestIntent.UpdateUrgency(
                                        key
                                    )
                                )
                            }
                            .padding(horizontal = MaterialTheme.spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = textColor, style = AppTypography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Quick Chips
            Text("Kya chahiye?", style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            val quickChips = listOf(
                "❄️ AC Tech" to "Mujhe AC technician chahiye",
                "🔧 Plumber" to "Mujhe plumber chahiye, pipe leak hai",
                "⚡ Electrician" to "Bijli ki problem hai, electrician chahiye",
                "📚 Tutor" to "Bacche ko tutor chahiye math ke liye"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                items(quickChips) { (label, query) ->
                    Box(
                        modifier = Modifier
                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.small)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .border(MaterialTheme.spacing.extraSmall / 4, Border, RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .clickable {
                                viewModel.onAction(
                                    ServiceRequestIntent.UpdateQuery(
                                        query
                                    )
                                )
                            }
                            .padding(horizontal = MaterialTheme.spacing.mediumSmall),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, style = AppTypography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            val isSubmitting = state.requestState is RequestState.Processing
            Button(
                onClick = { viewModel.onAction(ServiceRequestIntent.SubmitRequest) },
                enabled = state.query.isNotBlank() && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + MaterialTheme.spacing.extraSmall),
                shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmergency) Error else Primary
                )
            ) {
                if (isSubmitting) {
                    AiOrbView(AiOrbState.THINKING, MaterialTheme.spacing.large)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                    Text("Processing...", style = AppTypography.labelMedium)
                } else {
                    Text(
                        if (isEmergency) "Find Emergency Service 🚨" else "Find Service →",
                        style = AppTypography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun HomeAppBar(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("KaamKaro", style = AppTypography.titleLarge, color = Primary)

        // Language Toggle
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                .background(Surface)
                .border(MaterialTheme.spacing.extraSmall / 4, Border, RoundedCornerShape(MaterialTheme.spacing.xxl))
        ) {
            listOf("EN", "RU", "اردو").forEach { lang ->
                val isSelected = selectedLanguage == lang
                Box(
                    modifier = Modifier
                        .height(MaterialTheme.spacing.extraLarge)
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { onLanguageSelected(lang) }
                        .padding(horizontal = MaterialTheme.spacing.mediumSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        lang,
                        color = if (isSelected) Surface else TextSecondary,
                        style = AppTypography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.small))
            .background(ErrorLight)
            .border(MaterialTheme.spacing.extraSmall / 4, Border, RoundedCornerShape(MaterialTheme.spacing.small))
            .padding(MaterialTheme.spacing.mediumSmall)
    ) {
        Text("⚠️", modifier = Modifier.padding(end = MaterialTheme.spacing.small))
        Column {
            Text("Emergency Mode Active", style = AppTypography.labelMedium, color = Error)
            Text(
                "Sirf genuine emergencies ke liye use karein.",
                style = AppTypography.bodySmall,
                color = Error
            )
        }
    }
}
