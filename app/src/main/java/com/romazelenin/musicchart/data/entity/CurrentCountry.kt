package com.romazelenin.musicchart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentCountry(
    @PrimaryKey val id: Int = 1,
    val country: String
)
