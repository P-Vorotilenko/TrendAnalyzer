package vorotilenko.trendanalyzer.viewmodel

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import vorotilenko.trendanalyzer.R
import kotlin.random.Random

/**
 * Helps to get pretty colors to display the chart data.
 */
class DatasetsColorUtil(private val context: Context) {
    /**
     * Array that contains flags which define what colors are currently free.
     */
    private val colorIsFree = BooleanArray(10) { true }

    /**
     * Sets values to RGB color components
     */
    private fun setColorComponents(
        component1: IntArray,
        component2: IntArray,
        component3: IntArray,
        random: Random
    ) {
        component1[0] = 218
        when (random.nextInt(1, 3)) {
            1 -> {
                component2[0] = 3
                component3[0] = random.nextInt(3, 219)
            }
            2 -> {
                component3[0] = 3
                component2[0] = random.nextInt(3, 219)
            }
        }
    }

    /**
     * Returns pretty random color
     */
    private fun getRandomColor(): Int {
        val random = Random(System.currentTimeMillis())
        val r = intArrayOf(0)
        val g = intArrayOf(0)
        val b = intArrayOf(0)

        when (random.nextInt(1, 4)) {
            1 -> setColorComponents(r, g, b, random)
            2 -> setColorComponents(g, r, b, random)
            3 -> setColorComponents(b, r, g, random)
        }
        return Color.rgb(r[0], g[0], b[0])
    }

    /**
     * @return The color for the dataset
     */
    fun reserveColor() = when {
        colorIsFree[0] -> {
            colorIsFree[0] = false
            ContextCompat.getColor(context, R.color.teal_200)
        }
        colorIsFree[1] -> {
            colorIsFree[1] = false
            ContextCompat.getColor(context, R.color.chart_color_2)
        }
        colorIsFree[2] -> {
            colorIsFree[2] = false
            ContextCompat.getColor(context, R.color.chart_color_3)
        }
        colorIsFree[3] -> {
            colorIsFree[3] = false
            ContextCompat.getColor(context, R.color.chart_color_4)
        }
        colorIsFree[4] -> {
            colorIsFree[4] = false
            ContextCompat.getColor(context, R.color.chart_color_5)
        }
        colorIsFree[5] -> {
            colorIsFree[5] = false
            ContextCompat.getColor(context, R.color.chart_color_6)
        }
        colorIsFree[6] -> {
            colorIsFree[6] = false
            ContextCompat.getColor(context, R.color.chart_color_7)
        }
        colorIsFree[7] -> {
            colorIsFree[7] = false
            ContextCompat.getColor(context, R.color.chart_color_8)
        }
        colorIsFree[8] -> {
            colorIsFree[8] = false
            ContextCompat.getColor(context, R.color.chart_color_9)
        }
        colorIsFree[9] -> {
            colorIsFree[9] = false
            ContextCompat.getColor(context, R.color.chart_color_10)
        }
        else -> getRandomColor()
    }

    /**
     * Unlocks the color. To lock it, call [lockColor].
     */
    fun unlockColor(color: Int?) {
        when (color) {
            ContextCompat.getColor(context, R.color.teal_200) -> colorIsFree[0] = true
            ContextCompat.getColor(context, R.color.chart_color_2) -> colorIsFree[1] = true
            ContextCompat.getColor(context, R.color.chart_color_3) -> colorIsFree[2] = true
            ContextCompat.getColor(context, R.color.chart_color_4) -> colorIsFree[3] = true
            ContextCompat.getColor(context, R.color.chart_color_5) -> colorIsFree[4] = true
            ContextCompat.getColor(context, R.color.chart_color_6) -> colorIsFree[5] = true
            ContextCompat.getColor(context, R.color.chart_color_7) -> colorIsFree[6] = true
            ContextCompat.getColor(context, R.color.chart_color_8) -> colorIsFree[7] = true
            ContextCompat.getColor(context, R.color.chart_color_9) -> colorIsFree[8] = true
            ContextCompat.getColor(context, R.color.chart_color_10) -> colorIsFree[9] = true
        }
    }

    /**
     * Locks the color. To unlock it, call [unlockColor].
     */
    fun lockColor(color: Int) {
        when (color) {
            ContextCompat.getColor(context, R.color.teal_200) -> colorIsFree[0] = false
            ContextCompat.getColor(context, R.color.chart_color_2) -> colorIsFree[1] = false
            ContextCompat.getColor(context, R.color.chart_color_3) -> colorIsFree[2] = false
            ContextCompat.getColor(context, R.color.chart_color_4) -> colorIsFree[3] = false
            ContextCompat.getColor(context, R.color.chart_color_5) -> colorIsFree[4] = false
            ContextCompat.getColor(context, R.color.chart_color_6) -> colorIsFree[5] = false
            ContextCompat.getColor(context, R.color.chart_color_7) -> colorIsFree[6] = false
            ContextCompat.getColor(context, R.color.chart_color_8) -> colorIsFree[7] = false
            ContextCompat.getColor(context, R.color.chart_color_9) -> colorIsFree[8] = false
            ContextCompat.getColor(context, R.color.chart_color_10) -> colorIsFree[9] = false
        }
    }
}