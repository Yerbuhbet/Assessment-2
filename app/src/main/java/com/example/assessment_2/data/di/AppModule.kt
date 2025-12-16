package com.example.assessment_2.data.di

import android.content.Context
import androidx.room.Room
import com.example.assessment_2.data.local.AppDatabase
import com.example.assessment_2.data.remote.AuthApiService
import com.example.assessment_2.data.repository.AuthRepository
import com.example.assessment_2.data.repository.AuthRepositoryImpl
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppModule {
    @Volatile private var dbInstance: AppDatabase? = null
    @Volatile private var retrofitInstance: Retrofit? = null
    @Volatile private var authApiInstance: AuthApiService? = null
    @Volatile private var authRepoInstance: AuthRepository? = null

    fun provideDatabase(context: Context): AppDatabase {
        return dbInstance ?: synchronized(this) {
            dbInstance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_db"
            ).fallbackToDestructiveMigration().build().also { dbInstance = it }
        }
    }

    fun provideRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            val moshi = Moshi.Builder().build()
            Retrofit.Builder()
                .baseUrl("https://reqres.in/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .also { retrofitInstance = it }
        }
    }

    fun provideAuthApi(): AuthApiService {
        return authApiInstance ?: synchronized(this) {
            provideRetrofit().create(AuthApiService::class.java).also { authApiInstance = it }
        }
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        return authRepoInstance ?: synchronized(this) {
            val db = provideDatabase(context)
            val api = provideAuthApi()
            AuthRepositoryImpl(api, db.userDao()).also { authRepoInstance = it }
        }
    }
}
