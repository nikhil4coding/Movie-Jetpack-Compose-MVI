package com.jetpackcomposemvi.di

import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.usecase.GetMovieDetailsUseCaseImpl
import com.jetpackcomposemvi.domain.usecase.GetMovieListUseCaseImpl
import com.jetpackcomposemvi.ui.usecase.GetMovieDetailsUseCase
import com.jetpackcomposemvi.ui.usecase.GetMovieListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UIModule {
    @Provides
    fun provideGetMovieListUseCase(movieRepository: MovieRepository): GetMovieListUseCase{
        return GetMovieListUseCaseImpl(movieRepository)
    }

    @Provides
    fun provideGetMovieDetailsUseCase(movieRepository: MovieRepository): GetMovieDetailsUseCase{
        return GetMovieDetailsUseCaseImpl(movieRepository)
    }
}