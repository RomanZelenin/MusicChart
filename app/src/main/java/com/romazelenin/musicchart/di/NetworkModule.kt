package com.romazelenin.musicchart.di


import com.romazelenin.musicchart.data.service.AlbumCoverServiceApi
import com.romazelenin.musicchart.data.service.ArtistBioServiceApi
import com.romazelenin.musicchart.data.service.MusicServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class MusicService

@Qualifier
annotation class BioService

@Qualifier
annotation class CoverService

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private val musicServiceBaseUrl = "https://api.musixmatch.com/ws/1.1/"
    private val musicServiceApiKey = "c81e5304512c8e3070a8b104cc69c567"

    private val authorBioServiceBaseUrl = "https://ws.audioscrobbler.com/"
    private val authorBioServiceApiKey = "f73637a86f62b02edbf1a65dcd7a2377"

    private val coverServiceBaseUrl = "https://open.spotify.com/"

    @Provides
    fun provideRetrofitBuilderWithGsonConvertor(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }

    @MusicService
    @Provides
    fun provideMusicServiceRetrofit(
        @MusicService httpClient: OkHttpClient,
        retrofit: Retrofit.Builder
    ): Retrofit {
        return retrofit
            .baseUrl(musicServiceBaseUrl)
            .client(httpClient)
            .build()
    }

    @MusicService
    @Provides
    fun provideMusicServiceHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val urlWithApiKey = it.request().url.newBuilder()
                    .addQueryParameter("apikey", musicServiceApiKey)
                    .build()
                val request = it.request().newBuilder()
                    .url(urlWithApiKey)
                    .build()
                it.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideMusicServiceApi(@MusicService retrofit: Retrofit): MusicServiceApi {
        return retrofit.create()
    }

    @BioService
    @Provides
    fun provideArtistBioServiceRetrofit(
        retrofit: Retrofit.Builder,
        @BioService httpClient: OkHttpClient
    ): Retrofit {
        return retrofit
            .baseUrl(authorBioServiceBaseUrl)
            .client(httpClient)
            .build()
    }

    @BioService
    @Provides
    fun provideArtistBioServiceHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val urlWithQueryParameter = it.request().url.newBuilder()
                    .setQueryParameter("api_key", authorBioServiceApiKey)
                    .setQueryParameter("format", "json")
                    .build()
                val request = it.request().newBuilder()
                    .url(urlWithQueryParameter)
                    .build()
                it.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideArtistBioServiceApi(@BioService retrofit: Retrofit): ArtistBioServiceApi {
        return retrofit.create()
    }

    @CoverService
    @Provides
    fun provideCoverServiceRetrofit(
        retrofit: Retrofit.Builder,
        @CoverService httpClient: OkHttpClient
    ): Retrofit {
        return retrofit
            .baseUrl(coverServiceBaseUrl)
            .client(httpClient)
            .build()
    }

    @CoverService
    @Provides
    fun provideCoverServiceHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val queryParameter = it.request().url.queryParameter("url")
                val urlWithQueryParameter = it.request().url.newBuilder()
                    .setQueryParameter("url", "spotify:album:$queryParameter")
                    .build()
                val request = it.request().newBuilder()
                    .url(urlWithQueryParameter)
                    .build()
                it.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideApiAlbumCoversService(@CoverService retrofit: Retrofit): AlbumCoverServiceApi {
        return retrofit.create()
    }
}