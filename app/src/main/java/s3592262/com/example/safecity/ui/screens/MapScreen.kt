package s3592262.com.example.safecity.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MapReport(
    val category: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {

    var reports by remember { mutableStateOf<List<MapReport>>(emptyList()) }

    DisposableEffect(Unit) {
        val listener = FirebaseFirestore.getInstance()
            .collection("reports")
            .addSnapshotListener { result, error ->
                if (error == null && result != null) {
                    reports = result.documents.mapNotNull { doc ->
                        val lat = doc.getDouble("latitude")
                        val lng = doc.getDouble("longitude")

                        if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                            MapReport(
                                category = doc.getString("category") ?: "Issue",
                                description = doc.getString("description") ?: "",
                                latitude = lat,
                                longitude = lng
                            )
                        } else {
                            null
                        }
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
                title = { Text("Nearby Issues") },

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

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            factory = { context ->
                MapView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    controller.setZoom(16.0)
                    controller.setCenter(GeoPoint(37.4219983, -122.084))
                }
            },

            update = { mapView ->

                mapView.overlays.clear()

                val validReports = reports.filter {
                    it.latitude != 0.0 && it.longitude != 0.0
                }

                validReports.forEachIndexed { index, report ->

                    val sameLocationCount = validReports.count {
                        it.latitude == report.latitude && it.longitude == report.longitude
                    }

                    val sameLocationIndex = validReports
                        .take(index + 1)
                        .count {
                            it.latitude == report.latitude && it.longitude == report.longitude
                        } - 1

                    val offset = if (sameLocationCount > 1) {
                        sameLocationIndex * 0.00015
                    } else {
                        0.0
                    }

                    val marker = Marker(mapView)

                    marker.position = GeoPoint(
                        report.latitude + offset,
                        report.longitude + offset
                    )

                    marker.title = "${index + 1}. ${report.category}"
                    marker.snippet = report.description.ifBlank {
                        "No description"
                    }

                    marker.subDescription =
                        "Location: ${report.latitude}, ${report.longitude}"

                    marker.setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )

                    mapView.overlays.add(marker)
                }

                if (validReports.size == 1) {
                    val report = validReports.first()
                    mapView.controller.setZoom(17.0)
                    mapView.controller.setCenter(
                        GeoPoint(report.latitude, report.longitude)
                    )
                }

                if (validReports.size > 1) {
                    val north = validReports.maxOf { it.latitude } + 0.001
                    val south = validReports.minOf { it.latitude } - 0.001
                    val east = validReports.maxOf { it.longitude } + 0.001
                    val west = validReports.minOf { it.longitude } - 0.001

                    val box = BoundingBox(north, east, south, west)

                    mapView.post {
                        mapView.zoomToBoundingBox(box, true, 120)
                    }
                }

                mapView.invalidate()
            }
        )
    }
}