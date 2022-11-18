package io.simsim.fit.recorder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FitRecord(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val time: Long,
    val event: String,
    val count: String,
    val calorie: Long,
    val duration: Long,
)
