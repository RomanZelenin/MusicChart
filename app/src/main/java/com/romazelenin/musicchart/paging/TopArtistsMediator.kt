package com.romazelenin.musicchart.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.local.LocalDataSource
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class TopArtistsMediator @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RemoteMediator<Int, Artist>() {


    private var key = 1

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Artist>): MediatorResult {
        return withContext(ioDispatcher){
            try {
                val loadKey = when (loadType) {
                    LoadType.REFRESH -> {
                        key = 1
                        null
                    }
                    LoadType.PREPEND ->
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        ++key
                    }
                }
                val pageNumber = loadKey ?: 1
                val artists = remoteDataSource.getTopArtists(
                    pageNumber,
                    state.config.pageSize,
                    Country.valueOf(localDataSource.getCurrentCountry().first().country)
                )

                if (loadType == LoadType.REFRESH) {
                    localDataSource.deleteAllTopArtist()
                }

                artists.forEach {
                    localDataSource.insertArtist(it)
                }

                MediatorResult.Success(endOfPaginationReached = artists.isEmpty())
            } catch (e: IOException) {
                MediatorResult.Error(e)
            } catch (e: HttpException) {
                MediatorResult.Error(e)
            }
        }
    }
}