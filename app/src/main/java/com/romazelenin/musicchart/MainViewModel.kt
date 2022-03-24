package com.romazelenin.musicchart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.romazelenin.musicchart.data.service.AlbumCoverServiceApi
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase,
    private val getFavouriteArtistsUseCase: GetFavouriteArtistsUseCase,
    private val addFavouriteArtistUseCase: AddFavouriteArtistUseCase,
    private val deleteFavouriteArtistUseCase: DeleteFavouriteArtistUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getArtistBiographyUseCase: GetArtistBiographyUseCase,
    private val getCurrentCountryUseCase: GetCurrentCountryUseCase,
    private val setCurrentCountryUseCase: SetCurrentCountryUseCase
) :
    ViewModel() {


    val currentCountry = getCurrentCountryUseCase().distinctUntilChanged()
    fun setCountry(country: Country) {
        viewModelScope.launch {setCurrentCountryUseCase(country) }
    }

    val artists = getTopArtistsUseCase().cachedIn(viewModelScope)

    val favouriteArtists = getFavouriteArtistsUseCase().cachedIn(viewModelScope)

    fun addFavouriteArtist(artistId: Long) {
        viewModelScope.launch { addFavouriteArtistUseCase(artistId) }
    }

    fun deleteFavouriteArtist(artistId: Long) {
        viewModelScope.launch { deleteFavouriteArtistUseCase(artistId) }
    }

    fun getAlbums(artistId: Long) =
        getAlbumsUseCase(artistId).cachedIn(viewModelScope)

    fun getArtistBio(artistName: String) =
        getArtistBiographyUseCase(artistName)

}