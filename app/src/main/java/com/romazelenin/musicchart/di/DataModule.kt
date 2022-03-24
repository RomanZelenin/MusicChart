package com.romazelenin.musicchart.di

import android.content.ContentValues
import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.transaction
import com.romazelenin.musicchart.data.*
import com.romazelenin.musicchart.data.local.ImplLocalDataSource
import com.romazelenin.musicchart.data.local.LocalDataSource
import com.romazelenin.musicchart.data.remote.ImplRemoteDataSource
import com.romazelenin.musicchart.data.remote.RemoteDataSource
import com.romazelenin.musicchart.data.service.ArtistBioServiceApi
import com.romazelenin.musicchart.data.service.MusicServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideArtistRepository(@ApplicationContext context: Context): TopArtistsRepository {
        return Room.databaseBuilder(context, TopArtistsRepository::class.java, "artists.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.transaction {
                        db.insert(
                            "CurrentCountry",
                            OnConflictStrategy.REPLACE,
                            ContentValues().apply { put("country", "us") })
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideLocalDataSource(topArtistsRepository: TopArtistsRepository): LocalDataSource {
        return ImplLocalDataSource(topArtistsRepository)
    }

    @Provides
    fun provideRemoteDataSource(musicServiceApi: MusicServiceApi, artistBioServiceApi: ArtistBioServiceApi): RemoteDataSource {
        return ImplRemoteDataSource(musicServiceApi, artistBioServiceApi)
    }

}