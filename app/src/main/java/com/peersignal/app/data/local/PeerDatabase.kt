package com.peersignal.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peersignal.app.data.local.dao.BeaconSignalDao
import com.peersignal.app.data.local.entity.BeaconSignalEntity

@Database(entities = [BeaconSignalEntity::class], version = 1, exportSchema = false)
abstract class PeerDatabase : RoomDatabase() {
    abstract fun beaconSignalDao(): BeaconSignalDao
}
