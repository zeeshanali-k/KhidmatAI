package com.corestack.khidmatai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.corestack.khidmatai.core.domain.model.AiOrbState
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.components.BottomNavBar
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.Res
import khidmatai.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigateToProcessing: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToVoice: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLocationPicker: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {}
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.requestState) {
        if (state.requestState is RequestState.Processing) {
            onNavigateToProcessing()
        }
    }

    val isEmergency = state.urgency == "emergency"
    val backgroundColor = if (isEmergency) EmergencyBg else Background

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = backgroundColor,
        topBar = {
            HomeAppBar(
                selectedLanguage = state.selectedLanguage
            ) { newLang ->
                viewModel.onAction(ServiceRequestIntent.UpdateLanguage(newLang))
                onLanguageChange(newLang)
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "bookings" -> onNavigateToBookings()
                        "profile" -> onNavigateToProfile()
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
            Text(s.homeGreeting, style = AppTypography.titleLarge, color = TextPrimary)
            Text(s.homePrompt, style = AppTypography.bodyLarge, color = TextSecondary)

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

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        TextField(
                            value = state.query,
                            maxLines = 4,
                            onValueChange = { viewModel.onAction(ServiceRequestIntent.UpdateQuery(it)) },
                            placeholder = { Text(s.homeSearchHint) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                        Text(
                            "🎤",
                            modifier = Modifier
                                .size(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.small)
                                .clip(CircleShape)
                                .clickable { onNavigateToVoice() }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val counterColor = when {
                            state.query.length >= 300 -> Error
                            state.query.length >= 250 -> Warning
                            else -> TextSecondary
                        }
                        Text(
                            "${state.query.length} / 300",
                            style = AppTypography.bodySmall,
                            color = counterColor
                        )
                        if (state.query.isNotEmpty()) {
                            Text(
                                s.homeClear,
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
                    .border(
                        MaterialTheme.spacing.extraSmall / 4,
                        Border,
                        RoundedCornerShape(MaterialTheme.spacing.medium)
                    )
                    .padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📍 ${state.location}", style = AppTypography.bodyLarge)
                Text(
                    s.homeChangeLocation,
                    color = Primary,
                    style = AppTypography.labelMedium,
                    modifier = Modifier.clickable { onNavigateToLocationPicker() }
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Urgency
            Text(s.homeUrgencyTitle, style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            val urgencies = remember {
                listOf(
                    "low" to s.urgencyLow,
                    "medium" to s.urgencyMedium,
                    "high" to s.urgencyHigh,
                    "emergency" to s.urgencyEmergency
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                items(urgencies) { (key, label) ->
                    val isSelected = state.urgency == key
                    val chipBgColor = if (isSelected) {
                        if (key == "emergency") Error else Primary
                    } else Surface
                    val textColor = if (isSelected) Surface else TextPrimary
                    val chipBorder =
                        if (!isSelected) Border else if (key == "emergency") Error else Primary

                    Box(
                        modifier = Modifier
                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.extraSmall)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .background(chipBgColor)
                            .border(
                                MaterialTheme.spacing.extraSmall / 4,
                                chipBorder,
                                RoundedCornerShape(MaterialTheme.spacing.xxl)
                            )
                            .clickable { viewModel.onAction(ServiceRequestIntent.UpdateUrgency(key)) }
                            .padding(horizontal = MaterialTheme.spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = textColor, style = AppTypography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Quick Chips
            Text(s.homeQuickChipsTitle, style = AppTypography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            val quickChips = remember {
                listOf(
                    s.quickAc to s.quickAcQuery,
                    s.quickPlumber to s.quickPlumberQuery,
                    s.quickElectrician to s.quickElectricianQuery,
                    s.quickTutor to s.quickTutorQuery,
                    s.quickBeautician to s.quickBeauticianQuery,
                    s.quickCarpenter to s.quickCarpenterQuery
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                items(quickChips) { (label, query) ->
                    Box(
                        modifier = Modifier
                            .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.small)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                            .border(
                                MaterialTheme.spacing.extraSmall / 4,
                                Border,
                                RoundedCornerShape(MaterialTheme.spacing.xxl)
                            )
                            .clickable { viewModel.onAction(ServiceRequestIntent.UpdateQuery(query)) }
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
                    Text(s.homeProcessing, style = AppTypography.labelMedium)
                } else {
                    Text(
                        if (isEmergency) s.homeBtnFindEmergency else s.homeBtnFindService,
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
        Text(stringResource(Res.string.app_name), style = AppTypography.titleLarge, color = Primary)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                .background(Surface)
                .border(
                    MaterialTheme.spacing.extraSmall / 4,
                    Border,
                    RoundedCornerShape(MaterialTheme.spacing.xxl)
                )
        ) {
            remember { listOf("EN", "اردو") }.forEach { lang ->
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
    val s = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.small))
            .background(ErrorLight)
            .drawBehind {
                drawRect(
                    color = Error,
                    topLeft = Offset.Zero,
                    size = Size(4.dp.toPx(), size.height)
                )
            }
            .padding(
                start = MaterialTheme.spacing.medium,
                top = MaterialTheme.spacing.mediumSmall,
                end = MaterialTheme.spacing.mediumSmall,
                bottom = MaterialTheme.spacing.mediumSmall
            )
    ) {
        Text("⚠️", modifier = Modifier.padding(end = MaterialTheme.spacing.small))
        Column {
            Text(s.emergencyModeTitle, style = AppTypography.labelMedium, color = Error)
            Text(s.emergencyModeDesc, style = AppTypography.bodySmall, color = Error)
        }
    }
}
