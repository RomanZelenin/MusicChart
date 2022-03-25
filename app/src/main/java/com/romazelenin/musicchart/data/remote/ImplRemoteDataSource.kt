package com.romazelenin.musicchart.data.remote

import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.Bio
import com.romazelenin.musicchart.data.service.ArtistBioServiceApi
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.data.service.MusicServiceApi
import com.romazelenin.musicchart.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImplRemoteDataSource @Inject constructor(
    private val musicServiceApi: MusicServiceApi,
    private val bioServiceApi: ArtistBioServiceApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    RemoteDataSource {

    override suspend fun getTopArtists(page: Int, page_size: Int, country: Country): List<Artist> =
        withContext(ioDispatcher) {
            musicServiceApi.getTopArtists(page, page_size, country)
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
    ): List<Album> = withContext(ioDispatcher) {
        musicServiceApi.getAlbums(artistId, page, page_size, releaseDate)
            .message
            .body
            .album_list
            .map { it.album }
    }

    override suspend fun getArtistBio(artistName: String): Bio = withContext(ioDispatcher) {
        bioServiceApi.getBiography(artist = artistName)
            .artist
            .bio
    }
}