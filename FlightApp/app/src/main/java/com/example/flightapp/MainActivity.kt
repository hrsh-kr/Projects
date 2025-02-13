package com.example.flightapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.TypedValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Stop(
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val visaRequired: Boolean,
    val visaDetails: String
)

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var distanceUnitButton: Button
    private lateinit var nextStopButton: Button
    private lateinit var previousStopButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var citySpinner: Spinner
    private lateinit var addStopButton: Button
    private lateinit var calculateButton: Button
    private lateinit var resetButton: Button
    private lateinit var selectedStopsText: TextView

    private var allStops = mutableListOf<Stop>()
    private var selectedStops = mutableListOf<Stop>()
    private var currentStopIndex = 0
    private var isMetric = true
    private var totalDistance = 0.0
    private var coveredDistance = 0.0
    private var isLazyLoadingEnabled = false

    private fun getThemeColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        loadStops()
        setupSpinner()
        setupButtons()
        setupRecyclerView()
    }

    private fun loadStops() {
        try {
            val inputStream = resources.openRawResource(R.raw.stops)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size == 5) {
                    allStops.add(
                        Stop(
                            city = parts[0].trim(),
                            latitude = parts[1].toDouble(),
                            longitude = parts[2].toDouble(),
                            visaRequired = parts[3].toBoolean(),
                            visaDetails = parts[4].trim()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading stops: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StopsAdapter(
            allStops = selectedStops,
            visibleStops = selectedStops.take(3),
            isMetric = isMetric,
            currentStopIndex = currentStopIndex,
            highlightColor = getThemeColor()
        )
    }

    private fun updateRecyclerView() {
        val adapter = recyclerView.adapter as StopsAdapter
        isLazyLoadingEnabled = currentStopIndex >= 3

        if (isLazyLoadingEnabled) {
            val visibleStops = mutableListOf<Stop>()
            visibleStops.addAll(selectedStops.take(3))

            if (currentStopIndex >= 3) {
                visibleStops.add(selectedStops[currentStopIndex])
                if (currentStopIndex + 1 < selectedStops.size) {
                    visibleStops.add(selectedStops[currentStopIndex + 1])
                }
            }

            adapter.updateStops(visibleStops)
        } else {
            adapter.updateStops(getInitialStops())
        }

        adapter.updateCurrentStop(currentStopIndex)
    }

    private fun getInitialStops(): List<Stop> {
        return selectedStops.take(3)
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        distanceUnitButton = findViewById(R.id.unitToggleButton)
        nextStopButton = findViewById(R.id.nextStopButton)
        previousStopButton = findViewById(R.id.previousStopButton)
        recyclerView = findViewById(R.id.stopsRecyclerView)
        citySpinner = findViewById(R.id.citySpinner)
        addStopButton = findViewById(R.id.addStopButton)
        calculateButton = findViewById(R.id.calculateButton)
        resetButton = findViewById(R.id.resetButton)
        selectedStopsText = findViewById(R.id.selectedStopsText)

        // Initially disable navigation buttons until route is calculated
        nextStopButton.isEnabled = false
        previousStopButton.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun resetJourney() {
        selectedStops.clear()
        currentStopIndex = 0
        isLazyLoadingEnabled = false

        totalDistance = 0.0
        coveredDistance = 0.0

        isMetric = true
        distanceUnitButton.text = "Switch to Miles"

        selectedStopsText.text = "Selected stops: None"
        progressBar.progress = 0
        progressText.text = "Progress: 0%\nDistance covered: 0.0 km\nDistance remaining: 0.0 km\nTotal distance: 0.0 km"

        citySpinner.setSelection(0)

        nextStopButton.isEnabled = false
        previousStopButton.isEnabled = false

        calculateButton.isEnabled = true

        recyclerView.adapter = StopsAdapter(
            allStops = selectedStops,
            visibleStops = emptyList(),
            isMetric = isMetric,
            currentStopIndex = currentStopIndex,
            highlightColor = getThemeColor()
        )

        Toast.makeText(this, "Journey reset successfully", Toast.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        val cityNames = allStops.map { it.city }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter
    }

    private fun setupButtons() {
        addStopButton.setOnClickListener {
            val selectedStop = allStops[citySpinner.selectedItemPosition]
            if (!selectedStops.contains(selectedStop)) {
                selectedStops.add(selectedStop)
                updateSelectedStopsText()
            } else {
                Toast.makeText(this, "City already added to route", Toast.LENGTH_SHORT).show()
            }
        }

        calculateButton.setOnClickListener {
            if (selectedStops.size < 2) {
                Toast.makeText(this, "Please select at least 2 stops", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            calculateRoute()
        }

        resetButton.setOnClickListener {
            resetJourney()
        }

        distanceUnitButton.setOnClickListener {
            isMetric = !isMetric
            distanceUnitButton.text = if (isMetric) "Switch to Miles" else "Switch to KM"
            updateProgress()
            (recyclerView.adapter as StopsAdapter).updateUnit(isMetric)
        }

        nextStopButton.setOnClickListener {
            if (currentStopIndex < selectedStops.size - 1) {
                coveredDistance += calculateDistance(
                    selectedStops[currentStopIndex],
                    selectedStops[currentStopIndex + 1]
                )
                currentStopIndex++
                updateProgress()
                updateNavigationButtons()
                updateRecyclerView()
            }
        }

        previousStopButton.setOnClickListener {
            if (currentStopIndex > 0) {
                coveredDistance -= calculateDistance(
                    selectedStops[currentStopIndex - 1],
                    selectedStops[currentStopIndex]
                )
                currentStopIndex--
                updateProgress()
                updateNavigationButtons()
                updateRecyclerView()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedStopsText() {
        selectedStopsText.text = "Selected stops: ${selectedStops.joinToString(" → ") { it.city }}"
    }

    private fun calculateRoute() {
        currentStopIndex = 0
        coveredDistance = 0.0
        totalDistance = 0.0

        for (i in 0 until selectedStops.size - 1) {
            totalDistance += calculateDistance(selectedStops[i], selectedStops[i + 1])
        }

        updateNavigationButtons()
        updateRecyclerView()
        updateProgress()
    }


    private fun updateNavigationButtons() {
        previousStopButton.isEnabled = currentStopIndex > 0
        nextStopButton.isEnabled = currentStopIndex < selectedStops.size - 1
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

    @SuppressLint("SetTextI18n")
    private fun updateProgress() {
        if (totalDistance == 0.0) return

        val progress = (coveredDistance / totalDistance * 100).toInt()
        progressBar.progress = progress

        val remainingDistance = totalDistance - coveredDistance
        val unitText = if (isMetric) "km" else "miles"
        val coveredText = if (isMetric)
            coveredDistance
        else
            coveredDistance * 0.621371

        val remainingText = if (isMetric)
            remainingDistance
        else
            remainingDistance * 0.621371

        progressText.text = "Progress: ${progress}%\n" +
                "Distance covered: %.1f %s\n".format(coveredText, unitText) +
                "Distance remaining: %.1f %s\n".format(remainingText, unitText) +
                "Total distance: %.1f %s".format(
                    if (isMetric) totalDistance else totalDistance * 0.621371,
                    unitText
                )
    }
}