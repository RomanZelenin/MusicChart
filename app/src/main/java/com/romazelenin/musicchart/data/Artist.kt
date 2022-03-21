package com.romazelenin.musicchart.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey val artist_id: Long,
    val artist_name: String,
    val artist_rating: Int,
)