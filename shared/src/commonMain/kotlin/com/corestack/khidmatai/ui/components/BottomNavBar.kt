package com.corestack.khidmatai.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.corestack.khidmatai.ui.theme.*

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Surface,
        contentColor = TextSecondary,
        tonalElevation = MaterialTheme.spacing.small
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Text("🏠") },
            label = { Text("Home", style = AppTypography.bodySmall) },
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
            label = { Text("Bookings", style = AppTypography.bodySmall) },
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
            label = { Text("Profile", style = AppTypography.bodySmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
