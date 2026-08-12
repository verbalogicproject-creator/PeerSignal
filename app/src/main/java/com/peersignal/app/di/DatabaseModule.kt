package com.peersignal.app.di

import android.content.Context
import androidx.room.Room
import com.peersignal.app.data.local.PeerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePeerDatabase(@ApplicationContext context: Context): PeerDatabase {
        return Room.databaseBuilder(
            context,
            PeerDatabase::class.java,
            "peer_signal_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBeaconSignalDao(db: PeerDatabase) = db.beaconSignalDao()
}
