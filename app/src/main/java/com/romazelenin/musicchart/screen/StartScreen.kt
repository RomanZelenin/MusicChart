package com.romazelenin.musicchart.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.romazelenin.musicchart.MainViewModel
import com.romazelenin.musicchart.data.service.Country

private data class CountryItem(
    @DrawableRes val flagIcon: Int,
    @StringRes val countryName: Int,
    @StringRes val abbreviation: Int
)

private val countries = listOf(
    CountryItem(
        com.romazelenin.musicchart.R.drawable.ru,
        com.romazelenin.musicchart.R.string.russia,
        com.romazelenin.musicchart.R.string.ru
    ),
    CountryItem(
        com.romazelenin.musicchart.R.drawable.us,
        com.romazelenin.musicchart.R.string.united_states,
        com.romazelenin.musicchart.R.string.us
    ),
    CountryItem(
        com.romazelenin.musicchart.R.drawable.uk,
        com.romazelenin.musicchart.R.string.united_kingdom,
        com.romazelenin.musicchart.R.string.uk
    )
)

private data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector
)

private val bottomBarItems =
    listOf(
        NavigationItem(Screen.Artists, Icons.Default.List),
        NavigationItem(Screen.Favourite, Icons.Default.Favorite)
    )

@Composable
fun StartScreen(viewModel: MainViewModel) {

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val defaultTitle = stringResource(id = Screen.Artists.resourceId)
    var title by remember { mutableStateOf(defaultTitle) }
    var actionsTopAppBar by remember { mutableStateOf<@Composable RowScope.() -> Unit>({}) }
    var bottomBar by remember { mutableStateOf<@Composable () -> Unit>({}) }

    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 2.dp,
                title = {
                    Text(
                        text = title,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = actionsTopAppBar,
                navigationIcon = currentBackStackEntry?.let {
                    if (currentBackStackEntry!!.destination.route!!.startsWith(Screen.Albums.route)) {
                        {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    } else null
                }
            )
        },
        bottomBar = bottomBar
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Artists.route,
        ) {
            composable(Screen.Artists.route) {
                title = stringResource(id = Screen.Artists.resourceId)
                LaunchedEffect(true) {
                    actionsTopAppBar = {
                        var expanded by remember { mutableStateOf(false) }

                        IconButton(onClick = { expanded = true }) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                text = stringResource(com.romazelenin.musicchart.R.string.country_top_chart),
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp
                            )
                            Divider()
                            countries.forEach { country ->
                                val abbreviation = stringResource(country.abbreviation)
                                DropdownMenuItem(onClick = {
                                    viewModel.setCountry(Country.valueOf(abbreviation))
                                    expanded = false
                                }) {
                                    Icon(
                                        modifier = Modifier.size(width = 30.dp, height = 20.dp),
                                        painter = painterResource(id = country.flagIcon),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        stringResource(country.countryName),
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    }
                    bottomBar =
                        { BottomBar(navController = navController, setTitle = { title = it }) }
                }
                ScreenArtists(navController = navController, viewModel)
            }
            composable(
                "${Screen.Albums.route}/{artistName}/{artistId}",
                arguments = listOf(
                    navArgument("artistId") { type = NavType.LongType },
                    navArgument("artistName") { type = NavType.StringType })
            )
            { navBackStackEntry ->
                title = navBackStackEntry.arguments?.getString("artistName")!!
                LaunchedEffect(true){
                    actionsTopAppBar = {
                        var favIcon by remember { mutableStateOf(Icons.Default.FavoriteBorder) }
                        val favArtists = viewModel.favouriteArtists.collectAsLazyPagingItems()
                        val artistId = navBackStackEntry.arguments?.getLong("artistId")!!

                        if (favArtists.loadState.append is LoadState.NotLoading) {
                            for (i in 0..favArtists.itemCount) {
                                if (i == favArtists.itemCount) {
                                    favIcon = Icons.Default.FavoriteBorder
                                } else if (favArtists[i]!!.artist_id == artistId) {
                                    favIcon = Icons.Default.Favorite
                                    break
                                }
                            }
                        }
                        IconButton(onClick = {
                            if (favIcon == Icons.Default.FavoriteBorder) {
                                viewModel.addFavouriteArtist(artistId)
                            } else {
                                viewModel.deleteFavouriteArtist(artistId)
                            }
                        }) {
                            Icon(imageVector = favIcon, contentDescription = null)
                        }
                    }
                    bottomBar = {}
                }
                ScreenAlbums(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Favourite.route) {
                title = stringResource(id = Screen.Favourite.resourceId)
                LaunchedEffect(true){
                    actionsTopAppBar = {}
                    bottomBar = { BottomBar(navController = navController, setTitle = { title = it }) }
                }
                ScreenFavourite(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun BottomBar(setTitle: @Composable (title: String) -> Unit, navController: NavController) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomBarItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = item.screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any {
                    (it.route == item.screen.route).also { predicate ->
                        if (predicate) {
                            val title = stringResource(id = item.screen.resourceId)
                            setTitle(title)
                        }
                    }
                } == true,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}