package com.romazelenin.musicchart.di

import com.romazelenin.musicchart.data.MusicServiceApi
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

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private val musicServiceBaseUrl = "https://api.musixmatch.com/ws/1.1/"
    private val musicServiceApiKey = "c81e5304512c8e3070a8b104cc69c567"

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
}