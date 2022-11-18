package io.simsim.fit.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.simsim.fit.recorder.data.model.TimelineItem
import io.simsim.fit.recorder.ui.TimeLineScreen
import io.simsim.fit.recorder.ui.theme.FitRecorderTheme
import java.time.LocalDateTime
import java.util.*

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
                    TimeLineScreen(
                        modifier = Modifier.fillMaxSize(),
                        timelines = List(15) {
                            TimelineItem(
                                LocalDateTime.now(),
                                UUID.randomUUID().toString().take(6)
                            )
                        }
                    ) {
                        Text(modifier = Modifier.padding(16.dp), text = it)
                    }
                }
            }
        }
    }
}