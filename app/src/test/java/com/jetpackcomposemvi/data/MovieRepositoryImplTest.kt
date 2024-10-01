package com.jetpackcomposemvi.data

import com.jetpackcomposemvi.data.api.MovieService
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MovieRepositoryImplTest {

    private val movieService: MovieService = mock()

    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(movieService)
    }

    @Test
    fun `when fetch TopRated Movies returns empty list`() = runTest {
        whenever(movieService.topRated()).thenReturn(Response.success(MovieListResponseDTO(emptyList())))

        launch {
            val result = repository.fetchTopRatedMovies()
            assertEquals(MovieListResponse.Success(MovieListResponseDTO(emptyList())), result)
        }
    }

    @Test
    fun `when fetch TopRated Movies returns list of items`() = runTest {

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
        whenever(movieService.topRated()).thenReturn(Response.success(movieListResponseDTO))

        launch {
            val result = repository.fetchTopRatedMovies()
            assertEquals(MovieListResponse.Success(movieListResponseDTO), result)
        }
    }

    @Test
    fun `fetch movie details for given id returns value`() = runTest {
        val movieDto = MovieDetailDTO(
            id = 1L,
            title = "Movie 1",
            overview = "Movie 1 overview",
            posterPath = "movie.jpg"
        )

        val movieId = 123L
        whenever(movieService.movieDetails(movieId)).thenReturn(Response.success(movieDto))

        launch {
            val result = repository.fetchMovieDetails(movieId)
            assertEquals(MovieDetailResponse.Success(movieDto), result)
        }
    }
}