package com.jetpackcomposemvi.di

import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.usecase.GetMovieDetailsUseCase
import com.jetpackcomposemvi.domain.usecase.GetMovieListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UIModule {
    @Provides
    fun provideGetMovieListUseCase(movieRepository: MovieRepository): GetMovieListUseCase{
        return GetMovieListUseCase(movieRepository)
    }

    @Provides
    fun provideGetMovieDetailsUseCase(movieRepository: MovieRepository): GetMovieDetailsUseCase{
        return GetMovieDetailsUseCase(movieRepository)
    }
}