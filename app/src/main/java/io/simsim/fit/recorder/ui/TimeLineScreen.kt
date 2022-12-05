package io.simsim.fit.recorder.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.simsim.fit.recorder.data.model.TimelineItem
import io.simsim.fit.recorder.utils.compose.MeasureUnconstrainedViewSzie

@Composable
fun <ITEM> TimeLineScreen(
    modifier: Modifier = Modifier,
    timelines: List<TimelineItem<ITEM>>,
    holder: @Composable (ITEM) -> Unit,
) {
    val circleRadius = with(LocalDensity.current) {
        8.dp.toPx()
    }
    val markerColor = MaterialTheme.colorScheme.primary
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(timelines) { index, item ->
            MeasureUnconstrainedViewSzie(
                { holder(item.item) }
            ) { _, h ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(h)) {
                        if (index != 0) {
                            drawLine(
                                color = markerColor,
                                start = Offset(center.x, 0f),
                                end = Offset(center.x, center.y - circleRadius),
                                strokeWidth = 10f
                            )
                        }
                        drawCircle(
                            color = markerColor,
                            radius = circleRadius
                        )
                        if (index != timelines.lastIndex) {
                            drawLine(
                                color = markerColor,
                                start = Offset(center.x, center.y + circleRadius),
                                end = Offset(center.x, center.y * 2),
                                strokeWidth = 10f
                            )
                        }
                    }
                    holder(item.item)
                }
            }

        }
    }
}

private fun DrawScope.drawDashLine(
    color: Color,
    start: Offset,
    end: Offset,
) = drawLine(
    color = color,
    start = start,
    end = end,
    strokeWidth = 3f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 20f),
)