package com.romazelenin.musicchart.data.service

import retrofit2.http.GET
import retrofit2.http.Query

interface AlbumCoverServiceApi {

    @GET("oembed/")
    suspend fun getAlbumCover(@Query("url") id: String): CoverAlbumResponse
}

data class CoverAlbumResponse(val thumbnail_url: String)