package io.simsim.fit.recorder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.fit.recorder.ui.AnotherActivity
import io.simsim.fit.recorder.ui.theme.FitRecorderTheme
import io.simsim.fit.recorder.ui.widget.WaterMarkBox

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitRecorderTheme {
                var watermark by remember {
                    mutableStateOf(getRandomString())
                }
                var isFwPlaying by remember {
                    mutableStateOf(false)
                }
                WaterMarkBox(watermark = watermark) {
                    Box {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(onClick = {
                                watermark = getRandomString()
                            }) {
                                Text(text = "show bottom sheet")
                            }
                            OutlinedButton(onClick = {
                                isFwPlaying = true
                            }) {
                                Text(text = "shoot firework")
                            }
                            OutlinedButton(onClick = {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        AnotherActivity::class.java
                                    )
                                )
                            }) {
                                Text(text = "goto another activity")
                            }
                        }
                        FW(isFwPlaying)
                    }
                }
            }
        }
    }
}

@Composable
fun FW(isPlaying: Boolean = false) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fw))
    LottieAnimation(composition, isPlaying = isPlaying)
}

fun getRandomString(length: Int = 8): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

@Preview(showBackground = true)
@Composable
fun DownloadProgressMask(
    modifier: Modifier = Modifier,
    color: Color = Color.Black.copy(alpha = 0.7f),
    @FloatRange(from = 0.0, to = 1.1) percent: Float = 0.5f,
) {
    BoxWithConstraints(modifier) {
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