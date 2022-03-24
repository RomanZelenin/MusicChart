package com.romazelenin.musicchart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.*
import com.romazelenin.musicchart.MainViewModel
import com.romazelenin.musicchart.R
import com.romazelenin.musicchart.data.entity.Album
import com.romazelenin.musicchart.ui.theme.MusicChartTheme
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenAlbums(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val tabs = listOf(
        TabItem.Albums,
        TabItem.Bio,
    )
    val pagerState = rememberPagerState()
    navController.currentBackStackEntry?.arguments?.let {
        Column {
            Tabs(tabs = tabs, pagerState = pagerState)
            if (it.getString("artistName") != null) {
                val data = mapOf(
                    "artistId" to it.getLong("artistId"),
                    "artistName" to it.getString("artistName")!!
                )
                TabsContent(tabs = tabs, pagerState = pagerState, viewModel, data)
            }
        }
    }

}

typealias ComposableFun = @Composable (mainViewModel: MainViewModel, data: Map<String, Any>) -> Unit

sealed class TabItem(var icon: Int? = null, var title: String, var screen: ComposableFun) {
    object Albums : TabItem(title = "Albums", screen = { viewModel, data ->
        val albums by remember { mutableStateOf(viewModel.getAlbums(data["artistId"] as Long)) }
        ListAlbums(albums = albums.collectAsLazyPagingItems())
    })

    object Bio : TabItem(title = "Bio", screen = { viewModel, data ->
        val bio =
            viewModel.getArtistBio(data["artistName"].toString()).collectAsState(initial = null)

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            bio.value?.let {
                Text(text = it.content)
            }
        }
    })
}

@Composable
fun ListAlbums(modifier: Modifier = Modifier, albums: LazyPagingItems<Pair<Album, String?>>) {
    LazyColumn(modifier.fillMaxSize()) {
        itemsIndexed(albums) { index, value ->
            value?.let { album ->
                Row {
                    val urlToAlbumCover = album.second
                    ItemAlbum(album = album.first, urlToAlbumCover = urlToAlbumCover)
                }
            }
        }
        if (albums.loadState.append is LoadState.Loading || albums.loadState.refresh is LoadState.Loading) {
            val album = Album(-1, "", "", mapOf())
            items(10) {
                ItemAlbum(album = album,
                    cover = {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shimmer(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color.LightGray)
                            )
                        }
                    }, content = {
                        AlbumDescription(
                            album = album,
                            modifier = Modifier.shimmer(),
                            shimmerBackgroundColor = Color.LightGray
                        )
                    })
            }
        }
    }
}

@Composable
private fun ItemAlbum(
    modifier: Modifier = Modifier,
    album: Album,
    urlToAlbumCover: String? = null,
    cover: @Composable (url: String?) -> Unit = { Cover(url = urlToAlbumCover) },
    content: @Composable (album: Album) -> Unit = { AlbumDescription(album = album) }
) {
    Card(elevation = 5.dp, modifier = modifier.padding(8.dp)) {
        Row {
            cover(urlToAlbumCover)
            content(album)
        }
    }
}

@Composable
private fun AlbumDescription(
    modifier: Modifier = Modifier,
    album: Album,
    shimmerBackgroundColor: Color = Color.Transparent
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
    ) {
        Text(
            text = album.album_name,
            modifier = Modifier
                .fillMaxWidth()
                .background(shimmerBackgroundColor)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = album.album_release_date,
            modifier = Modifier
                .fillMaxWidth()
                .background(shimmerBackgroundColor)
        )
    }
}

@Composable
private fun Cover(modifier: Modifier = Modifier, url: String?) {
    SubcomposeAsyncImage(
        modifier = modifier.padding(8.dp),
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        loading = {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shimmer(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.LightGray)
                )
            }
        },
        error = {
            Icon(
                modifier = modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_outline_album_24),
                contentDescription = null
            )
        },
        contentDescription = null
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
            )
        }) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun TabsPreview() {
    val tabs = listOf(
        TabItem.Albums,
        TabItem.Bio,
    )
    val pagerState = rememberPagerState()
    MusicChartTheme {
        Tabs(tabs = tabs, pagerState = pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(
    tabs: List<TabItem>,
    pagerState: PagerState,
    mainViewModel: MainViewModel,
    data: Map<String, Any>
) {
    HorizontalPager(state = pagerState, count = tabs.size) { page ->
        tabs[page].screen(mainViewModel, data)
    }
}