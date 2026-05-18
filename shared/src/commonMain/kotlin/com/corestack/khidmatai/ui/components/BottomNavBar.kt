package com.corestack.khidmatai.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.corestack.khidmatai.ui.theme.*

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val s = LocalAppStrings.current
    NavigationBar(
        containerColor = Surface,
        contentColor = TextSecondary,
        tonalElevation = MaterialTheme.spacing.small
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Text("🏠") },
            label = { Text(s.navHome, style = AppTypography.bodySmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "bookings",
            onClick = { onNavigate("bookings") },
            icon = { Text("📋") },
            label = { Text(s.navBookings, style = AppTypography.bodySmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Text("👤") },
            label = { Text(s.navProfile, style = AppTypography.bodySmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
