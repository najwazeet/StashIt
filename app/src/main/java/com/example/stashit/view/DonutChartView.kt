package com.example.stashit.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

data class DonutSlice(val value: Float, val color: Int)

class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var slices: List<DonutSlice> = emptyList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val rectF = RectF()

    fun setData(data: List<DonutSlice>) {
        slices = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        val strokeWidth = width.coerceAtMost(height) * 0.14f
        paint.strokeWidth = strokeWidth

        val padding = strokeWidth / 2 + 8f
        rectF.set(padding, padding, width - padding, height - padding)

        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f

        slices.forEach { slice ->
            val sweep = if (total == 0f) 0f else (slice.value / total) * 360f
            paint.color = slice.color
            canvas.drawArc(rectF, startAngle, sweep, false, paint)
            startAngle += sweep
        }
    }
}