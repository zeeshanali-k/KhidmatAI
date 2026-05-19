package com.corestack.khidmatai.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.corestack.khidmatai.admin.ui.bookings.BookingDetailScreen
import com.corestack.khidmatai.admin.ui.bookings.BookingsScreen
import com.corestack.khidmatai.admin.ui.dashboard.DashboardScreen
import com.corestack.khidmatai.admin.ui.providers.ProviderFormScreen
import com.corestack.khidmatai.admin.ui.providers.ProvidersScreen
import com.corestack.khidmatai.admin.ui.requests.RequestDetailScreen
import com.corestack.khidmatai.admin.ui.requests.RequestsScreen

import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination

private val DarkBackground = Color(0xFF0F0F0F)
private val SidebarBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFF00E676)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFF888888)
private val Selected = Color(0xFF2A2A2A)

sealed interface AdminRoute {
    @Serializable data object Dashboard : AdminRoute
    @Serializable data object Bookings : AdminRoute
    @Serializable data class BookingDetail(val bookingId: String) : AdminRoute
    @Serializable data object Providers : AdminRoute
    @Serializable data object ProviderCreate : AdminRoute
    @Serializable data class ProviderEdit(val providerId: String) : AdminRoute
    @Serializable data object Requests : AdminRoute
    @Serializable data class RequestDetail(val requestId: String) : AdminRoute
}

private data class NavItem(val route: Any, val label: String)

private val navItems = listOf(
    NavItem(AdminRoute.Dashboard, "Dashboard"),
    NavItem(AdminRoute.Bookings, "Bookings"),
    NavItem(AdminRoute.Providers, "Providers"),
    NavItem(AdminRoute.Requests, "Requests")
)

@Composable
fun AdminApp() {
    val navController = rememberNavController()
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = DarkBackground,
            surface = SidebarBackground,
            primary = AccentGreen,
            onPrimary = Color.Black,
            onBackground = TextPrimary,
            onSurface = TextPrimary
        )
    ) {
        Row(Modifier.fillMaxSize().background(DarkBackground)) {
            AdminSidebar(navController)
            Box(Modifier.weight(1f).fillMaxHeight()) {
                AdminNavHost(navController)
            }
        }
    }
}

@Composable
private fun AdminSidebar(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination

    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(SidebarBackground)
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "KhidmatAI",
            color = AccentGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp)
        )
        Text(
            text = "Admin Panel",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)
        )
        Divider(color = Color(0xFF2A2A2A))
        Spacer(Modifier.height(16.dp))

        navItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            SidebarItem(
                label = item.label,
                isSelected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun SidebarItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Selected else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isSelected) {
                Box(Modifier.width(3.dp).height(18.dp).background(AccentGreen))
            } else {
                Spacer(Modifier.width(3.dp))
            }
            Text(
                text = label,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AdminNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AdminRoute.Dashboard) {
        composable<AdminRoute.Dashboard> {
            DashboardScreen(navController)
        }
        composable<AdminRoute.Bookings> {
            BookingsScreen(navController)
        }
        composable<AdminRoute.BookingDetail> { backStack ->
            val route = backStack.toRoute<AdminRoute.BookingDetail>()
            BookingDetailScreen(bookingId = route.bookingId, navController = navController)
        }
        composable<AdminRoute.Providers> {
            ProvidersScreen(navController)
        }
        composable<AdminRoute.ProviderCreate> {
            ProviderFormScreen(providerId = null, navController = navController)
        }
        composable<AdminRoute.ProviderEdit> { backStack ->
            val route = backStack.toRoute<AdminRoute.ProviderEdit>()
            ProviderFormScreen(providerId = route.providerId, navController = navController)
        }
        composable<AdminRoute.Requests> {
            RequestsScreen(navController)
        }
        composable<AdminRoute.RequestDetail> { backStack ->
            val route = backStack.toRoute<AdminRoute.RequestDetail>()
            RequestDetailScreen(requestId = route.requestId, navController = navController)
        }
    }
}
