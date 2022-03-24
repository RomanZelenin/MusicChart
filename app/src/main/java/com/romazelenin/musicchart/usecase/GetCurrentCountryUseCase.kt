package com.romazelenin.musicchart.usecase

import com.romazelenin.musicchart.data.ArtistsRepository
import javax.inject.Inject

class GetCurrentCountryUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {

    operator fun invoke() = artistsRepository.getCurrentCountry()

}