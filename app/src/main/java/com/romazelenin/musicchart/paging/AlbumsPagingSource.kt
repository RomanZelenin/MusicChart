package com.romazelenin.musicchart.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException

class AlbumsPagingSource @AssistedInject constructor(
    private val apiMusicService: RemoteDataSource,
    @Assisted private val artistId: Long
) :
    PagingSource<Int, Album>() {
    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        return try {
            val nextPageNumber = params.key ?: 1
            val albums = apiMusicService.getAlbums(artistId, nextPageNumber, params.loadSize)

            LoadResult.Page(
                data = albums,
                prevKey = params.key,
                nextKey = if (albums.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }catch (e: HttpException) {
            LoadResult.Error(e)
        }

    }
}

@AssistedFactory
interface AlbumsPagingSourceFactory {
    fun createAlbumsPagingSource(artistId: Long): AlbumsPagingSource
}