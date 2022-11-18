package io.simsim.fit.recorder.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.simsim.fit.recorder.data.model.FitRecord
import io.simsim.fit.recorder.utils.SingletonHolder

@Database(
    entities = [
        FitRecord::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FitDb:RoomDatabase() {
    abstract fun fitDao():FitDao
    companion object : SingletonHolder<Context, FitDb>(
        initializer = { ctx->
            Room.databaseBuilder(ctx,FitDb::class.java,"fit_db").build()
        }
    )
}