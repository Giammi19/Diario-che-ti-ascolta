package com.ium.diario.navigate

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ium.diario.models.*
import com.ium.diario.state.*
import com.ium.diario.ui.screens.*
import com.ium.diario.ui.theme.*

// ============================================================
// MARK: — Route constants
// ============================================================

object Routes {
    const val SPLASH         = "splash"
    const val ONBOARDING     = "onboarding"
    const val HOME           = "home"
    const val SEARCH         = "search"
    const val EVENT_DETAIL   = "event_detail/{eventId}"
    const val BOOKING        = "booking/{eventId}"
    const val PAYMENT        = "payment/{eventId}"
    const val CONFIRM        = "confirm/{bookingId}"
    const val CALENDAR       = "calendar"
    const val PROFILE        = "profile"
    const val PROFILE_SAVED  = "profile_saved"
    const val NOTIFICHE      = "notifiche"

    // Builder helpers
    fun eventDetail(eventId: String) = "event_detail/$eventId"
    fun booking(eventId: String)     = "booking/$eventId"
    fun payment(eventId: String, seats: Int) = "payment/$eventId"
    fun confirm(bookingId: String)   = "confirm/$bookingId"
}

// ============================================================
// MARK: — AppNavigation
// ============================================================

@Composable
fun AppNavigation(
    appViewModel: AppViewModel,
    startRoute  : String = Routes.SPLASH,
) {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = startRoute,
    ) {
        // ── Splash & Onboarding ──────────────────────────────
        composable(Routes.SPLASH) {
            SplashView(navController, appViewModel)
        }
        composable(Routes.ONBOARDING) {
            OnboardingView(navController, appViewModel)
        }

        // ── Main Tab container ───────────────────────────────
        composable(Routes.HOME) {
            MainTabView(navController = navController, appViewModel = appViewModel)
        }

        // ── Event Detail ─────────────────────────────────────
        composable(
            route     = Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EventDetailView(eventId, navController, appViewModel)
        }

        // ── Booking ──────────────────────────────────────────
        composable(
            route     = Routes.BOOKING,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            BookingView(eventId, navController, appViewModel)
        }

        // ── Payment ──────────────────────────────────────────
        composable(
            route     = Routes.PAYMENT,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            PaymentView(eventId, navController, appViewModel)
        }

        // ── Confirm ──────────────────────────────────────────
        composable(
            route     = Routes.CONFIRM,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: return@composable
            ConfirmView(bookingId, navController, appViewModel)
        }

        // ── Profile Saved ────────────────────────────────────
        composable(Routes.PROFILE_SAVED) {
            ProfileSavedView(navController, appViewModel)
        }

        // ── Notifiche ────────────────────────────────────────
        composable(Routes.NOTIFICHE) {
            NotificheView(navController, appViewModel)
        }
    }
}

// ============================================================
// MARK: — MainTabView
// ============================================================

@Composable
fun MainTabView(
    navController: NavController,
    appViewModel : AppViewModel,
) {
    val activeTab by appViewModel.activeTab.collectAsStateWithLifecycle()
    val tabNavController = rememberNavController()

    // Sincronizza il NavHost interno con lo stato della tab attiva
    LaunchedEffect(activeTab) {
        if (tabNavController.currentDestination?.route != activeTab.route) {
            tabNavController.navigate(activeTab.route) {
                popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                activeTab = activeTab,
                onTabSelected = { tab ->
                    appViewModel.setActiveTab(tab)
                    tabNavController.navigate(tab.route) {
                        popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        },
        containerColor = AppColors.Background,
    ) { innerPadding ->

        NavHost(
            navController    = tabNavController,
            startDestination = AppTab.HOME.route,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { fadeIn(tween(200)) },
            exitTransition   = { fadeOut(tween(200)) },
        ) {
            composable(AppTab.HOME.route) {
                HomeView(navController as NavHostController, appViewModel)
            }
            composable(AppTab.SEARCH.route) {
                SearchView(navController as NavHostController, appViewModel)
            }
            composable(AppTab.CALENDAR.route) {
                CalendarView(navController as NavHostController, appViewModel)
            }
            composable(AppTab.PROFILE.route) {
                ProfileView(navController as NavHostController, appViewModel)
            }
        }
    }
}

// ============================================================
// MARK: — AppBottomBar
// ============================================================

@Composable
fun AppBottomBar(
    activeTab     : AppTab,
    onTabSelected : (AppTab) -> Unit,
) {
    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 0.dp,
    ) {
        AppTab.entries.forEach { tab ->
            val selected = tab == activeTab
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector         = tabIcon(tab, selected),
                        contentDescription  = tab.label,
                    )
                },
                label = {
                    Text(
                        text  = tab.label,
                        style = AppTypography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = AppColors.Primary,
                    indicatorColor      = AppColors.PrimaryMuted,
                    unselectedIconColor = AppColors.TextTertiary,
                ),
            )
        }
    }
}

private fun tabIcon(tab: AppTab, selected: Boolean): ImageVector = when (tab) {
    AppTab.HOME     -> if (selected) Icons.Filled.Home       else Icons.Outlined.Home
    AppTab.SEARCH   -> if (selected) Icons.Filled.Search     else Icons.Outlined.Search
    AppTab.CALENDAR -> if (selected) Icons.Filled.DateRange  else Icons.Outlined.DateRange
    AppTab.PROFILE  -> if (selected) Icons.Filled.Person     else Icons.Outlined.Person
}

@Composable
private fun PlaceholderScreen(
    title        : String,
    navController: NavController,
    nextRoute    : String? = null,
) {
    ScreenContainer {
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(AppSpacing.xxl),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
        ) {
            Text(text = title, style = AppTypography.displaySmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(AppSpacing.xl))
            if (nextRoute != null) {
                PrimaryButton(
                    text    = "Continua →",
                    onClick = { navController.navigate(nextRoute) },
                    fullWidth = false,
                )
            } else {
                SecondaryButton(
                    text    = "← Indietro",
                    onClick = { navController.popBackStack() },
                    fullWidth = false,
                )
            }
        }
    }
}
