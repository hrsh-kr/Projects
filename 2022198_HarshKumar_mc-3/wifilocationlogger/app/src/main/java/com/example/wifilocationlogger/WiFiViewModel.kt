package com.example.wifilocationlogger

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class WiFiViewModel(val repository: WifiDataRepository) : ViewModel() {
    // Expose the location data from the repository
    val locations: StateFlow<List<LocationData>> = repository.locationDataList
    
    fun scanWifiNetworks(locationName: String): Boolean {
        return repository.scanWifiNetworks(locationName)
    }
    
    fun fillRandomData(locationName: String) {
        repository.fillRandomData(locationName)
    }
    
    fun getTopAccessPoints(locationName: String, count: Int = 3): List<AccessPointInfo> {
        return repository.getTopAccessPoints(locationName, count)
    }
    
    fun getLocationStats(locationName: String): LocationStats? {
        return repository.getLocationStats(locationName)
    }
    
    fun getAllLocationStats(): List<Pair<String, LocationStats>> {
        return repository.getAllLocationStats()
    }
} 