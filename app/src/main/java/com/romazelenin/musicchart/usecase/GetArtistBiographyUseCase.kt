package com.romazelenin.musicchart.usecase

import com.romazelenin.musicchart.data.ArtistsRepository
import com.romazelenin.musicchart.data.entity.Bio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistBiographyUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    operator fun invoke(artistName: String): Flow<Bio> {
        return artistsRepository.getArtistBio(artistName)
    }
}