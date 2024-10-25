package com.jetpackcomposemvi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposemvi.domain.usecase.GetMovieDetailsUseCase
import com.jetpackcomposemvi.domain.usecase.MovieDetailResult
import com.jetpackcomposemvi.intent.UiAction
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
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
) : ViewModel() {

    private val movieDetailViewStateEmitter = MutableLiveData<MovieDetailViewState>()
    val movieDetailViewState: LiveData<MovieDetailViewState> = movieDetailViewStateEmitter

    private val movieExceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, exception ->
        Timber.d("Error: " + exception.message)
    }

    fun performActions(action: UiAction) {
        when (action) {
            is UiAction.FetchMovieDetails -> {
                viewModelScope.launch(movieExceptionHandler) {
                    withContext(Dispatchers.IO) {
                        when (val result = getMovieDetailsUseCase.getMovieDetails(action.movieId)) {
                            is MovieDetailResult.Error -> movieDetailViewStateEmitter.postValue(MovieDetailViewState.Error(result.errorCode))
                            is MovieDetailResult.Success -> {
                                val movieDetailUI = MovieDetailUI(
                                    id = result.movieDetail.id,
                                    title = result.movieDetail.title,
                                    overview = result.movieDetail.overview,
                                    posterPath = result.movieDetail.posterPath
                                )
                                movieDetailViewStateEmitter.postValue(MovieDetailViewState.Movie(data = movieDetailUI))
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    sealed interface MovieDetailViewState {
        data object Loading : MovieDetailViewState
        data class Movie(val data: MovieDetailUI, val isLoading: Boolean = false) : MovieDetailViewState
        data class Error(val errorCode: String) : MovieDetailViewState
    }
}