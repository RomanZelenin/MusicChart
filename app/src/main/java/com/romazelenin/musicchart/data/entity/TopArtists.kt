package com.romazelenin.musicchart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TopArtists(
    @PrimaryKey(autoGenerate = true) val place: Int = 0,
    val artist_id: Long
)
