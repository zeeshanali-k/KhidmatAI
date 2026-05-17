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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val state by viewModel.uiState.collectAsState()

    // Trigger navigation when state changes to Processing
    if (state.requestState is RequestState.Processing) {
        onNavigateToProcessing()
    }

    val isEmergency = state.urgency == "emergency"
    val backgroundColor = if (isEmergency) EmergencyBg else Background

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            HomeAppBar(selectedLanguage = state.selectedLanguage) { newLang ->
                viewModel.handleIntent(ServiceRequestIntent.UpdateLanguage(newLang))
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
                .padding(16.dp)
        ) {
            Text("Assalam o Alaikum!", style = AppTypography.titleLarge, color = TextPrimary)
            Text("Aaj kya chahiye aapko?", style = AppTypography.bodyLarge, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isEmergency) {
                EmergencyBanner()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Main Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        Text("🎤", modifier = Modifier.size(40.dp).clickable { /* TODO: Voice Input */ })
                    }
                    TextField(
                        value = state.query,
                        onValueChange = { viewModel.handleIntent(ServiceRequestIntent.UpdateQuery(it)) },
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
                        Text("${state.query.length} / 300", style = AppTypography.bodySmall, color = TextSecondary)
                        if (state.query.isNotEmpty()) {
                            Text("Clear ✕", modifier = Modifier.clickable { viewModel.handleIntent(ServiceRequestIntent.UpdateQuery("")) }, style = AppTypography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📍 ${state.location}", style = AppTypography.bodyLarge)
                Text("Change", color = Primary, style = AppTypography.labelMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Urgency
            Text("Kitni zaroorat hai?", style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            val urgencies = listOf("low" to "🟢 Low", "medium" to "🟡 Medium", "high" to "🔴 High", "emergency" to "🚨 Emergency")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(urgencies) { (key, label) ->
                    val isSelected = state.urgency == key
                    val chipBgColor = if (isSelected) {
                        if (key == "emergency") Error else Primary
                    } else Surface
                    val textColor = if (isSelected) Surface else TextPrimary
                    val border = if (!isSelected) Border else if (key == "emergency") Error else Primary
                    
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(chipBgColor)
                            .border(1.dp, border, RoundedCornerShape(999.dp))
                            .clickable { viewModel.handleIntent(ServiceRequestIntent.UpdateUrgency(key)) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = textColor, style = AppTypography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick Chips
            Text("Kya chahiye?", style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            val quickChips = listOf(
                "❄️ AC Tech" to "Mujhe AC technician chahiye",
                "🔧 Plumber" to "Mujhe plumber chahiye, pipe leak hai",
                "⚡ Electrician" to "Bijli ki problem hai, electrician chahiye",
                "📚 Tutor" to "Bacche ko tutor chahiye math ke liye"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickChips) { (label, query) ->
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .border(1.dp, Border, RoundedCornerShape(999.dp))
                            .clickable { viewModel.handleIntent(ServiceRequestIntent.UpdateQuery(query)) }
                            .padding(horizontal = 12.dp),
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
                onClick = { viewModel.handleIntent(ServiceRequestIntent.SubmitRequest) },
                enabled = state.query.isNotBlank() && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmergency) Error else Primary
                )
            ) {
                if (isSubmitting) {
                    AiOrbView(AiOrbState.THINKING, 24.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...", style = AppTypography.labelMedium)
                } else {
                    Text(if (isEmergency) "Find Emergency Service 🚨" else "Find Service →", style = AppTypography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun HomeAppBar(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("KaamKaro", style = AppTypography.titleLarge, color = Primary)
        
        // Language Toggle
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(999.dp))
        ) {
            listOf("EN", "RU", "اردو").forEach { lang ->
                val isSelected = selectedLanguage == lang
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { onLanguageSelected(lang) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(lang, color = if (isSelected) Surface else TextSecondary, style = AppTypography.labelMedium)
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
            .clip(RoundedCornerShape(8.dp))
            .background(ErrorLight)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text("⚠️", modifier = Modifier.padding(end = 8.dp))
        Column {
            Text("Emergency Mode Active", style = AppTypography.labelMedium, color = Error)
            Text("Sirf genuine emergencies ke liye use karein.", style = AppTypography.bodySmall, color = Error)
        }
    }
}
