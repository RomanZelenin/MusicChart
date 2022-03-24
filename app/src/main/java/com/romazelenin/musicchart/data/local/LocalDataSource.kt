package com.romazelenin.musicchart.data.local

import androidx.paging.PagingSource
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.CurrentCountry
import kotlinx.coroutines.flow.Flow


interface LocalDataSource {

    fun getTopArtists(): PagingSource<Int, Artist>

    fun getFavouriteArtists(): PagingSource<Int, Artist>

    suspend fun addFavouriteArtist(artistId:Long)

    suspend fun deleteFavouriteArtist(artistId: Long)

    suspend fun deleteAllTopArtist()

    suspend fun insertArtist(artist: Artist)

    fun getCurrentCountry(): Flow<CurrentCountry>

}