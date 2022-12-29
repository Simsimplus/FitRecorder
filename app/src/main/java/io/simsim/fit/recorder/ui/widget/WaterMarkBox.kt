package io.simsim.fit.recorder.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@OptIn(ExperimentalTextApi::class)
@Composable
fun WaterMarkBox(
    modifier: Modifier = Modifier,
    watermark: String,
    watermarkStyle: TextStyle = WatermarkStyleDefault,
    watermarkPaddingCount: Int = watermark.length * 2,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier, contentAlignment, propagateMinConstraints) {
        val textMeasurer = rememberTextMeasurer()
        val textPaddingRandom = (1..2)
        val watermarkLayerSizeDp = with(LocalConfiguration.current) {
            sqrt(
                screenHeightDp.toFloat().pow(2) + screenWidthDp.toFloat().pow(2)
            ).dp
        }
        val watermarkLayerSizePx = with(LocalDensity.current) {
            watermarkLayerSizeDp.toPx().roundToInt()
        }
        content()
        Layout(
            {},
            measurePolicy = { _, _ -> layout(watermarkLayerSizePx, watermarkLayerSizePx) {} },
            modifier = modifier
                .rotate(-45f)
                .size(watermarkLayerSizeDp * 2)
                .drawBehind {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = List(1000) {
                            watermark
                        }.joinToString(
                            " ".repeat(watermarkPaddingCount * textPaddingRandom.random())
                        ),
                        softWrap = true,
                        style = watermarkStyle
                    )
                })
    }
}

val WatermarkStyleDefault = TextStyle.Default.copy(
    color = Color.LightGray.copy(alpha = 0.5f),
    fontSize = 20.sp,
    lineHeight = 60.sp
)