package com.peersignal.app.di

import com.peersignal.app.data.remote.CompanionPythonEngineImpl
import com.peersignal.app.domain.repository.SpecialistEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the single engine implementation behind the [SpecialistEngine]
 * contract. This is the seam the in-app native engine will replace: swapping
 * the binding here is the only change needed downstream, because
 * TrainingRunCoordinator now depends on the interface rather than on
 * CompanionPythonEngineImpl directly.
 *
 * @Binds rather than @Provides: CompanionPythonEngineImpl and
 * TrainingRunCoordinator both already carry @Inject constructors, so the
 * previous @Provides methods were redundant bindings of types Hilt could
 * already construct.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindSpecialistEngine(impl: CompanionPythonEngineImpl): SpecialistEngine
}
