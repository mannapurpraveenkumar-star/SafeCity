package s3592262.com.example.safecity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import s3592262.com.example.safecity.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController = navController) }
        composable("report") { ReportScreen(navController = navController) }
        composable("map") { MapScreen(navController = navController) }
        composable("stats") { StatsScreen(navController = navController) }
    }
}