package com.jetpackcomposemvi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposemvi.domain.usecase.ErrorResult
import com.jetpackcomposemvi.domain.usecase.GetMovieDetailsUseCase
import com.jetpackcomposemvi.domain.usecase.MovieDetailResult
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
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
) : ViewModel() {

    private val movieDetailViewStateEmitter = MutableStateFlow<MovieDetailViewState>(MovieDetailViewState.Loading)
    val movieDetailViewState = movieDetailViewStateEmitter.asStateFlow()

    private val movieExceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, exception ->
        Timber.d("Error: " + exception.message)
    }

    fun performActions(action: UiAction) {
        when (action) {
            is UiAction.FetchMovieDetails -> {
                viewModelScope.launch(movieExceptionHandler) {
                    withContext(Dispatchers.IO) {
                        when (val result = getMovieDetailsUseCase.getMovieDetails(action.movieId)) {
                            is MovieDetailResult.Error -> movieDetailViewStateEmitter.value =
                                when (result.errorCode) {
                                    ErrorResult.NULL_RESPONSE -> MovieDetailViewState.ErrorUi.EmptyList
                                    ErrorResult.RESPONSE_FAILURE -> MovieDetailViewState.ErrorUi.Failure
                                }

                            is MovieDetailResult.Success -> {
                                val movieDetailUI = MovieDetailUI(
                                    id = result.movieDetail.id,
                                    title = result.movieDetail.title,
                                    overview = result.movieDetail.overview,
                                    posterPath = result.movieDetail.posterPath
                                )
                                movieDetailViewStateEmitter.value = MovieDetailViewState.Movie(data = movieDetailUI)
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
        sealed interface ErrorUi : MovieDetailViewState {
            data object EmptyList : ErrorUi
            data object Failure : ErrorUi
        }
    }
}