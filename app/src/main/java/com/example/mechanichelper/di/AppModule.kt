package com.example.mechanichelper.di

import android.content.Context
import com.example.mechanichelper.auth.AuthApi
import com.example.mechanichelper.data.api.UsersApi
import com.example.mechanichelper.data.preferences.PreferencesManager
import com.example.mechanichelper.data.repository.PhotoRepositoryImpl
import com.example.mechanichelper.data.repository.RecommendationsRepositoryImpl
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.RecommendationsRepository
import com.example.mechanichelper.presentation.viewmodel.UsersViewModel
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
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)


    @Provides
    @Singleton
    fun providePartsViewModel(
        usersApi: UsersApi,
        preferencesManager: PreferencesManager
    ): UsersViewModel {
        return UsersViewModel(usersApi, preferencesManager)
    }

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
    fun providePartsApi(retrofit: Retrofit): UsersApi = retrofit.create(UsersApi::class.java)
}