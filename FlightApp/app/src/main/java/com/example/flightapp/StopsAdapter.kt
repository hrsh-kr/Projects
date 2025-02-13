package com.example.flightapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class StopsAdapter(
    private var allStops: List<Stop>,
    private var visibleStops: List<Stop>,
    private var isMetric: Boolean,
    private var currentStopIndex: Int,
    private val highlightColor: Int
) : RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {

    class StopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityText: TextView = view.findViewById(R.id.cityText)
        val visaText: TextView = view.findViewById(R.id.visaText)
        val coordinatesText: TextView = view.findViewById(R.id.coordinatesText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stop_item, parent, false)
        return StopViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = visibleStops[position]

        holder.cityText.text = buildStopText(stop, position)
        holder.visaText.text = if (stop.visaRequired)
            "Visa required: ${stop.visaDetails}"
        else
            "No visa required"
        holder.coordinatesText.text = "Coordinates: %.4f, %.4f".format(
            stop.latitude,
            stop.longitude
        )

        // Highlight current stop
        holder.itemView.setBackgroundColor(
            if (allStops.indexOf(stop) == currentStopIndex)
                highlightColor
            else
                Color.TRANSPARENT
        )

        // Adjust text color based on highlight
        val isHighlighted = allStops.indexOf(stop) == currentStopIndex
        val textColor = if (isHighlighted) Color.WHITE else Color.GRAY

        holder.cityText.setTextColor(if (position >= 3) Color.BLUE else textColor)
        holder.visaText.setTextColor(textColor)
        holder.coordinatesText.setTextColor(textColor)
    }

    private fun buildStopText(stop: Stop, position: Int): String {
        val baseText = StringBuilder(stop.city)

        if (position > 0) {
            val previousStop = visibleStops[position - 1]
            val distance = calculateDistance(previousStop, stop)
            val displayDistance = if (isMetric) distance else distance * 0.621371
            val timehrs = distance/900
            val unit = if (isMetric) "km" else "miles"
            val unitt = "hrs"

            baseText.append("\nDistance from previous: %.1f %s".format(displayDistance, unit))
            baseText.append("\nTime from previous: %.1f %s".format(timehrs, unitt))
        }
        return baseText.toString()
    }

    override fun getItemCount() = visibleStops.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateStops(newStops: List<Stop>) {
        visibleStops = newStops
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUnit(metric: Boolean) {
        isMetric = metric
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCurrentStop(newIndex: Int) {
        currentStopIndex = newIndex
        notifyDataSetChanged()
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
}