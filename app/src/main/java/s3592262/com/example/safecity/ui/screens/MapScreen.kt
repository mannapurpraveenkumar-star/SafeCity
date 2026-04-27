package s3592262.com.example.safecity.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {

    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },

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
            factory = { ctx ->
                MapView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(-37.8136, 144.9631))

                    // Report marker 1
                    val report1 = Marker(this)
                    report1.position = GeoPoint(-37.8136, 144.9631)
                    report1.title = "Pothole"
                    report1.snippet = "Large pothole reported near city centre"
                    report1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(report1)

                    // Report marker 2
                    val report2 = Marker(this)
                    report2.position = GeoPoint(-37.8180, 144.9670)
                    report2.title = "Litter"
                    report2.snippet = "Overflowing bin reported nearby"
                    report2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(report2)

                    // Report marker 3
                    val report3 = Marker(this)
                    report3.position = GeoPoint(-37.8100, 144.9600)
                    report3.title = "Noise"
                    report3.snippet = "Noise complaint reported"
                    report3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(report3)

                    if (hasLocationPermission) {
                        val locationOverlay = MyLocationNewOverlay(
                            GpsMyLocationProvider(ctx),
                            this
                        )

                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()

                        overlays.add(locationOverlay)
                    }
                }
            },
            update = { mapView ->
                if (hasLocationPermission) {
                    val alreadyAdded = mapView.overlays.any {
                        it is MyLocationNewOverlay
                    }

                    if (!alreadyAdded) {
                        val locationOverlay = MyLocationNewOverlay(
                            GpsMyLocationProvider(context),
                            mapView
                        )

                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()

                        mapView.overlays.add(locationOverlay)
                        mapView.invalidate()
                    }
                }
            }
        )
    }
}