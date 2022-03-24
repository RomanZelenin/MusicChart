package com.romazelenin.musicchart.usecase

import com.romazelenin.musicchart.data.ArtistsRepository
import com.romazelenin.musicchart.data.service.Country
import javax.inject.Inject

class SetCurrentCountryUseCase @Inject constructor(private val artistsRepository: ArtistsRepository) {

    suspend operator fun invoke(country: Country) = artistsRepository.setCurrentCountry(country)

}