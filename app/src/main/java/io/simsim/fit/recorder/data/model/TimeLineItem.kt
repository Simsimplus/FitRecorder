package io.simsim.fit.recorder.data.model

import java.time.LocalDateTime

data class TimeLineItem<ITEM>(
    val time:LocalDateTime,
    val item: ITEM
)
