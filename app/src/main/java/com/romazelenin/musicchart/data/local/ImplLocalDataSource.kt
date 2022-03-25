package com.romazelenin.musicchart.data.local

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.romazelenin.musicchart.data.TopArtistsRepository
import com.romazelenin.musicchart.data.entity.*
import com.romazelenin.musicchart.data.service.Country
import com.romazelenin.musicchart.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImplLocalDataSource @Inject constructor(
    private val topArtistsRepository: TopArtistsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    LocalDataSource {

    private val artistDao = topArtistsRepository.getArtistDao()

    override fun getTopArtists(): PagingSource<Int, Artist> {
        return artistDao.getTopArtists()
    }

    override fun getFavouriteArtists(): PagingSource<Int, Artist> {
        return artistDao.getFavouriteArtists()
    }

    override suspend fun addFavouriteArtist(artistId: Long) = withContext(ioDispatcher) {
        artistDao.insertFavouriteArtist(Favourite(artist_id = artistId))
    }

    override suspend fun deleteFavouriteArtist(artistId: Long) = withContext(ioDispatcher) {
        artistDao.deleteFavouriteArtist(artistId)
    }

    override suspend fun deleteAllTopArtist() = withContext(ioDispatcher) {
        topArtistsRepository.withTransaction {
            artistDao.deleteAllTopArtist()
            artistDao.refreshSqliteSequence()
        }
    }

    override suspend fun insertArtist(artist: Artist) = withContext(ioDispatcher) {
        topArtistsRepository.withTransaction {
            artistDao.insertTop(TopArtists(artist_id = artist.artist_id))
            artistDao.insert(artist)
        }
    }

    override fun getCurrentCountry(): Flow<CurrentCountry> {
        return artistDao.getCurrentCountry().flowOn(ioDispatcher)
    }

    override suspend fun setCurrentCountry(country: Country) = withContext(ioDispatcher) {
        artistDao.setCurrentCountry(CurrentCountry(country = country.name))
    }

    override suspend fun getArtistBio(artistName: String): Bio = withContext(ioDispatcher) {
        TODO("Not yet implemented")
    }

}