package io.simsim.fit.recorder.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleSlider(
    modifier: Modifier,
    value: Float
    /** 0-360 */
    ,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
    trackerColor: Color = MaterialTheme.colorScheme.primary,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable () -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = SliderDefaults.colors(
                activeTickColor = trackerColor,
                thumbColor = thumbColor
            ),
            enabled = enabled
        )
    },
) {
    val trackerWidth = with(LocalDensity.current) {
        4.dp.toPx()
    }
    var centerOffset = Offset.Zero
    val thumbDraggableModifier = Modifier.pointerInput(true) {
        var startOffset = Offset.Zero
        detectDragGestures(
            onDragStart = {
                startOffset = it
            },
            onDrag = { change, dragAmount ->
                val endOffset = startOffset + dragAmount
                val vector1 = startOffset.x - centerOffset.x to startOffset.y - centerOffset.y
                val vector2 = endOffset.x - centerOffset.x to endOffset.y - centerOffset.y
                onValueChange(value + calculateAngle(vector1, vector2))
            }

        )
    }
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId("thumb")
                    .then(thumbDraggableModifier)
            ) {
                thumb()
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .layoutId("tracker")) {
                drawArc(
                    color = trackerColor,
                    startAngle = -90f,
                    sweepAngle = value,
                    useCenter = false,
                    style = Stroke(
                        width = trackerWidth
                    )
                )
            }

        }, modifier = modifier
            .requiredSizeIn(
                minWidth = 20.dp,
                minHeight = 20.dp
            )
            .background(Color.LightGray)
    ) { measurables, constraints ->
        val thumbPlaceable = measurables.first {
            it.layoutId == "thumb"
        }.measure(constraints)
        val trackerPlaceable = measurables.first {
            it.layoutId == "tracker"
        }.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth - thumbPlaceable.width,
                maxHeight = constraints.maxHeight - thumbPlaceable.height,
                minWidth = 0,
                minHeight = 0
            )
        )
        val layoutSize = constraints.maxWidth - thumbPlaceable.width
        centerOffset = Offset(
            layoutSize / 2.0f, layoutSize / 2.0f
        )
        val thumbY = (layoutSize / 2) * (1 - cos(value.toDouble())).roundToInt()
        val thumbX = (layoutSize / 2) * (1 + sin(value.toDouble())).roundToInt()
        layout(
            width = layoutSize,
            height = layoutSize,
        ) {
            trackerPlaceable.placeRelative(
                0, 0
            )
            thumbPlaceable.placeRelative(
                thumbX, thumbY
            )
        }

    }
}

@Composable
@Preview(
    showBackground = true
)
private fun CircleSlidePreview() = Box(modifier = Modifier.fillMaxSize()) {
    var angle by remember {
        mutableStateOf(90f)
    }
    CircleSlider(
        modifier = Modifier
            .size(100.dp)
            .align(Alignment.Center),
        value = angle,
        onValueChange = { angle = it }
    )
}

private fun calculateAngle(v1: Pair<Float, Float>, v2: Pair<Float, Float>): Float {
    val v1Length = sqrt(v1.first.pow(2) + v1.second.pow(2))
    val v2Length = sqrt(v2.first.pow(2) + v2.second.pow(2))
    return (v1.first * v2.first + v1.second * v2.second) / (v1Length + v2Length)
}