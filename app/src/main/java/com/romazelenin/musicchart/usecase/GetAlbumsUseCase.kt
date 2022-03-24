package com.romazelenin.musicchart.usecase

import androidx.paging.PagingData
import com.romazelenin.musicchart.data.ArtistsRepository
import com.romazelenin.musicchart.data.entity.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    operator fun invoke(artistId: Long): Flow<PagingData<Album>> {
        return artistsRepository.getAlbums(artistId)
    }
}