package com.corestack.khidmatai.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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
            label = { Text(stringResource(Res.string.nav_home), style = AppTypography.bodySmall) },
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
            label = { Text(stringResource(Res.string.nav_bookings), style = AppTypography.bodySmall) },
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
            label = { Text(stringResource(Res.string.nav_profile), style = AppTypography.bodySmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
