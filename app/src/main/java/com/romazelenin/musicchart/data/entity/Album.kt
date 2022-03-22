package com.romazelenin.musicchart.data.entity

data class Album(
    val album_id: Long,
    val album_name: String,
    val album_release_date: String,
    val external_ids: Map<String, List<String>>
)