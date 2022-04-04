package com.romazelenin.musicchart.data

import androidx.paging.*
import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.Bio
import com.romazelenin.musicchart.data.local.LocalDataSource
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import com.romazelenin.musicchart.data.service.AlbumCoverServiceApi
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.di.IoDispatcher
import com.romazelenin.musicchart.paging.AlbumsPagingSourceFactory
import com.romazelenin.musicchart.paging.TopArtistsMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val albumsPagingSourceFactory: AlbumsPagingSourceFactory,
    private val albumCoverService: AlbumCoverServiceApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val mediator = TopArtistsMediator(remoteDataSource, localDataSource, ioDispatcher)

    @OptIn(ExperimentalPagingApi::class)
    fun getTopArtists(): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
            remoteMediator = mediator
        ) { localDataSource.getTopArtists() }
            .flow
            .flowOn(ioDispatcher)
    }

    fun getFavouriteArtists(): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
        ) { localDataSource.getFavouriteArtists() }
            .flow
            .flowOn(ioDispatcher)
    }

    suspend fun addFavouriteArtist(artistId: Long) {
        withContext(ioDispatcher) {
            localDataSource.addFavouriteArtist(artistId)
        }
    }

    suspend fun deleteFavouriteArtist(artistId: Long) {
        withContext(ioDispatcher) {
            localDataSource.deleteFavouriteArtist(artistId)
        }
    }

    fun getAlbums(artistId: Long) =
        Pager(config = PagingConfig(pageSize = 20)) {
            albumsPagingSourceFactory.createAlbumsPagingSource(artistId)
        }.flow
            .distinctUntilChanged()
            .map { album ->
                album.map {
                    if (it.external_ids["spotify"]!!.isNotEmpty()) {
                        Pair<Album, String?>(
                            it,
                            albumCoverService.getAlbumCover(it.external_ids["spotify"]!![0]).thumbnail_url
                        )
                    } else {
                        Pair<Album, String?>(it, null)
                    }
                }
            }.catch {
                //emit(Pair<Album,String?>(it, null))
            }.flowOn(ioDispatcher)


    fun getArtistBio(artistName: String) =
        flow<Bio?> {
            emit(remoteDataSource.getArtistBio(artistName))
        }.catch { emit(null) }
            .flowOn(ioDispatcher)


    fun getCurrentCountry() = localDataSource.getCurrentCountry().flowOn(ioDispatcher)

    suspend fun setCurrentCountry(country: Country) {
        withContext(ioDispatcher) {
            localDataSource.setCurrentCountry(country)
        }
    }
}