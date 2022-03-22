package com.romazelenin.musicchart.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.local.LocalDataSource
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import com.romazelenin.musicchart.paging.TopArtistsMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
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

}