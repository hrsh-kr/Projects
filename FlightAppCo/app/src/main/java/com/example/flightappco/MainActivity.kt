package com.example.flightappco

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.*

data class Stop(
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val visaRequired: Boolean,
    val visaDetails: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FlightPlannerApp()
            }
        }
    }
}

@Composable
fun FlightPlannerApp() {

    var allStops by remember { mutableStateOf(listOf<Stop>()) }
    var selectedStops by remember { mutableStateOf(listOf<Stop>()) }
    var currentStopIndex by remember { mutableIntStateOf(0) }
    var isMetric by remember { mutableStateOf(true) }
    var totalDistance by remember { mutableDoubleStateOf(0.0) }
    var coveredDistance by remember { mutableDoubleStateOf(0.0) }
    var isRouteCalculated by remember { mutableStateOf(false) }
    var lazyLoadedIndices by remember { mutableStateOf(setOf<Int>()) }


    // Load stops from raw directory
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.stops)
            val reader = BufferedReader(InputStreamReader(inputStream))
            allStops = reader.readLines().mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size == 5) {
                    Stop(
                        city = parts[0].trim(),
                        latitude = parts[1].toDouble(),
                        longitude = parts[2].toDouble(),
                        visaRequired = parts[3].toBoolean(),
                        visaDetails = parts[4].trim()
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e("LoadStops", "Error reading stops: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 46.dp)
    ) {
        CitySelector(
            allStops = allStops,
            onStopSelected = { stop ->
                if (!selectedStops.contains(stop)) {
                    selectedStops = selectedStops + stop
                }
            }
        )

        Text(
            text = "Selected stops: ${selectedStops.joinToString(" → ") { it.city }}",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ControlButtons(
            isMetric = isMetric,
            canCalculate = selectedStops.size >= 2,
            onCalculate = {
                currentStopIndex = 0
                coveredDistance = 0.0
                totalDistance = calculateTotalDistance(selectedStops)
                isRouteCalculated = true  // Show StopsList when route is calculated
            },
            onUnitToggle = { isMetric = !isMetric },
            onReset = {
                selectedStops = emptyList()
                currentStopIndex = 0
                totalDistance = 0.0
                coveredDistance = 0.0
                isMetric = true
                isRouteCalculated = false  // Hide StopsList when reset
            }
        )

        NavigationButtons(
            currentStopIndex = currentStopIndex,
            selectedStops = selectedStops,
            onPrevious = {
                if (currentStopIndex > 0) {
                    coveredDistance -= calculateDistance(
                        selectedStops[currentStopIndex - 1],
                        selectedStops[currentStopIndex]
                    )
                    currentStopIndex--

                    lazyLoadedIndices = lazyLoadedIndices + currentStopIndex
                }
            },
            onNext = {
                if (currentStopIndex < selectedStops.size - 1) {
                    coveredDistance += calculateDistance(
                        selectedStops[currentStopIndex],
                        selectedStops[currentStopIndex + 1]
                    )
                    currentStopIndex++

                    lazyLoadedIndices = lazyLoadedIndices + currentStopIndex
                }
            }
        )

        ProgressDisplay(
            totalDistance = totalDistance,
            coveredDistance = coveredDistance,
            isMetric = isMetric
        )

        if (isRouteCalculated) {
            StopsList(
                selectedStops = selectedStops,
                currentStopIndex = currentStopIndex,
                isMetric = isMetric,
                lazyLoadedIndices = lazyLoadedIndices
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelector(
    allStops: List<Stop>,
    onStopSelected: (Stop) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<Stop?>(null) }

    Column {
        Text(
            text = "Select City to Add:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = selectedCity?.city ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allStops.forEach { stop ->
                        DropdownMenuItem(
                            text = { Text(stop.city) },
                            onClick = {
                                selectedCity = stop
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    selectedCity?.let { onStopSelected(it) }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add Stop")
            }
        }
    }
}

@Composable
fun ControlButtons(
    isMetric: Boolean,
    canCalculate: Boolean,
    onCalculate: () -> Unit,
    onUnitToggle: () -> Unit,
    onReset: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCalculate,
                enabled = canCalculate,
                modifier = Modifier.weight(1f)
            ) {
                Text("Calculate Route")
            }

            Button(
                onClick = onUnitToggle,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(if (isMetric) "Switch to Miles" else "Switch to KM")
            }
        }

        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Reset Journey")
        }
    }
}

@Composable
fun NavigationButtons(
    currentStopIndex: Int,
    selectedStops: List<Stop>,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            enabled = currentStopIndex > 0,
            modifier = Modifier.weight(1f)
        ) {
            Text("Previous Stop")
        }

        Button(
            onClick = onNext,
            enabled = currentStopIndex < selectedStops.size - 1,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text("Next Stop")
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ProgressDisplay(
    totalDistance: Double,
    coveredDistance: Double,
    isMetric: Boolean
) {
    val progress = if (totalDistance > 0) (coveredDistance / totalDistance * 100).toInt() else 0
    val remainingDistance = totalDistance - coveredDistance
    val unit = if (isMetric) "km" else "miles"
    val conversionFactor = if (isMetric) 1.0 else 0.621371

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = """
                Progress: $progress%
                Distance covered: ${String.format("%.1f", coveredDistance * conversionFactor)} $unit
                Distance remaining: ${String.format("%.1f", remainingDistance * conversionFactor)} $unit
                Total distance: ${String.format("%.1f", totalDistance * conversionFactor)} $unit
            """.trimIndent(),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun StopsList(
    selectedStops: List<Stop>,
    currentStopIndex: Int,
    isMetric: Boolean,
    lazyLoadedIndices: Set<Int>
) {
    LazyColumn {
        // Display first 3 stops traditionally
        items(selectedStops.take(3)) { stop ->
            val index = selectedStops.indexOf(stop)
            StopItem(
                stop = stop,
                previousStop = if (index > 0) selectedStops[index - 1] else null,
                isCurrentStop = index == currentStopIndex,
                isMetric = isMetric
            )
        }

        // Lazy display additional stops
        items(selectedStops.drop(3).withIndex().toList()) { (relativeIndex, stop) ->
            val actualIndex = relativeIndex + 3
            if (actualIndex in lazyLoadedIndices) {
                StopItem(
                    stop = stop,
                    previousStop = if (actualIndex > 0) selectedStops[actualIndex - 1] else null,
                    isCurrentStop = actualIndex == currentStopIndex,
                    isMetric = isMetric,
                    isDynamicallyLoaded = true
                )
            }
        }
    }
}


@Composable
fun StopItem(
    stop: Stop,
    previousStop: Stop?,
    isCurrentStop: Boolean,
    isMetric: Boolean,
    isDynamicallyLoaded: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(if (isCurrentStop) MaterialTheme.colorScheme.primary else Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stop.city,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentStop) Color.Black else Color.DarkGray
            )

            previousStop?.let {
                val distance = calculateDistance(it, stop)
                val time= distance/900
                val displayDistance = if (isMetric) distance else distance * 0.621371
                Text(
                    text = "Distance from ${it.city}: %.1f ${if (isMetric) "km" else "miles"}".format(displayDistance),
                    fontSize = 14.sp,
                    color = if (isCurrentStop) Color.Black else Color.DarkGray
                )
                Text(
                    text = "Time from ${it.city}: %.1f ${"hrs"}".format(time),
                    fontSize = 14.sp,
                    color = if (isCurrentStop) Color.Black else Color.DarkGray
                )
            }

            Text(
                text = "Coordinates: %.4f, %.4f".format(stop.latitude, stop.longitude),
                fontSize = 14.sp,
                color = if (isCurrentStop) Color.Black else Color.DarkGray
            )

            Text(
                text = if (stop.visaRequired) "Visa required: ${stop.visaDetails}" else "No visa required",
                fontSize = 14.sp,
                color = if (isCurrentStop) Color.Black else Color.DarkGray
            )

            if (isDynamicallyLoaded) {
                Text(
                    text = "Dynamically Loaded",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


private fun calculateDistance(start: Stop, end: Stop): Double {
    val radius = 6378.0

    val lat1 = Math.toRadians(start.latitude)
    val lat2 = Math.toRadians(end.latitude)
    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLon = Math.toRadians(end.longitude - start.longitude)

    val a = sin(dLat/2) * sin(dLat/2) +
            cos(lat1) * cos(lat2) *
            sin(dLon/2) * sin(dLon/2)

    val c = 2 * atan2(sqrt(a), sqrt(1-a))

    return radius * c
}

private fun calculateTotalDistance(stops: List<Stop>): Double {
    var total = 0.0
    for (i in 0 until stops.size - 1) {
        total += calculateDistance(stops[i], stops[i + 1])
    }
    return total
}