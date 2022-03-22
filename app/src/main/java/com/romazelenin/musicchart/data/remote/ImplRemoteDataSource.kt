package com.romazelenin.musicchart.data.remote

import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.data.service.MusicServiceApi
import javax.inject.Inject

class ImplRemoteDataSource @Inject constructor(private val musicServiceApi: MusicServiceApi) :
    RemoteDataSource {

    override suspend fun getTopArtists(page: Int, page_size: Int, country: Country): List<Artist> {
        return musicServiceApi.getTopArtists(page, page_size, country)
            .message
            .body
            .artist_list
            .map { it.artist }
    }

    override suspend fun getAlbums(
        artistId: Long,
        page: Int,
        page_size: Int,
        releaseDate: String
    ): List<Album> {
        return musicServiceApi.getAlbums(artistId, page, page_size, releaseDate)
            .message
            .body
            .album_list
            .map { it.album }
    }
}