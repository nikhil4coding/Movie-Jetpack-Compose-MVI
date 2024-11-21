package com.jetpackcomposemvi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetpackcomposemvi.intent.UiAction
import com.jetpackcomposemvi.ui.viewmodel.MovieDetailsViewModel
import com.jetpackcomposemvi.ui.viewmodel.MovieListViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val detailedViewModel: MovieDetailsViewModel by viewModels()
    private val movieListViewModel: MovieListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SetupView()
            }
        }
    }

    @Composable
    fun SetupView() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "movie_list"
        ) {
            composable("movie_list") {
                MovieListView(
                    viewStateFlow = movieListViewModel.viewState,
                    onMovieClicked = {
                        navController.navigate("movie_detail/${it}")
                    }
                ) {
                    movieListViewModel.performActions(UiAction.FetchTopRatedMovies)
                }
            }

            composable("movie_detail/{movieId}") {
                val movieId = it.arguments?.getString("movieId")
                movieId?.let {
                    MovieDetailsView(
                        viewStateFlow = detailedViewModel.movieDetailViewState
                    ) {
                        navController.popBackStack()
                    }
                    LaunchedEffect(movieId) {
                        detailedViewModel.performActions(UiAction.FetchMovieDetails(movieId))
                    }
                }
            }
        }
    }
}
