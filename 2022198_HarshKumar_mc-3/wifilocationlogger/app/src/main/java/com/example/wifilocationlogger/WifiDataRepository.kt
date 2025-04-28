package com.example.wifilocationlogger

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class WifiDataRepository(private val context: Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    // MutableStateFlow to hold our location data
    private val _locationDataList = MutableStateFlow<List<LocationData>>(emptyList())
    val locationDataList: StateFlow<List<LocationData>> = _locationDataList.asStateFlow()
    
    // Store access point details
    private val _accessPointsList = MutableStateFlow<Map<String, List<AccessPointInfo>>>(emptyMap())
    val accessPointsList: StateFlow<Map<String, List<AccessPointInfo>>> = _accessPointsList.asStateFlow()
    
    val locationNames = listOf("Home", "Office", "Library")
    
    init {
        _locationDataList.value = locationNames.map { LocationData(it) }
        _accessPointsList.value = locationNames.associateWith { emptyList() }
    }
    
    fun scanWifiNetworks(locationName: String): Boolean {
        if (!wifiManager.isWifiEnabled) {
            return false
        }

        wifiManager.startScan()
        val results: List<ScanResult> = wifiManager.scanResults
        
        val rssiValues = results.map { it.level }
        
        val rssiMatrix = MutableList(100) { -100 }
        
        for (i in 0 until minOf(100, rssiValues.size)) {
            rssiMatrix[i] = rssiValues[i]
        }
        
        val currentList = _locationDataList.value.toMutableList()
        val locationIndex = currentList.indexOfFirst { it.name == locationName }
        
        if (locationIndex != -1) {
            currentList[locationIndex] = currentList[locationIndex].copy(rssiMatrix = rssiMatrix)
            _locationDataList.value = currentList
            
            // Store access point details - filter to prevent duplicate SSIDs
            val accessPoints = results
                .map { scanResult ->
                    AccessPointInfo(
                        ssid = scanResult.SSID.ifEmpty { "Hidden Network" },
                        bssid = scanResult.BSSID,
                        rssi = scanResult.level,
                        frequency = scanResult.frequency,
                        capabilities = scanResult.capabilities
                    )
                }
                .sortedByDescending { it.rssi }
                .distinctBy { it.ssid }
            
            val currentApMap = _accessPointsList.value.toMutableMap()
            currentApMap[locationName] = accessPoints
            _accessPointsList.value = currentApMap
            
            return true
        }
        
        return false
    }
    
    fun fillRandomData(locationName: String) {
        val currentList = _locationDataList.value.toMutableList()
        val locationIndex = currentList.indexOfFirst { it.name == locationName }
        
        if (locationIndex != -1) {
            val currentMatrix = currentList[locationIndex].rssiMatrix.toMutableList()
            
            // Fill empty slots (-100 values) with random RSSI values
            for (i in currentMatrix.indices) {
                if (currentMatrix[i] == -100) {
                    currentMatrix[i] = Random.nextInt(-95, -40)
                }
            }
            
            currentList[locationIndex] = currentList[locationIndex].copy(rssiMatrix = currentMatrix)
            _locationDataList.value = currentList
        }
    }
    
    fun getTopAccessPoints(locationName: String, count: Int = 3): List<AccessPointInfo> {
        return _accessPointsList.value[locationName]?.take(count) ?: emptyList()
    }
    
    fun getLocationStats(locationName: String): LocationStats? {
        val location = _locationDataList.value.find { it.name == locationName } ?: return null
        val activeSignals = location.rssiMatrix.filter { it > -100 }
        
        return if (activeSignals.isEmpty()) {
            LocationStats(0, 0, 0, 0)
        } else {
            LocationStats(
                minSignal = activeSignals.minOrNull() ?: 0,
                maxSignal = activeSignals.maxOrNull() ?: 0,
                avgSignal = activeSignals.average().toInt(),
                count = activeSignals.size
            )
        }
    }
    
    fun getAllLocationStats(): List<Pair<String, LocationStats>> {
        return _locationDataList.value.map { 
            it.name to (getLocationStats(it.name) ?: LocationStats(0, 0, 0, 0))
        }
    }
}

data class LocationStats(
    val minSignal: Int,
    val maxSignal: Int,
    val avgSignal: Int,
    val count: Int
)

data class AccessPointInfo(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequency: Int,
    val capabilities: String
) 