package com.jetpackcomposemvi.usecase

import com.jetpackcomposemvi.data.MovieDetailDTO
import com.jetpackcomposemvi.data.MovieListResponse
import com.jetpackcomposemvi.data.MovieListResponseDTO
import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.model.MovieDetail
import com.jetpackcomposemvi.domain.usecase.GetMovieListUseCase
import com.jetpackcomposemvi.domain.usecase.MovieListResult
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetMovieListUseCaseTest {

    private val repository: MovieRepository = mock()
    private lateinit var useCase: GetMovieListUseCase

    @Before
    fun setUp() {
        useCase = GetMovieListUseCase(repository)
    }

    @Test
    fun `get movie list returns error return error`() = runTest {
        whenever(repository.fetchTopRatedMovies(1)).thenReturn(MovieListResponse.Error("error"))

        val result = useCase.getTopRatedMovieList(1)
        assertEquals(MovieListResult.Error("error"), result)
    }

    @Test
    fun `get movie list returns empty list return list`() = runTest {
        whenever(repository.fetchTopRatedMovies(1)).thenReturn(MovieListResponse.Success(MovieListResponseDTO(emptyList())))

        val result = useCase.getTopRatedMovieList(1)
        assertEquals(MovieListResult.Success(emptyList()), result)
    }

    @Test
    fun `get movie list return list`() = runTest {
        val movieListResponseDTO = MovieListResponseDTO(
            listOf(
                MovieDetailDTO(
                    id = 1L,
                    title = "Movie 1",
                    overview = "Movie 1 overview",
                    posterPath = "movie.jpg"
                )
            )
        )
        val expected = listOf(
            MovieDetail(
                id = 1L,
                title = "Movie 1",
                overview = "Movie 1 overview",
                posterPath = "movie.jpg"
            )
        )
        whenever(repository.fetchTopRatedMovies(1)).thenReturn(MovieListResponse.Success(movieListResponseDTO))

        val result = useCase.getTopRatedMovieList(1)
        assertEquals(MovieListResult.Success(expected), result)
    }
}