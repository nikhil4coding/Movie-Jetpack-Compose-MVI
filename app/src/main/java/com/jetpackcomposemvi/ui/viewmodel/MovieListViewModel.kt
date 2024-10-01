package com.jetpackcomposemvi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposemvi.intent.UiAction
import com.jetpackcomposemvi.domain.usecase.GetMovieListUseCase
import com.jetpackcomposemvi.domain.usecase.MovieListResult
import com.movies.ui.model.MovieDetailUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase
) : ViewModel() {

    private val movieListViewStateEmitter = MutableLiveData<MovieListViewState>()
    val movieListViewState: LiveData<MovieListViewState> = movieListViewStateEmitter

    private val movieExceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, exception ->
        Timber.d("Error: " + exception.message)
    }

    fun performActions(action: UiAction) {
        when (action) {
            UiAction.FetchTopRatedMovies -> {
                viewModelScope.launch(movieExceptionHandler) {
                    withContext(Dispatchers.IO) {
                        when (val results = getMovieListUseCase.getTopRatedMovieList()) {
                            is MovieListResult.Error -> movieListViewStateEmitter.postValue(MovieListViewState.Error(results.errorCode))
                            is MovieListResult.Success -> {
                                val movieList = results.movieDetail.map {
                                    MovieDetailUI(
                                        id = it.id,
                                        title = it.title,
                                        overview = it.overview,
                                        posterPath = it.posterPath
                                    )
                                }
                                movieListViewStateEmitter.postValue(MovieListViewState.MovieList(movieList))
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    // get movie list
    fun getMovieList() {
        movieListViewStateEmitter.postValue(MovieListViewState.Loading)
        viewModelScope.launch(movieExceptionHandler) {
            withContext(Dispatchers.IO) {
                when (val results = getMovieListUseCase.getTopRatedMovieList()) {
                    is MovieListResult.Error -> movieListViewStateEmitter.postValue(MovieListViewState.Error(results.errorCode))
                    is MovieListResult.Success -> {
                        val movieList = results.movieDetail.map {
                            MovieDetailUI(
                                id = it.id,
                                title = it.title,
                                overview = it.overview,
                                posterPath = it.posterPath
                            )
                        }
                        movieListViewStateEmitter.postValue(MovieListViewState.MovieList(movieList))
                    }
                }
            }
        }
    }

    sealed interface MovieListViewState {
        data object Loading : MovieListViewState
        data class MovieList(val data: List<MovieDetailUI>, val isLoading: Boolean = false) : MovieListViewState
        data class Error(val errorCode: String) : MovieListViewState
    }
}