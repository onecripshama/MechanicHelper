package com.example.mechanichelper.di

import android.content.Context
import com.example.mechanichelper.data.api.PartsApi
import com.example.mechanichelper.data.preferences.PreferencesManager
import com.example.mechanichelper.data.repository.PhotoRepositoryImpl
import com.example.mechanichelper.data.repository.RecommendationsRepositoryImpl
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.RecommendationsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecommendationsRepository(
        @ApplicationContext context: Context
    ): RecommendationsRepository {
        return RecommendationsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(
        repoImpl: PhotoRepositoryImpl
    ): PhotoRepository = repoImpl

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providePartsApi(retrofit: Retrofit): PartsApi = retrofit.create(PartsApi::class.java)
}