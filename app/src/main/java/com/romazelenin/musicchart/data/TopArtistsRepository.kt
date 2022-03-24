package com.romazelenin.musicchart.data

import androidx.paging.PagingSource
import androidx.room.*
import com.romazelenin.musicchart.data.entity.Artist
import com.romazelenin.musicchart.data.entity.CurrentCountry
import com.romazelenin.musicchart.data.entity.Favourite
import com.romazelenin.musicchart.data.entity.TopArtists
import kotlinx.coroutines.flow.Flow

@Database(entities = [Artist::class, TopArtists::class, CurrentCountry::class, Favourite::class], version = 1)
abstract class TopArtistsRepository : RoomDatabase() {

    abstract fun getArtistDao(): ArtistDao
}

@Dao
interface ArtistDao {
    @Query(
        "Select TopArtists.place, Artist.artist_id,Artist.artist_name,Artist.artist_rating " +
                "From TopArtists " +
                "INNER JOIN Artist " +
                "ON Artist.artist_id == TopArtists.artist_id " +
                "ORDER BY TopArtists.place ASC"
    )
    fun getTopArtists(): PagingSource<Int, Artist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: Artist)

    @Insert
    suspend fun insertTop(artist: TopArtists)

    @Query("DELETE FROM TopArtists")
    suspend fun deleteAllTopArtist()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name LIKE 'TopArtists'")
    suspend fun refreshSqliteSequence()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrentCountry(country: CurrentCountry)

    @Query("Select * From CurrentCountry")
    fun getCurrentCountry(): Flow<CurrentCountry>

    @Query(
        "SELECT Artist.artist_id,Artist.artist_name,Artist.artist_rating " +
                "FROM Favourite " +
                "INNER JOIN Artist " +
                "ON Artist.artist_id == Favourite.artist_id "
    )
    fun getFavouriteArtists(): PagingSource<Int, Artist>

    @Query("DELETE FROM Favourite WHERE artist_id == :artist_id")
    suspend fun deleteFavouriteArtist(artist_id: Long)

    @Insert
    suspend fun insertFavouriteArtist(favourite: Favourite)
}