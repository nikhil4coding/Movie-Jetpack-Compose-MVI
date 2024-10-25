package com.jetpackcomposemvi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposemvi.domain.usecase.ErrorResult
import com.jetpackcomposemvi.domain.usecase.GetMovieListUseCase
import com.jetpackcomposemvi.domain.usecase.MovieListResult
import com.jetpackcomposemvi.intent.UiAction
import com.movies.ui.model.MovieDetailUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase
) : ViewModel() {

    private val movieListViewStateEmitter = MutableStateFlow<MovieListViewState>(MovieListViewState.Loading)
    val viewState = movieListViewStateEmitter.asStateFlow()

    private val movieExceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, exception ->
        Timber.d("Error: " + exception.message)
    }

    private var currentPage = 1
    private var completeMovieList = listOf<MovieDetailUI>()

    init {
        movieListViewStateEmitter.value = MovieListViewState.Loading
        performActions(UiAction.FetchTopRatedMovies)
    }

    fun performActions(action: UiAction) {
        when (action) {
            is UiAction.FetchTopRatedMovies -> {
                viewModelScope.launch(movieExceptionHandler) {
                    withContext(Dispatchers.IO) {
                        when (val results = getMovieListUseCase.getTopRatedMovieList(currentPage)) {
                            is MovieListResult.Error -> movieListViewStateEmitter.value =
                                when (results.errorCode) {
                                    ErrorResult.NULL_RESPONSE -> MovieListViewState.ErrorUi.EmptyDetails
                                    ErrorResult.RESPONSE_FAILURE -> MovieListViewState.ErrorUi.Failure
                                }

                            is MovieListResult.Success -> {
                                val movieList = results.movieDetail.map {
                                    MovieDetailUI(
                                        id = it.id,
                                        title = it.title,
                                        overview = it.overview,
                                        posterPath = it.posterPath
                                    )
                                }
                                currentPage++
                                val finalList = completeMovieList + movieList
                                completeMovieList = finalList
                                movieListViewStateEmitter.value = MovieListViewState.MovieList(finalList)
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    sealed interface MovieListViewState {
        data object Loading : MovieListViewState
        data class MovieList(val data: List<MovieDetailUI>, val isLoading: Boolean = false) : MovieListViewState
        sealed interface ErrorUi : MovieListViewState {
            data object EmptyDetails : ErrorUi
            data object Failure : ErrorUi
        }
    }
}