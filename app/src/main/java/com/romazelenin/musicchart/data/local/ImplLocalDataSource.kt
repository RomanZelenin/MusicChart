package com.romazelenin.musicchart.data.local

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.romazelenin.musicchart.data.TopArtistsRepository
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.CurrentCountry
import com.romazelenin.musicchart.data.entity.TopArtists
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImplLocalDataSource @Inject constructor(private val topArtistsRepository: TopArtistsRepository) :
    LocalDataSource {

    private val artistDao = topArtistsRepository.getArtistDao()

    override fun getTopArtists(): PagingSource<Int, Artist> {
        return artistDao.getTopArtists()
    }

    override fun getFavouriteArtists(): PagingSource<Int, Artist> {
        return artistDao.getFavouriteArtists()
    }

    override suspend fun deleteAllTopArtist() {
        topArtistsRepository.withTransaction {
            artistDao.deleteAllTopArtist()
            artistDao.refreshSqliteSequence()
        }
    }

    override suspend fun insertArtist(artist: Artist) {
        topArtistsRepository.withTransaction {
            artistDao.insertTop(TopArtists(artist_id = artist.artist_id))
            artistDao.insert(artist)
        }
    }

    override fun getCurrentCountry(): Flow<CurrentCountry> {
        return artistDao.getCurrentCountry()
    }

}