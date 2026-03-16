package s3592262.com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ReportScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Report Issue Screen")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("map") }) {
                Text("Go to Map")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.navigate("stats") }) {
                Text("Go to Stats")
            }
        }
    }
}