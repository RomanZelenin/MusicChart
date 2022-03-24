package com.romazelenin.musicchart.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.romazelenin.musicchart.MainViewModel

@Composable
fun ScreenFavourite(
    navController: NavController,
    viewModel: MainViewModel,
) {

    val favouriteArtists = viewModel.favouriteArtists.collectAsLazyPagingItems()

    ListArtists(
        navController = navController,
        artists = favouriteArtists
    )
}