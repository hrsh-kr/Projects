package com.example.wifilocationlogger

data class LocationData(
    val name: String,
    val rssiMatrix: MutableList<Int> = MutableList(100) { -100 } // represents no signal
)
