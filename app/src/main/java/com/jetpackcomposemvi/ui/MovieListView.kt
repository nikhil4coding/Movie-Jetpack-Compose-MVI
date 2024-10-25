package com.jetpackcomposemvi.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jetpackcomposemvi.R
import com.jetpackcomposemvi.ui.viewmodel.MovieListViewModel
import com.movies.ui.model.MovieDetailUI
import kotlinx.coroutines.flow.StateFlow

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun MovieListView(
    viewStateFlow: StateFlow<MovieListViewModel.MovieListViewState>,
    onMovieClicked: (String) -> Unit,
    loadMore: () -> Unit
) {
    val viewState by viewStateFlow.collectAsStateWithLifecycle()

    var isLoading: Boolean by rememberSaveable { mutableStateOf(false) }
    var movieList: List<MovieDetailUI> by rememberSaveable { mutableStateOf(emptyList()) }
    when (viewState) {
        is MovieListViewModel.MovieListViewState.ErrorUi -> {
            val errorStr = when (viewState as MovieListViewModel.MovieListViewState.ErrorUi) {
                MovieListViewModel.MovieListViewState.ErrorUi.EmptyDetails -> stringResource(R.string.no_data_found)
                MovieListViewModel.MovieListViewState.ErrorUi.Failure -> stringResource(R.string.call_to_retrieve_data_failed)
            }
            Toast.makeText(
                LocalContext.current,
                stringResource(R.string.error, errorStr),
                Toast.LENGTH_SHORT
            ).show()
        }

        is MovieListViewModel.MovieListViewState.Loading -> isLoading = true
        is MovieListViewModel.MovieListViewState.MovieList -> {
            isLoading = (viewState as MovieListViewModel.MovieListViewState.MovieList).isLoading
            movieList = (viewState as MovieListViewModel.MovieListViewState.MovieList).data
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.top_rated_movies),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(movieList, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                            .clickable {
                                onMovieClicked(item.id.toString())
                            }
                    ) {
                        //Movie image
                        GlideImage(
                            model = stringResource(R.string.https_image_url, item.posterPath),
                            contentDescription = "",
                            modifier = Modifier
                                .width(92.dp)
                                .height(134.dp)
                                .padding(end = 16.dp)
                                .background(color = colorResource(id = R.color.black))
                        )

                        //movie Name and overview
                        Column(modifier = Modifier.fillMaxWidth()) {
                            //Title
                            Text(
                                text = item.title,
                                modifier = Modifier
                                    .wrapContentSize(),
                                fontSize = 22.sp,
                                lineHeight = 28.sp,
                                color = colorResource(id = R.color.black),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            //overview
                            Text(
                                text = item.overview,
                                modifier = Modifier
                                    .wrapContentSize(),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorResource(id = R.color.black),
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                item {
                    loadMore()
                }
            }
        }
    }
}