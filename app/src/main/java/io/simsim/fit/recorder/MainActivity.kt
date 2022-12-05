package io.simsim.fit.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.fit.recorder.ui.theme.FitRecorderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitRecorderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DownloadProgressMask(Modifier.sizeIn(maxHeight = 60.dp, maxWidth = 60.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadProgressMask(
    modifier: Modifier = Modifier,
    color: Color = Color.Black.copy(alpha = 0.7f),
    @FloatRange(from = 0.0, to = 1.1) percent: Float = 0.5f,
) {
    BoxWithConstraints(modifier) {
        Image(painter = painterResource(id = R.mipmap.ic_launcher), contentDescription = "")
        val outerRadius = with(LocalDensity.current) {
            maxWidth.div(2f).toPx() - 10f
        }
        val innerRadius = with(LocalDensity.current) {
            maxWidth.div(2f).toPx() - 15f
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path1 = Path().apply {
                reset()
                moveTo(center.x + outerRadius, center.y)
                arcTo(Rect(center, outerRadius), 0f, 359.999f, false)
                lineTo(size.width, size.height / 2f)
                lineTo(size.width, 0f)
                lineTo(0f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(size.width, size.height / 2f)
                lineTo(size.width / 2f + outerRadius, size.height / 2f)
                close()
            }
            val path2 = Path().apply {
                reset()
                moveTo(center.x, center.y)
                lineTo(center.x, size.height / 2f - innerRadius)
                arcTo(Rect(center, innerRadius), -90f, -(359.999f * (1f - percent)), false)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(
                path1,
                color = color,
            )
            drawPath(
                path2,
                color = color,
            )
        }
    }
}