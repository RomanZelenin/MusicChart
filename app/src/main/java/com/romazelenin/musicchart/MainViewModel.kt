package com.romazelenin.musicchart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.romazelenin.musicchart.usecase.GetFavouriteArtistsUseCase
import com.romazelenin.musicchart.usecase.GetTopArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase,
    private val getFavouriteArtistsUseCase: GetFavouriteArtistsUseCase
) :
    ViewModel() {

/*    val currentCountry = artistDao.getCurrentCountry().distinctUntilChanged()
    fun setCountry(country: String) {
        viewModelScope.launch { artistDao.updateCountry(CurrentCountry(country = country)) }
    }*/

    val artists = getTopArtistsUseCase().cachedIn(viewModelScope)

    val favouriteArtists = getFavouriteArtistsUseCase().cachedIn(viewModelScope)
}