package com.example.mechanichelper.di

import android.content.Context
import com.example.mechanichelper.auth.AuthApi
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.data.api.UsersApi
import com.example.mechanichelper.data.network.AuthInterceptor
import com.example.mechanichelper.data.preferences.PreferencesManager
import com.example.mechanichelper.data.repository.CarRepositoryImpl
import com.example.mechanichelper.data.repository.PhotoRepositoryImpl
import com.example.mechanichelper.data.repository.RecommendationsRepositoryImpl
import com.example.mechanichelper.data.repository.RemindersRepositoryImpl
import com.example.mechanichelper.data.repository.UserPreferencesRepositoryImpl
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.RecommendationsRepository
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.presentation.viewmodel.UsersViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  private const val BACKEND_BASE_URL = "http://10.0.2.2:8080/"

  @Provides
  @Singleton
  fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
      OkHttpClient.Builder().addInterceptor(authInterceptor).build()

  @Provides
  @Singleton
  @Named("backend")
  fun provideBackendRetrofit(okHttpClient: OkHttpClient): Retrofit =
      Retrofit.Builder()
          .baseUrl(BACKEND_BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create())
          .build()

  @Provides
  @Singleton
  fun provideAuthApi(@Named("backend") retrofit: Retrofit): AuthApi =
      retrofit.create(AuthApi::class.java)

  @Provides
  @Singleton
  fun provideMechanicApi(@Named("backend") retrofit: Retrofit): MechanicApi =
      retrofit.create(MechanicApi::class.java)

  @Provides
  @Singleton
  fun providePartsViewModel(
      usersApi: UsersApi,
      preferencesManager: PreferencesManager
  ): UsersViewModel = UsersViewModel(usersApi, preferencesManager)

  @Provides
  @Singleton
  fun provideRecommendationsRepository(
      impl: RecommendationsRepositoryImpl
  ): RecommendationsRepository = impl

  @Provides
  @Singleton
  fun provideRemindersRepository(impl: RemindersRepositoryImpl): RemindersRepository = impl

  @Provides
  @Singleton
  fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
      PreferencesManager(context)

  @Provides
  @Singleton
  fun provideUserPreferencesRepository(
      impl: UserPreferencesRepositoryImpl
  ): UserPreferencesRepository = impl

  @Provides
  @Singleton
  fun providePhotoRepository(repoImpl: PhotoRepositoryImpl): PhotoRepository = repoImpl

  @Provides
  @Singleton
  fun provideCarRepository(impl: CarRepositoryImpl): CarRepository = impl

  @Provides
  @Singleton
  @Named("dummyjson")
  fun provideDummyJsonRetrofit(): Retrofit =
      Retrofit.Builder()
          .baseUrl("https://dummyjson.com/")
          .addConverterFactory(GsonConverterFactory.create())
          .build()

  @Provides
  @Singleton
  fun providePartsApi(@Named("dummyjson") retrofit: Retrofit): UsersApi =
      retrofit.create(UsersApi::class.java)
}
