package io.simsim.fit.recorder.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun MeasureUnconstrainedViewSzie(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Dp, measuredHeight: Dp) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val subcompose = subcompose("viewToMeasure", viewToMeasure)[0]
            .measure(Constraints())
        val contentPlaceable = subcompose("content") {
            content(subcompose.width.toDp(), subcompose.height.toDp())
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}