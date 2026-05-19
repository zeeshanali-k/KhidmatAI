package com.corestack.khidmatai.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.corestack.khidmatai.ui.components.BottomNavBar
import com.corestack.khidmatai.ui.theme.*

@Composable
fun ProfileScreen(
    selectedLanguage: String = "EN",
    onLanguageChange: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val s = LocalAppStrings.current

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .border(MaterialTheme.spacing.extraSmall / 4, Border, RoundedCornerShape(0))
                    .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.mediumSmall)
            ) {
                Text(s.profileTitle, style = AppTypography.titleLarge, color = TextPrimary)
            }
        },
        bottomBar = {
            BottomNavBar(currentRoute = "profile", onNavigate = onNavigate)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // User Card
            item {
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.large),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.spacing.extraSmall / 2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.large).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(MaterialTheme.spacing.xxl + MaterialTheme.spacing.large)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Primary, PrimaryDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", style = AppTypography.displayLarge, color = Surface)
                        }

                        // Name & phone
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ali Raza", style = AppTypography.titleLarge, color = TextPrimary)
                            Text("+92 300 1234567", style = AppTypography.bodySmall, color = TextSecondary)
                        }

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.extraSmall),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(
                                "3" to s.profileBookings,
                                "4.8" to s.profileRating,
                                "100%" to s.profileVerified
                            ).forEach { (value, label) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(value, style = AppTypography.titleLarge, color = Primary)
                                    Text(label, style = AppTypography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }

            // Menu
            item {
                Card(
                    shape = RoundedCornerShape(MaterialTheme.spacing.medium),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.spacing.extraSmall / 4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val menuItems = listOf(
                            Triple("🔔", s.profileNotifications, "Reminders on"),
                            Triple("🌐", s.profileLanguage, selectedLanguage),
                            Triple("📍", s.profileSavedLocations, "2 saved"),
                            Triple("🔐", s.profilePrivacy, ""),
                            Triple("ℹ️", s.profileAbout, "v1.0"),
                        )
                        menuItems.forEachIndexed { index, (icon, label, sub) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (label == s.profileLanguage) {
                                            val next = when (selectedLanguage) {
                                                "EN" -> "اردو"
                                                "اردو" -> "EN"
                                                else -> "EN"
                                            }
                                            onLanguageChange(next)
                                        }
                                    }
                                    .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.mediumSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(icon, style = AppTypography.titleLarge, modifier = Modifier.padding(end = MaterialTheme.spacing.mediumSmall))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(label, style = AppTypography.labelMedium, color = TextPrimary)
                                    if (sub.isNotEmpty()) {
                                        Text(sub, style = AppTypography.bodySmall, color = TextSecondary)
                                    }
                                }
                                Text("›", style = AppTypography.titleLarge, color = Border)
                            }
                            if (index < menuItems.size - 1) {
                                HorizontalDivider(
                                    color = Border,
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
                                )
                            }
                        }
                    }
                }
            }

            // Sign Out
            item {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    border = androidx.compose.foundation.BorderStroke(
                        MaterialTheme.spacing.extraSmall / 4,
                        Error
                    )
                ) {
                    Text(s.profileSignOut, color = Error, style = AppTypography.labelMedium)
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            }
        }
    }
}
