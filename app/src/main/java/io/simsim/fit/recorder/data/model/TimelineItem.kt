package io.simsim.fit.recorder.data.model

import java.time.LocalDateTime

data class TimelineItem<ITEM>(
    val time: LocalDateTime,
    val item: ITEM
)
