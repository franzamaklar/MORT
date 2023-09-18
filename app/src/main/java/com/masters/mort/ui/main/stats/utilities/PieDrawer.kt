package com.masters.mort.ui.main.stats.utilities

import android.app.Activity
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.masters.mort.R

class PieDrawer {

    companion object {
        fun createPieWithStatistics(
            pieChart: PieChart,
            dataToShow: MutableMap<String, Int>,
            propertiesData: Array<String>,
            activity: Activity
        ): Boolean {
            if (!dataToShow.all { it.value == 0 }) {
                setUpPieChart(pieChart, activity)
                val entries: ArrayList<PieEntry> = ArrayList()
                val colorsWithProperties: MutableMap<String, Int> = mutableMapOf()
                colorsWithProperties[propertiesData.elementAt(0)] =
                    activity.resources.getColor(R.color.pieColorTwo, activity.resources.newTheme())
                colorsWithProperties[propertiesData.elementAt(1)] =
                    activity.resources.getColor(R.color.pieColorOne, activity.resources.newTheme())
                colorsWithProperties[propertiesData.elementAt(2)] =
                    activity.resources.getColor(R.color.pieColorThree,activity.resources.newTheme())
                colorsWithProperties[propertiesData.elementAt(3)] =
                    activity.resources.getColor(R.color.mainColor, activity.resources.newTheme())
                colorsWithProperties[propertiesData.elementAt(4)] =
                    activity.resources.getColor(R.color.accentColor, activity.resources.newTheme())

                for (data in dataToShow) {
                    if (data.value != 0) {
                        entries.add(PieEntry(data.value.toFloat()))
                    }
                }

                val dataSet = PieDataSet(entries, "Statistics")
                dataSet.setDrawIcons(false)

                dataSet.sliceSpace = 5F
                dataSet.iconsOffset = MPPointF(0F, 40F)
                dataSet.selectionShift = 5F

                val colorsToShow: ArrayList<Int> = ArrayList()

                colorsWithProperties.iterator().forEach {
                    if (dataToShow.any { data -> data.key == it.key && data.value != 0 }) {
                        colorsToShow.add(it.value)
                    }
                }

                dataSet.colors = colorsToShow

                val data = PieData(dataSet)
                data.setValueFormatter(PercentFormatter())
                data.setValueTextSize(15F)
                data.setValueTypeface(Typeface.DEFAULT_BOLD)
                data.setValueTextColor(activity.resources.getColor(R.color.black, activity.resources.newTheme()))
                pieChart.data = data

                pieChart.highlightValues(null)
                pieChart.invalidate()
                return true
            }else{
                return false
            }
        }

        private fun setUpPieChart(pieChart: PieChart, activity: Activity) {
            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setExtraOffsets(5F, 10F, 5F, 5F)
            pieChart.dragDecelerationFrictionCoef = 0.95F
            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(
                activity.resources.getColor(
                    R.color.white,
                    activity.resources.newTheme()
                )
            )
            pieChart.setTransparentCircleColor(
                activity.resources.getColor(
                    R.color.white,
                    activity.resources.newTheme()
                )
            )
            pieChart.setTransparentCircleAlpha(110)
            pieChart.setDrawCenterText(true)
            pieChart.rotationAngle = 0F
            pieChart.isRotationEnabled = true
            pieChart.isHighlightPerTapEnabled = true
            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(
                activity.resources.getColor(
                    R.color.white,
                    activity.resources.newTheme()
                )
            )
            pieChart.setEntryLabelTextSize(12F)
        }
    }
}