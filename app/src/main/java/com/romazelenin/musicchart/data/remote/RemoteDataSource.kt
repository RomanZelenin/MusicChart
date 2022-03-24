package com.romazelenin.musicchart.data.remote

import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.Bio
import com.romazelenin.musicchart.data.service.Country

interface RemoteDataSource {

    suspend fun getTopArtists(page: Int, page_size: Int, country: Country): List<Artist>

    suspend fun getAlbums(
        artistId: Long,
        page: Int,
        page_size: Int,
        releaseDate: String = "desc"
    ): List<Album>

    suspend fun getArtistBio(artistName: String): Bio
}