package s3592262.com.example.safecity.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "SafeCity",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Report Button
        Button(
            onClick = { navController.navigate("report") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Report Issue")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map Button
        Button(
            onClick = { navController.navigate("map") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Map")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Button
        Button(
            onClick = { navController.navigate("stats") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Reports")
        }
    }
}