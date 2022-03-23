package com.romazelenin.musicchart.screen

import androidx.annotation.StringRes
import com.romazelenin.musicchart.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Artists : Screen("artists", R.string.top_artists)
    object Albums : Screen("albums", R.string.albums)
    object Favourite : Screen("favourite", R.string.favourite)
}