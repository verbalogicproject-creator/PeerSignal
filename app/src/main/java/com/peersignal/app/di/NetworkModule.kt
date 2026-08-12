package com.peersignal.app.di

import com.peersignal.app.data.remote.CompanionPythonEngineImpl
import com.peersignal.app.domain.repository.SpecialistEngine
import com.peersignal.app.domain.usecase.TrainingRunCoordinator
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
    fun provideCompanionPythonEngine(): CompanionPythonEngineImpl {
        return CompanionPythonEngineImpl()
    }

    @Provides
    @Singleton
    fun provideSpecialistEngine(impl: CompanionPythonEngineImpl): SpecialistEngine {
        return impl
    }

    @Provides
    @Singleton
    fun provideTrainingRunCoordinator(engine: CompanionPythonEngineImpl): TrainingRunCoordinator {
        return TrainingRunCoordinator(engine)
    }
}
