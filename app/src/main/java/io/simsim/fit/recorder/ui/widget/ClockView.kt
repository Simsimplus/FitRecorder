package io.simsim.fit.recorder.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import io.simsim.fit.recorder.R
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    ctx: Context,
    attributeSet: AttributeSet? = null,
    defaultStyle: Int = 0
) : View(
    ctx, attributeSet, defaultStyle
) {
    val clockCirclePaint by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }
    val hourPaint by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 20f
        }
    }

    val minutePaint by lazy {
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.purple_200)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 10f
        }
    }

    val secondPaint by lazy {
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.purple_200)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 8f
        }
    }

    val currentTime
        get() = LocalDateTime.now()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = measuredHeight / 2f
        val cy = measuredHeight / 2f
        canvas.drawCircle(
            cx,
            cy,
            measuredHeight / 2f,
            clockCirclePaint
        )
        val hourDegree = (90 - (currentTime.hour / 24f) * 360)
        val minuteDegree = (90 - (currentTime.minute / 60f) * 360)
        val secondDegree = (90 - (currentTime.second / 60f) * 360)
        val hourEndPoint = (
                cx + measuredHeight * 0.2f * cos(hourDegree)
                ) to (
                cy - measuredHeight * 0.2f * sin(hourDegree)
                )
        val minuteEndPoint = (
                cx + measuredHeight * 0.3f * cos(minuteDegree)
                ) to (
                cy - measuredHeight * 0.3f * sin(minuteDegree)
                )
        val secondEndPoint = (
                cx + measuredHeight * 0.4f * cos(secondDegree)
                ) to (
                cy - measuredHeight * 0.4f * sin(secondDegree)
                )
        canvas.drawLine(
            cx, cy, hourEndPoint.first, hourEndPoint.second, hourPaint
        )
        canvas.drawLine(
            cx, cy, minuteEndPoint.first, minuteEndPoint.second, minutePaint
        )
        canvas.drawLine(
            cx, cy, secondEndPoint.first, secondEndPoint.second, secondPaint
        )
        postDelayed(1000) {
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
            .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(
            size, size
        )
    }
}