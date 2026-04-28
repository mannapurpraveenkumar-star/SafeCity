package s3592262.com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

data class FirebaseReport(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val audioUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController) {

    var reports by remember { mutableStateOf<List<FirebaseReport>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val listener = FirebaseFirestore.getInstance()
            .collection("reports")
            .addSnapshotListener { result, error ->

                if (error != null) {
                    errorMessage = error.message ?: "Unknown Firebase error"
                    return@addSnapshotListener
                }

                if (result != null) {
                    reports = result.documents.map { doc ->
                        FirebaseReport(
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            audioUrl = doc.getString("audioUrl") ?: ""
                        )
                    }
                }
            }

        onDispose {
            listener.remove()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },

                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },

                actions = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                }
            )
        }
    ) { padding ->

        if (errorMessage.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Firebase error: $errorMessage")
            }
        } else if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No reports found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Category: ${report.category}")
                            Text("Description: ${report.description}")
                            Text("Name: ${report.name.ifBlank { "Anonymous" }}")
                            Text("Location: ${report.latitude}, ${report.longitude}")

                            if (report.imageUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Image uploaded: Yes")
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Image uploaded: No")
                            }

                            if (report.audioUrl.isNotBlank()) {
                                Text("Audio uploaded: Yes")
                            } else {
                                Text("Audio uploaded: No")
                            }
                        }
                    }
                }
            }
        }
    }
}