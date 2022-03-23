package com.romazelenin.musicchart.usecase

import androidx.paging.PagingData
import com.romazelenin.musicchart.data.ArtistsRepository
import com.romazelenin.musicchart.data.entity.Artist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteArtistsUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {

    operator fun invoke(): Flow<PagingData<Artist>> {
        return artistsRepository.getFavouriteArtists()
    }
}