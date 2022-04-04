package com.romazelenin.musicchart.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.romazelenin.musicchart.MainViewModel
import com.romazelenin.musicchart.R
import com.romazelenin.musicchart.data.entity.Artist
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ScreenArtists(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val artists = viewModel.artists.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        var isFirstLaunch = false
        viewModel.currentCountry.collectLatest {
            if (isFirstLaunch) {
                artists.refresh()
            }
            isFirstLaunch = true
        }
    }
    ListArtists(
        navController = navController,
        artists = artists
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListArtists(navController: NavController, artists: LazyPagingItems<Artist>) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.padding(bottom = defaultBottomNavigationHeight),
        state = lazyListState
    ) {
        stickyHeader {
            Surface {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(text = "#")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(modifier = Modifier.weight(1f), text = stringResource(R.string.artist))
                    Text(text = stringResource(R.string.rating), textAlign = TextAlign.End)
                }
            }
        }
        itemsIndexed(artists) { index, value ->
            value?.let { artist ->
                Row(modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("albums/${artist.artist_name}/${artist.artist_id}")
                    },
                verticalAlignment = Alignment.Top) {
                    Text(text = "${index+1} ")
                    Text(
                        text = artist.artist_name,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(getColorForRating(artist.artist_rating))
                            .size(50.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${artist.artist_rating}", fontSize = 20.sp)
                    }
                }

            }
        }
        if (artists.loadState.append is LoadState.Loading) {
            item { CircularProgressIndicator(Modifier.padding(16.dp)) }
        }
    }
}

private fun getColorForRating(rating: Int): Color {
   return when (rating) {
        in 70..100 -> Color.Green
        in 50..69 -> Color.Yellow
        in 30..49-> Color.Red
        else-> Color.LightGray
    }
}

private val defaultBottomNavigationHeight = 56.dp