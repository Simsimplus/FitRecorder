package io.simsim.fit.recorder.ui.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClickRippleLayout(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    val rippleColor = MaterialTheme.colorScheme.primary
    val rippleRadius = remember {
        Animatable(0f)
    }
    var ripplePosition by remember {
        mutableStateOf(Offset.Unspecified)
    }
    LaunchedEffect(key1 = ripplePosition) {
        if (ripplePosition.isSpecified) {
            rippleRadius.animateTo(250f)
            rippleRadius.snapTo(0f)
        }
    }
    val rippleModifier = modifier.then(
        Modifier
            .pointerInput(true) {
                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown()
                        ripplePosition = down.position
                    }
                }
            }
    )
    Surface(
        rippleModifier, shape, color, contentColor, tonalElevation, shadowElevation, border
    ) {
        Box {
            content()
            if (ripplePosition.isSpecified) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = rippleColor,
                        radius = rippleRadius.value,
                        center = ripplePosition,
                        style = Stroke()
                    )
                }
            }
        }
    }
}