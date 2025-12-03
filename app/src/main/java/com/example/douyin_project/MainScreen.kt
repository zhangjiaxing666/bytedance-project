package com.example.douyin_project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext

//主屏幕
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var currentTab by remember { mutableStateOf("社区") }
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    // 将 HomeScreen 的所有状态提升到 MainScreen
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }

    val likeManager = rememberLikeManager()
    var likedPosts by remember { mutableStateOf(likeManager.getLikedPosts()) }

    val coroutineScope = rememberCoroutineScope()

    // 初始加载数据
    LaunchedEffect(Unit) {
        if (posts.isEmpty()) {
            loadInitialData(
                onLoading = { isLoading = true },
                onSuccess = {
                    posts = it
                    isLoading = false
                    hasError = false
                },
                onError = {
                    isLoading = false
                    hasError = true
                }
            )
        }
    }

    val showBottomNav by derivedStateOf {
        selectedPost == null
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentRoute = currentScreen.route,
                    onItemSelected = { navItem ->
                        currentScreen = navItem
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                is BottomNavItem.Home -> {
                    if (selectedPost != null) {
                        PostDetailScreen(
                            post = selectedPost!!,
                            onBackClick = { selectedPost = null },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        HomeScreen(
                            currentTab = currentTab,
                            onTabSelected = { tab -> currentTab = tab },
                            onPostClick = { post -> selectedPost = post },
                            posts = posts,
                            isLoading = isLoading,
                            isRefreshing = isRefreshing,
                            isLoadingMore = isLoadingMore,
                            hasError = hasError,
                            likedPosts = likedPosts,
                            onRefresh = {
                                isRefreshing = true
                                coroutineScope.launch {
                                    delay(1500)
                                    posts = generateMockPosts().shuffled()
                                    isRefreshing = false
                                    currentPage = 1
                                }
                            },
                            onLoadMore = {
                                if (!isLoadingMore && !hasError && posts.isNotEmpty()) {
                                    isLoadingMore = true
                                    coroutineScope.launch {
                                        delay(1000)
                                        val newPosts = generateMockPosts().shuffled()
                                        posts = posts + newPosts
                                        currentPage++
                                        isLoadingMore = false
                                    }
                                }
                            },
                            onLikeClick = { postId ->
                                likeManager.toggleLike(postId)
                                likedPosts = likeManager.getLikedPosts()
                            },
                            onRetry = {
                                hasError = false
                                isLoading = true
                                coroutineScope.launch {
                                    loadInitialData(
                                        onLoading = { /* 不需要设置，因为已经设置了 */ },
                                        onSuccess = {
                                            posts = it
                                            isLoading = false
                                        },
                                        onError = {
                                            isLoading = false
                                            hasError = true
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is BottomNavItem.Profile -> {
                    ProfileScreen(modifier = Modifier.fillMaxSize())
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentScreen.title}页面\n(该页面暂不可点击)",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
@Composable
fun HomeScreen(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    // 所有状态作为参数传入
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    hasError: Boolean,
    likedPosts: Set<String>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onLikeClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    val listState = rememberLazyStaggeredGridState()

    //新增预加载主逻辑
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val preloadRange = listState.firstVisibleItemIndex..(listState.firstVisibleItemIndex + 8)

        withContext(Dispatchers.IO) {
            preloadRange.forEach { index ->
                if (index in posts.indices) {
                    val post = posts[index]
                    if (post.imageResList.isNotEmpty()) {
                        try {
                            ImageLoaderManager.preloadImage(
                                context = context,
                                data = post.imageResList[0]
                            )
                        } catch (e: Exception) {
                            // 忽略异常
                        }
                    }
                }
            }
        }
    }

    // 监听滚动加载更多
    LaunchedEffect(listState, posts.size, isLoadingMore, hasError) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleCount = layoutInfo.visibleItemsInfo.size
            val firstVisible = listState.firstVisibleItemIndex

            firstVisible + visibleCount >= posts.size - 3 // 提前触发
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !isLoadingMore && !hasError) {
                    onLoadMore()
                }
            }
    }

    Column(modifier = modifier) {
        HomeTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                hasError && posts.isEmpty() -> {
                    EmptyState(
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                isLoading && posts.isEmpty() -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                    ) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF5F5F5)),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalItemSpacing = 4.dp,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            itemsIndexed(posts) { index, post ->
                                // 判断是否需要预加载
                                val shouldPreload by remember(index, posts.size) {
                                    derivedStateOf {
                                        // 预加载策略：当前项的后4项
                                        index in (listState.firstVisibleItemIndex + 1)..(listState.firstVisibleItemIndex + 4)
                                    }
                                }
                                val isLiked = likedPosts.contains(post.id)

                                PostCard(
                                    post = post,
                                    isLiked = isLiked,
                                    onLikeClick = onLikeClick,
                                    onPostClick = onPostClick,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (isLoadingMore) {
                                item {
                                    LoadMoreState()
                                }
                            }
                        }
                        PullRefreshIndicator(
                            refreshing = isRefreshing,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier)
{
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //头像部分
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),  // 裁剪为圆形
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.begin2),  // 你的头像图片
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),  // 图片也裁剪为圆形
                    contentScale = ContentScale.Crop  // 裁剪填充
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            //用户名
            Text(
                text = "用户名",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            //签名
            Text(
                text = "像风一样的男子",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

//数据加载函数
//suspend fun -> 可以在不阻塞线程的情况下暂停执行，稍后恢复
private suspend fun loadInitialData(
    onLoading: () -> Unit,
    onSuccess: (List<Post>) ->Unit,
    onError: () -> Unit
) {
    onLoading()
    delay(2000) //用于模拟网络请求

    //模拟随机实现的失败 0% 概率失败
    if(kotlin.random.Random.nextFloat() > 0f) {
        onSuccess(generateMockPosts().shuffled())
    } else {
        onError()
    }
}

private suspend fun loadMoreData(
    currentPage: Int,
    onLoading: () -> Unit,
    onSuccess: (List<Post>) -> Unit,
    onError: () -> Unit
) {
    onLoading()
    delay(1000)

    //模拟加载更多数据
    val morePosts = generateMockPosts().shuffled().take(4)
        .map { it.copy(id = "${it.id}_page_$currentPage") } //it.id = "1"，currentPage = 2 -> 1_page_2

    onSuccess(morePosts)
}

