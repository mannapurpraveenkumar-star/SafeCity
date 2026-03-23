package s3592262.com.example.safecity.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SafeCity App",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    // Navigate automatically to ReportScreen after 1 seconds
    LaunchedEffect(true) {
        delay(1000)
        navController.navigate("report") {
            popUpTo("splash") { inclusive = true }
        }
    }
}
