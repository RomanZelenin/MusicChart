package com.romazelenin.musicchart.data.service

import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicServiceApi {

    @GET("chart.artists.get")
    suspend fun getTopArtists(
        @Query("page") page: Int,
        @Query("page_size") page_size: Int,
        @Query("country") country: Country
    ): ResponseApiMusic<ArtistsHeader, ArtistsBody>

    @GET("artist.albums.get")
    suspend fun getAlbums(
        @Query("artist_id") artistId: Long,
        @Query("page") page: Int,
        @Query("page_size") page_size: Int,
        @Query("s_release_date") releaseDate: String = "desc"
    ): ResponseApiMusic<ArtistAlbumsHeader, ArtistAlbumsBody>
}

data class ResponseApiMusic<H, B>(
    val message: MessageResponse<H, B>
)

data class MessageResponse<H, B>(
    val header: H,
    val body: B
)

data class ArtistsHeader(val status_code: Int, val execute_time: Double)
data class ArtistsBody(val artist_list: List<ItemArtist>)
data class ItemArtist(val artist: Artist)

data class ArtistAlbumsHeader(val status_code: Int, val execute_time: Double, val available: Int)
data class ArtistAlbumsBody(val album_list: List<ItemAlbum>)
data class ItemAlbum(val album: Album)

enum class Country{
    us, uk, ru
}