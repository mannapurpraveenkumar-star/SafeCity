package s3592262.com.example.safecity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import s3592262.com.example.safecity.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"   // ✅ Home screen first
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("splash") {
            SplashScreen(navController)
        }

        composable("report") {
            ReportScreen(navController)
        }

        composable("map") {
            MapScreen(navController)
        }

        composable("stats") {
            StatsScreen(navController)
        }
    }
}