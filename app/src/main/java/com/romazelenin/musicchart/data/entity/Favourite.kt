package com.romazelenin.musicchart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val artist_id: Long
)
