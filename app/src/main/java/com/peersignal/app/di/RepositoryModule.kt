package com.peersignal.app.di

import com.peersignal.app.data.repository.BeaconRepositoryImpl
import com.peersignal.app.domain.repository.BeaconRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindBeaconRepository(
        impl: BeaconRepositoryImpl
    ): BeaconRepository
}
