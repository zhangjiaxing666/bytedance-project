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

//主屏幕
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var currentTab by remember { mutableStateOf("社区") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentScreen.route,
                onItemSelected = { navItem ->
                    currentScreen = navItem
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        // 不使用 paddingValues，让内容从屏幕顶部开始
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                is BottomNavItem.Home -> {
                    HomeScreen(
                        currentTab = currentTab,
                        onTabSelected = { tab -> currentTab = tab },
                        modifier = Modifier.fillMaxSize()
                    )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    //添加详情页状态
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    //用于状态管理
    var posts by remember { mutableStateOf(emptyList<Post>()) } // 帖子列表
    var isLoading by remember { mutableStateOf(true) } // 初始加载状态
    var isRefreshing by remember { mutableStateOf(false) } // 下拉刷新状态
    var isLoadingMore by remember { mutableStateOf(false) } // 加载更多状态
    var hasError by remember { mutableStateOf(false) } // 错误状态
    var currentPage by remember { mutableStateOf(1) } // 当前页码

    //这是用于点赞管理
    val likeManager = rememberLikeManager()
    var likedPosts by remember { mutableStateOf(likeManager.getLikedPosts()) }

    // 获取协程作用域
    val coroutineScope = rememberCoroutineScope()

    //使用PullRefresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                delay(1500) // 模拟网络请求
                posts = generateMockPosts().shuffled()
                isRefreshing = false
                currentPage = 1
            }
        }
    )

    //初始加载数据
    LaunchedEffect(Unit) {
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

    //<!-- 很重要的步骤 -->
    //监听列表滚动，实现更多的加载
    val listState = rememberLazyStaggeredGridState()
    // 使用更轻量的监听策略
    LaunchedEffect(listState, posts.size, isLoadingMore, hasError) {
        snapshotFlow {
            // 只监听第一个可见项索引，计算量最小
            listState.firstVisibleItemIndex
        }
            .map { firstVisible ->
                // 计算是否接近底部
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size
                firstVisible + visibleCount >= posts.size - 5
            }
            .distinctUntilChanged() // 只有 true/false 状态变化时才处理
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !isLoadingMore && !hasError && posts.isNotEmpty()) {
                    coroutineScope.launch {
                        isLoadingMore = true
                        try {
                            val newPosts = generateMockPosts().shuffled() // 直接返回数据
                            posts = posts + newPosts
                            currentPage++
                        } catch (e: Exception) {
                            // 错误处理
                        } finally {
                            isLoadingMore = false
                        }
                    }
                }
            }
    }
    //添加点击就可以展示详情的页面
    if (selectedPost != null) {
        //展示详情页面
        PostDetailScreen(
            post = selectedPost!!,
            onBackClick = { selectedPost = null },
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Column(modifier = modifier) {
            HomeTabs(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.fillMaxWidth()
            )
            //这里是内容区域
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    hasError && posts.isEmpty() -> {
                        //首次刷新失败
                        EmptyState(
                            onRetry = {
                                hasError = false
                                isLoading = true
                                //重新加载
                                coroutineScope.launch {
                                    loadInitialData(
                                        onLoading = { /*todo*/ },
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

                    isLoading && posts.isEmpty() -> {
                        LoadingState(modifier = Modifier.fillMaxSize())
                    }

                    else -> {
                        //下拉刷新和瀑布流
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
                                    val isLiked = likedPosts.contains(post.id)

                                    PostCard(
                                        post = post,
                                        isLiked = isLiked,
                                        onLikeClick = { postId ->
                                            likeManager.toggleLike(postId)
                                            likedPosts = likeManager.getLikedPosts()
                                        },
                                        onPostClick = { clickdPost ->
                                            //处理帖子点击
                                            selectedPost = clickdPost
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (isLoadingMore) {
                                    item {
                                        LoadMoreState()
                                    }
                                }
                            }
                            //下拉刷新指示器
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

