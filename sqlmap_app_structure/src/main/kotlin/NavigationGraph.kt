package com.sqlmap.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sqlmap.app.ui.screens.DashboardScreen
import com.sqlmap.app.ui.screens.DisclaimerScreen
import com.sqlmap.app.ui.screens.DumpProgressScreen
import com.sqlmap.app.ui.screens.DumpResultScreen
import com.sqlmap.app.ui.screens.InjectionTestScreen
import com.sqlmap.app.ui.screens.LanguageSelectionScreen
import com.sqlmap.app.ui.screens.SplashScreen
import com.sqlmap.app.ui.screens.WAFDetectionScreen
import com.sqlmap.app.ui.screens.WelcomeScreen

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        
        // Welcome Screen
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        
        // Disclaimer Screen
        composable(Screen.Disclaimer.route) {
            DisclaimerScreen(navController)
        }
        
        // Language Selection Screen
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(navController)
        }
        
        // Dashboard Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        
        // WAF Detection Screen
        composable(
            route = "waf_detection?url={url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStack ->
            WAFDetectionScreen(
                navController = navController,
                targetUrl = backStack.arguments?.getString("url").orEmpty()
            )
        }
        
        // Injection Test Screen
        composable(
            route = "injection_test?url={url}&param={param}",
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("param") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStack ->
            InjectionTestScreen(
                navController = navController,
                targetUrl = backStack.arguments?.getString("url").orEmpty(),
                parameter = backStack.arguments?.getString("param").orEmpty()
            )
        }
        
        // Dump Progress Screen
        composable(Screen.DumpProgress.route) {
            DumpProgressScreen(navController)
        }

        // Dump Result Screen — ZIP kartı + SAF kopyalama
        composable(
            route = "dump_result?db={db}",
            arguments = listOf(navArgument("db") { defaultValue = "database" })
        ) { backStack ->
            DumpResultScreen(
                navController = navController,
                databaseName = backStack.arguments?.getString("db") ?: "database"
            )
        }

        // Settings Screen (placeholder)
        composable(Screen.Settings.route) {
            // SettingsScreen(navController)
        }
        
        // Help Menu Screen (placeholder)
        composable(Screen.HelpMenu.route) {
            // HelpMenuScreen(navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Disclaimer : Screen("disclaimer")
    object LanguageSelection : Screen("language_selection")
    object Dashboard : Screen("dashboard")
    object TargetSetup : Screen("target_setup")
    object WAFDetection : Screen("waf_detection")
    object InjectionTest : Screen("injection_test")
    object DatabaseEnum : Screen("database_enum")
    object DumpProgress : Screen("dump_progress")
    object ResultsViewer : Screen("results_viewer")
    object Settings : Screen("settings")
    object HelpMenu : Screen("help_menu")
}
