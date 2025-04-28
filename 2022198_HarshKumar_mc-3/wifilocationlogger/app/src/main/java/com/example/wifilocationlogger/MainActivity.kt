package com.example.wifilocationlogger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wifilocationlogger.ui.theme.WifilocationloggerTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : ComponentActivity() {
    private val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001
    
    private val viewModel: WiFiViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WiFiViewModel(WifiDataRepository(applicationContext)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        
        setContent {
            WifilocationloggerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WiFiLocationLoggerApp(viewModel)
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE
                ),
                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
            )
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission required for WiFi scanning", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun WiFiLocationLoggerApp(viewModel: WiFiViewModel) {
    val locations by viewModel.locations.collectAsState()
    var selectedLocation by remember { mutableStateOf(viewModel.repository.locationNames.firstOrNull() ?: "") }
    val context = LocalContext.current
    var scanStatus by remember { mutableStateOf("Ready to scan") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "WiFi Location Logger",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LocationSelector(
            locations = viewModel.repository.locationNames,
            selectedLocation = selectedLocation,
            onLocationSelected = { selectedLocation = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scanStatus = "Scanning..."
                    val success = viewModel.scanWifiNetworks(selectedLocation)
                    scanStatus = if (success) "Scan completed for $selectedLocation" else "Scan failed. Enable WiFi."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Scan WiFi")
            }
            
            Button(
                onClick = {
                    viewModel.fillRandomData(selectedLocation)
                    scanStatus = "Filled missing data with random values for $selectedLocation"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Fill Random")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = scanStatus,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab layout for different views
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("Matrix View", "Top APs", "Statistics", "Comparison")
        
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on selected tab
        when (selectedTabIndex) {
            0 -> MatrixView(locations, selectedLocation)
            1 -> TopAccessPointsView(viewModel, selectedLocation)
            2 -> StatisticsView(viewModel, selectedLocation)
            3 -> ComparisonView(viewModel)
        }
    }
}

@Composable
fun LocationSelector(
    locations: List<String>,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Select Location:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            locations.forEach { location ->
                val isSelected = location == selectedLocation
                
                OutlinedButton(
                    onClick = { onLocationSelected(location) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(location)
                }
            }
        }
    }
}

@Composable
fun MatrixView(locations: List<LocationData>, selectedLocation: String) {
    val locationData = locations.find { it.name == selectedLocation }
    
    if (locationData == null) {
        Text("No data available for $selectedLocation")
        return
    }
    
    Column {
        Text(
            text = "RSSI Matrix for ${locationData.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn {
            items(10) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until 10) {
                        val index = row * 10 + col
                        val rssi = locationData.rssiMatrix.getOrNull(index) ?: -100
                        
                        RssiCell(rssi)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun RssiCell(rssi: Int) {
    val color = when {
        rssi > -65 -> Color.Green
        rssi > -75 -> Color(0xFF8BC34A)
        rssi > -85 -> Color.Yellow
        rssi > -95 -> Color.Red
        else -> Color.Gray
    }
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (rssi > -100) "$rssi" else "",
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TopAccessPointsView(viewModel: WiFiViewModel, selectedLocation: String) {
    val topAPs = viewModel.getTopAccessPoints(selectedLocation)
    
    Column {
        Text(
            text = "Top Access Points at $selectedLocation",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (topAPs.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(topAPs) { ap ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = ap.ssid,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row {
                                Column(modifier = Modifier.weight(1f)) {
                                    APInfoItem("BSSID", ap.bssid)
                                    APInfoItem("RSSI", "${ap.rssi} dBm")
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    APInfoItem("Frequency", "${ap.frequency} MHz")
                                    APInfoItem("Security", parseSecurityType(ap.capabilities))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text("No access points scanned at $selectedLocation yet")
        }
    }
}

@Composable
fun APInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Medium
        )
        Text(text = value)
    }
}

fun parseSecurityType(capabilities: String): String {
    return when {
        capabilities.contains("WPA3") -> "WPA3"
        capabilities.contains("WPA2") -> "WPA2"
        capabilities.contains("WPA") -> "WPA"
        capabilities.contains("WEP") -> "WEP"
        else -> "Open"
    }
}

@Composable
fun StatisticsView(viewModel: WiFiViewModel, selectedLocation: String) {
    val stats = viewModel.getLocationStats(selectedLocation)
    
    Column {
        Text(
            text = "Statistics for $selectedLocation",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (stats != null) {
            StatItem("Networks Found", "${stats.count}")
            StatItem("Minimum Signal", "${stats.minSignal} dBm")
            StatItem("Maximum Signal", "${stats.maxSignal} dBm")
            StatItem("Average Signal", "${stats.avgSignal} dBm")
        } else {
            Text("No data available for $selectedLocation")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Divider()
}

@Composable
fun ComparisonView(viewModel: WiFiViewModel) {
    val stats = viewModel.getAllLocationStats()
    
    Column {
        Text(
            text = "Location Comparison",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (stats.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Location",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Networks",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Avg RSSI",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Range",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Location rows
            stats.forEach { (location, stat) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = location,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${stat.count}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${stat.avgSignal} dBm",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${stat.minSignal} to ${stat.maxSignal}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            Text("No data available for comparison")
        }
    }
}
