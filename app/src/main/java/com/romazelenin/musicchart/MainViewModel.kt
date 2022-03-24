package com.romazelenin.musicchart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.romazelenin.musicchart.data.service.AlbumCoverServiceApi
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
    private val albumCoverService: AlbumCoverServiceApi
) :
    ViewModel() {

    //TODO
/*    val currentCountry = artistDao.getCurrentCountry().distinctUntilChanged()
    fun setCountry(country: String) {
        viewModelScope.launch { artistDao.updateCountry(CurrentCountry(country = country)) }
    }*/

    val artists = getTopArtistsUseCase().cachedIn(viewModelScope)

    val favouriteArtists = getFavouriteArtistsUseCase().cachedIn(viewModelScope)

    fun addFavouriteArtist(artistId: Long) {
        viewModelScope.launch { addFavouriteArtistUseCase(artistId) }
    }

    fun deleteFavouriteArtist(artistId: Long) {
        viewModelScope.launch { deleteFavouriteArtistUseCase(artistId) }
    }

    fun getAlbums(artistId: Long) =
        getAlbumsUseCase(artistId)
            .distinctUntilChanged()
            .map { album ->
                album.map {
                    if (it.external_ids["spotify"]!!.isNotEmpty()) {
                        Pair(
                            it,
                            albumCoverService.getAlbumCover(it.external_ids["spotify"]!![0]).thumbnail_url
                        )
                    } else {
                        Pair(it, null)
                    }
                }
            }
            .cachedIn(viewModelScope)

    fun getArtistBio(artistName: String) =
        getArtistBiographyUseCase(artistName)

}