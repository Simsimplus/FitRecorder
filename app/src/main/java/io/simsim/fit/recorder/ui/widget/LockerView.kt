package io.simsim.fit.recorder.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.simsim.fit.recorder.R
import kotlin.math.pow
import kotlin.math.sqrt


class LockerView @JvmOverloads constructor(
    ctx: Context,
    attributeSet: AttributeSet? = null,
    defaultStyle: Int = 0
) : View(
    ctx, attributeSet, defaultStyle
) {
    private val nodeCountPerLine = 3
    private val nodeRadius: Float = dp2Px(18)
    private val selectedNodeInnerRadius: Float = dp2Px(12)
    private val nodePadding: Float
        get() = (measuredWidth - nodeRadius * 2 * nodeCountPerLine) / (nodeCountPerLine + 1f)
    private val nodePaint by lazy {
        Paint().apply {
            this.style = Paint.Style.STROKE
            this.color = Color.BLACK
            this.strokeWidth = 10f
        }
    }
    private val selectedNodePaint by lazy {
        Paint().apply {
            this.style = Paint.Style.STROKE
            this.color = ContextCompat.getColor(context, R.color.purple_200)
            this.strokeWidth = 10f
        }
    }
    private val linePaint by lazy {
        Paint().apply {
            this.style = Paint.Style.FILL_AND_STROKE
            this.color = ContextCompat.getColor(context, R.color.purple_200)
            this.strokeWidth = 10f
        }
    }
    private val selectedNodes: MutableSet<Int> = mutableSetOf()
    private val nodeCenters: MutableList<Pair<Float, Float>> = mutableListOf()
    private var holdPosition: Pair<Float, Float>? = null
    private val gestureListener = GestureDetector(context, SimpleOnGestureListenerImpl())


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            Toast.makeText(context, "$selectedNodes", Toast.LENGTH_SHORT).show()
            selectedNodes.clear()
            holdPosition = null
            invalidate()
        }
        gestureListener.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        nodeCenters.clear()
        repeat(nodeCountPerLine) { line ->
            repeat(nodeCountPerLine) { indexInLine ->
                val cx =
                    nodePadding * (indexInLine + 1) + nodeRadius * 2 * (indexInLine) + nodeRadius
                val cy = nodePadding * (line + 1) + nodeRadius * 2 * (line) + nodeRadius
                nodeCenters.add(cx to cy)
                if ((line * nodeCountPerLine + indexInLine) in selectedNodes) {
                    canvas.drawCircle(
                        cx,
                        cy,
                        nodeRadius,
                        selectedNodePaint
                    )
                } else {
                    canvas.drawCircle(
                        cx,
                        cy,
                        nodeRadius,
                        nodePaint
                    )
                }

            }
        }
        selectedNodes.forEach {
            val c = nodeCenters[it]
            canvas.drawCircle(
                c.first, c.second, selectedNodeInnerRadius, linePaint
            )
        }
        selectedNodes.windowed(2, 1, false).forEach { window ->
            val startPoint = nodeCenters[window[0]]
            val endPoint = nodeCenters[window[1]]
            canvas.drawLine(
                startPoint.first, startPoint.second, endPoint.first, endPoint.second, linePaint
            )
        }

        if (holdPosition != null && selectedNodes.isNotEmpty()) {
            val startPoint = nodeCenters[selectedNodes.last()]
            val endPoint = holdPosition!!
            canvas.drawLine(
                startPoint.first, startPoint.second, endPoint.first, endPoint.second, linePaint
            )
        }
    }

    inner class SimpleOnGestureListenerImpl : SimpleOnGestureListener() {
        private var firstDownPosition: Pair<Float, Float> = 0f to 0f
        override fun onDown(e: MotionEvent): Boolean {
            firstDownPosition = e.x to e.y
            checkShouldSelectNode(
                firstDownPosition
            )
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            checkShouldSelectNode(e2.x to e2.y)
            return super.onScroll(e1, e2, distanceX, distanceY)
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

    private fun dp2Px(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
    }

    private fun checkShouldSelectNode(
        position: Pair<Float, Float>
    ) {
        val nearestNodeIndex = nodeCenters.withIndex().minByOrNull { (_, center) ->
            calPointsDistance(
                position, center
            )
        }?.index
        if (nearestNodeIndex != null && calPointsDistance(
                position,
                nodeCenters[nearestNodeIndex]
            ) <= nodeRadius
        ) {
            selectedNodes.add(
                nearestNodeIndex
            )
        } else {
            holdPosition = position
        }
        invalidate()
    }

    private fun calPointsDistance(
        p1: Pair<Float, Float>,
        p2: Pair<Float, Float>,
    ) = sqrt(
        (p1.first - p2.first).pow(2) + (p1.second - p2.second).pow(2)
    )
}