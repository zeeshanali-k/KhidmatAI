package com.corestack.khidmatai.ui.location

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corestack.khidmatai.core.domain.model.LocationPermissionStatus
import com.corestack.khidmatai.ui.theme.AppTypography
import com.corestack.khidmatai.ui.theme.Background
import com.corestack.khidmatai.ui.theme.Border
import com.corestack.khidmatai.ui.theme.Error
import com.corestack.khidmatai.ui.theme.LocalAppStrings
import com.corestack.khidmatai.ui.theme.Primary
import com.corestack.khidmatai.ui.theme.PrimaryLight
import com.corestack.khidmatai.ui.theme.Surface
import com.corestack.khidmatai.ui.theme.TextPrimary
import com.corestack.khidmatai.ui.theme.TextSecondary
import com.corestack.khidmatai.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LocationPickerScreen(
    viewModel: LocationPickerViewModel = koinViewModel(),
    currentLocation: String,
    onLocationSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionState = rememberLocationPermissionState { status ->
        if (status == LocationPermissionStatus.GRANTED) {
            viewModel.onAction(LocationPickerIntent.DetectLocation)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.detectedLocation.collect { location ->
            onLocationSelected(location)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Background,
        topBar = {
            LocationPickerTopBar(title = s.locationPickerTitle, onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall),
        ) {
            item {
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                LocationSearchBar(
                    query = state.searchQuery,
                    hint = s.locationPickerSearchHint,
                    onQueryChange = { viewModel.onAction(LocationPickerIntent.UpdateSearch(it)) },
                )
            }

            item {
                DetectLocationRow(
                    isDetecting = state.isDetecting,
                    permissionStatus = permissionState.status,
                    detectLabel = s.locationPickerDetect,
                    detectingLabel = s.locationPickerDetecting,
                    permissionRequired = s.locationPermissionRequired,
                    onClick = {
                        when (permissionState.status) {
                            LocationPermissionStatus.GRANTED -> viewModel.onAction(LocationPickerIntent.DetectLocation)
                            else -> permissionState.launchRequest()
                        }
                    }
                )
                if (state.detectionError != null) {
                    Text(
                        text = state.detectionError!!,
                        style = AppTypography.bodySmall,
                        color = Error,
                        modifier = Modifier.padding(
                            top = MaterialTheme.spacing.extraSmall,
                            start = MaterialTheme.spacing.small
                        )
                    )
                }
            }

            item { HorizontalDivider(color = Border) }

            item { SectionHeader(title = s.locationPickerPopularAreas) }

            if (state.filteredPopular.isEmpty()) {
                item { NoResultsText(text = s.locationPickerNoResults) }
            } else {
                items(state.filteredPopular) { location ->
                    LocationRow(
                        location = location,
                        isSelected = location == currentLocation,
                        onClick = { onLocationSelected(location) },
                    )
                }
            }

            item { HorizontalDivider(color = Border) }

            item { SectionHeader(title = s.locationPickerMajorCities) }

            if (state.filteredCities.isEmpty()) {
                item { NoResultsText(text = s.locationPickerNoResults) }
            } else {
                items(state.filteredCities) { location ->
                    LocationRow(
                        location = location,
                        isSelected = location == currentLocation,
                        onClick = { onLocationSelected(location) },
                    )
                }
            }

            item { Spacer(Modifier.height(MaterialTheme.spacing.large)) }
        }
    }
}

@Composable
private fun LocationPickerTopBar(title: String, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.mediumSmall,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "←",
            style = AppTypography.titleLarge,
            color = TextPrimary,
            modifier = Modifier
                .clickable(onClick = onNavigateBack)
                .padding(end = MaterialTheme.spacing.mediumSmall),
        )
        Text(text = title, style = AppTypography.titleLarge, color = TextPrimary)
    }
}

@Composable
private fun LocationSearchBar(query: String, hint: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.mediumSmall))
            .background(Surface)
            .border(
                MaterialTheme.spacing.extraSmall / 4,
                Border,
                RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "🔍",
            style = AppTypography.bodyLarge,
            modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
        )
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(hint, color = TextSecondary, style = AppTypography.bodyLarge) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetectLocationRow(
    isDetecting: Boolean,
    permissionStatus: LocationPermissionStatus,
    detectLabel: String,
    detectingLabel: String,
    permissionRequired: String,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "detect_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_alpha",
    )
    val isDenied = permissionStatus == LocationPermissionStatus.DENIED
    val label = when {
        isDetecting -> detectingLabel
        isDenied -> permissionRequired
        else -> detectLabel
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.mediumSmall))
            .background(if (isDenied) Color(0xFFFEF3F2) else PrimaryLight)
            .border(
                MaterialTheme.spacing.extraSmall / 4,
                if (isDenied) Error.copy(alpha = 0.3f) else Primary.copy(alpha = 0.3f),
                RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
            )
            .clickable(enabled = !isDetecting, onClick = onClick)
            .padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "📍",
                style = AppTypography.bodyLarge,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
            )
            Text(
                text = label,
                style = AppTypography.labelMedium,
                color = if (isDenied) Error
                    else if (isDetecting) Primary.copy(alpha = alpha)
                    else Primary,
            )
        }
        if (isDetecting) {
            CircularProgressIndicator(
                strokeWidth = MaterialTheme.spacing.extraSmall / 2,
                color = Primary,
                modifier = Modifier.size(28.dp),
            )
        } else if (!isDenied) {
            Text(text = "→", style = AppTypography.labelMedium, color = Primary)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = AppTypography.bodySmall,
        color = TextSecondary,
        modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall),
    )
}

@Composable
private fun LocationRow(location: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.small))
            .background(if (isSelected) PrimaryLight else Surface)
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.mediumSmall,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "•",
                style = AppTypography.bodyLarge,
                color = if (isSelected) Primary else TextSecondary,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
            )
            Text(
                text = location,
                style = AppTypography.bodyLarge,
                color = if (isSelected) Primary else TextPrimary,
            )
        }
        if (isSelected) {
            Text(text = "✓", style = AppTypography.labelMedium, color = Primary)
        }
    }
}

@Composable
private fun NoResultsText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AppTypography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
    }
}
