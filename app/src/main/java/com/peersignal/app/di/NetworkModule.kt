package com.peersignal.app.di

import com.peersignal.app.data.remote.GeminiApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGeminiApiClient(): GeminiApiClient {
        return GeminiApiClient()
    }
}
