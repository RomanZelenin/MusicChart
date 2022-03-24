package com.romazelenin.musicchart.usecase

import com.romazelenin.musicchart.data.ArtistsRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {
    operator fun invoke(artistId: Long)=
         artistsRepository.getAlbums(artistId)
}