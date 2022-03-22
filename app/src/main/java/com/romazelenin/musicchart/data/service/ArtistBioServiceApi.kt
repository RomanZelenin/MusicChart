package com.romazelenin.musicchart.data.service

import com.romazelenin.musicchart.data.entity.Bio
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistBioServiceApi {
    @GET("2.0/")
    suspend fun getBiography(
        @Query("method") method: String = "artist.getinfo",
        @Query("artist") artist: String
    ): ResponseAutoBioService
}

data class ResponseAutoBioService(
    val artist: BioArtist
)

data class BioArtist(
    val bio: Bio
)