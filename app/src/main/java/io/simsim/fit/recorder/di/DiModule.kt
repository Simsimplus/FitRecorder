package io.simsim.fit.recorder.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.simsim.fit.recorder.data.db.FitDb

@Module
@InstallIn(SingletonComponent::class)
object DiModule {
    @Provides
    fun provideDb(
        ctx:Application
    ) = FitDb.getInstance(ctx)
}