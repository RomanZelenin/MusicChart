package com.romazelenin.musicchart.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.Bio
import com.romazelenin.musicchart.data.local.LocalDataSource
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.paging.AlbumsPagingSourceFactory
import com.romazelenin.musicchart.paging.TopArtistsMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val albumsPagingSourceFactory: AlbumsPagingSourceFactory,
) {
    private val mediator = TopArtistsMediator(remoteDataSource, localDataSource)

    @OptIn(ExperimentalPagingApi::class)
    fun getTopArtists(): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
            remoteMediator = mediator
        ) { localDataSource.getTopArtists() }
            .flow
    }

    fun getFavouriteArtists(): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
        ) { localDataSource.getFavouriteArtists() }
            .flow
    }

    suspend fun addFavouriteArtist(artistId: Long) {
        localDataSource.addFavouriteArtist(artistId)
    }

    suspend fun deleteFavouriteArtist(artistId: Long) {
        localDataSource.deleteFavouriteArtist(artistId)
    }

    fun getAlbums(artistId: Long): Flow<PagingData<Album>> {
        return Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = true)) {
            albumsPagingSourceFactory.createAlbumsPagingSource(artistId)
        }.flow
    }

    private val _authorBiography = MutableStateFlow<Bio?>(null)

    fun getArtistBio(artistName: String) =
        flow {
            emit(remoteDataSource.getArtistBio(artistName))
        }


    fun getCurrentCountry() = localDataSource.getCurrentCountry()

    suspend fun setCurrentCountry(country: Country) {
        localDataSource.setCurrentCountry(country)
    }
}