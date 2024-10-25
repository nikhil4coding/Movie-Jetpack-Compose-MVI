package com.jetpackcomposemvi.di

import android.content.Context
import com.google.gson.Gson
import com.jetpackcomposemvi.data.MovieRepositoryImpl
import com.jetpackcomposemvi.data.api.MovieService
import com.jetpackcomposemvi.data.cache.MovieSharedPref
import com.jetpackcomposemvi.domain.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideMovieSharedPref(@ApplicationContext context: Context): MovieSharedPref{
        return MovieSharedPref(context)
    }
    @Provides
    @Singleton
    fun provideMovieRepository(movieService: MovieService, movieSharedPref: MovieSharedPref): MovieRepository {
        return MovieRepositoryImpl(movieService, movieSharedPref)
    }

    @Provides
    @Singleton
    fun provideMovieService(client: OkHttpClient): MovieService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(client)
            .build()
            .create(MovieService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(createAuthInterceptor())
            .addInterceptor(logInterceptor)
            .build()
    }

    private fun createAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization",
                        "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkZjc1NmNmNjQwNjNmY2U2Y2I0YjRmMjI3YmI2MDA3OSIsIm5iZiI6MTcyOTc2NTAyNy44NDQyODQsInN1YiI6IjY2ZTNmYjBlMDAwMDAwMDAwMGI5ODQzOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.jWwk014StOySlzTYPFhMikhbQKKgyTItyKloTcFXJOI"
                    )
                    .build()
            )
        }
    }
}