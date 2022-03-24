package com.romazelenin.musicchart.usecase

import com.romazelenin.musicchart.data.ArtistsRepository
import javax.inject.Inject

class AddFavouriteArtistUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {

    suspend operator fun invoke(artistId: Long) {
            artistsRepository.addFavouriteArtist(artistId)
    }
}