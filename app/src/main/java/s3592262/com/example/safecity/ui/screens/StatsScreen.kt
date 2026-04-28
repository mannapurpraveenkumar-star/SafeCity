package s3592262.com.example.safecity.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FirebaseReport(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val audioUrl: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController) {

    val context = LocalContext.current

    var reports by remember { mutableStateOf<List<FirebaseReport>>(emptyList()) }
    var selectedReport by remember { mutableStateOf<FirebaseReport?>(null) }
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
                            audioUrl = doc.getString("audioUrl") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
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
                    IconButton(onClick = { navController.popBackStack() }) {
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

                    val formattedDate = if (report.timestamp != 0L) {
                        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                            .format(Date(report.timestamp))
                    } else {
                        "Unknown"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedReport = report
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Issue: ${report.category}")
                            Text("Description: ${report.description}")
                            Text("Reported on: $formattedDate")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Tap to view full report")
                        }
                    }
                }
            }
        }
    }

    selectedReport?.let { report ->

        val formattedDate = if (report.timestamp != 0L) {
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                .format(Date(report.timestamp))
        } else {
            "Unknown"
        }

        AlertDialog(
            onDismissRequest = {
                selectedReport = null
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedReport = null
                }) {
                    Text("Close")
                }
            },
            title = {
                Text("Report Details")
            },
            text = {
                Column {
                    Text("Category: ${report.category}")
                    Text("Description: ${report.description}")
                    Text("Name: ${report.name.ifBlank { "Anonymous" }}")
                    Text("Location: ${report.latitude}, ${report.longitude}")
                    Text("Reported on: $formattedDate")

                    Spacer(modifier = Modifier.height(12.dp))

                    if (report.imageUrl.isNotBlank()) {
                        Text("Image:")
                        AsyncImage(
                            model = report.imageUrl,
                            contentDescription = "Report Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    } else {
                        Text("Image: Not available")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (report.audioUrl.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(report.audioUrl)
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play Audio")
                        }
                    } else {
                        Text("Audio: Not available")
                    }
                }
            }
        )
    }
}