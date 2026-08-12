package com.peersignal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beacon_signals")
data class BeaconSignalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val codeSnippet: String?,
    val architectureNotes: String,
    val githubDiscussionUrl: String?,
    val timestamp: Long
)
