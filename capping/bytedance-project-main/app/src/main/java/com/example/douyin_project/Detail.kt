package com.example.douyin_project

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale

@Composable
fun PostDetailScreen(
    post: Post,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentImageIndex by remember { mutableStateOf(0) }
    val followManager = rememberFollowManager()
    var isFollowing by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    //从本地存储读取关注状态
    LaunchedEffect(
        post.author
    ) {
        isFollowing = followManager.isFollowing(post.author)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        // 状态栏占位空间 - 显示系统状态栏信息
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(Color.Transparent)
        )
        //顶部作者区
        AuthorHeader(
            authorName = post.author,
            avatarRes = post.avatarRes,
            isFollowing = isFollowing,
            onBackClick = onBackClick,
            onFollowClick = {
                isFollowing = !isFollowing
                //保存关注状态到本地
                followManager.setFollowState(post.author, isFollowing)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        //内容区域
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll((rememberScrollState()))
        ) {
            //图片横滑容器
            if(post.imageResList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.imageAspectRatio)
                ) {
                    ImageSwiper(
                        imageResList = post.imageResList,
                        currentIndex = currentImageIndex,
                        onIndexChange = { currentImageIndex = it },
                        modifier = Modifier.fillMaxSize()
                    )

                    // 进度条放在图片内部底部 - 占满宽度
                    if (post.imageResList.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            FullWidthProgressIndicator(
                                total = post.imageResList.size,
                                current = currentImageIndex,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            //标题和内容区域
            PostContent(
                post = post,
                modifier = Modifier.padding(16.dp)
            )
        }
        //底部交互区
        BottomInteractionBar(
            isLiked = isLiked,
            onLikeClick = { isLiked = !isLiked },
            onCommentClick = {/*todo*/},
            onCollectionClick = {/*todo*/},
            onSharedClick = {/*todo*/},
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun FullWidthProgressIndicator(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp), // 点之间的间距
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f) // 关键：每个点等分宽度
                    .height(4.dp)
                    .background(
                        color = if (index == current)
                            Color.White.copy(alpha = 0.9f)  // 当前进度点：白色半透明
                        else
                            Color.White.copy(alpha = 0.4f), // 其他进度点：更透明
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
private fun ImageSwiper(
    imageResList: List<Int>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { imageResList.size }
    )
    //同步pager状态和外部状态
    LaunchedEffect(pagerState.currentPage) {
        onIndexChange(pagerState.currentPage)
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        Image(
            painter = painterResource(id = imageResList[page]),
            contentDescription = "帖子图片",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TransparentProgressIndicator(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index == current)
                            Color.White.copy(alpha = 0.9f)  // 当前进度点：白色半透明
                        else
                            Color.White.copy(alpha = 0.4f), // 其他进度点：更透明
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
private fun AuthorHeader(
    authorName: String,
    avatarRes: Int,
    isFollowing: Boolean,
    onBackClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //返回按钮
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.back),
                tint = Color.Gray,
                contentDescription = "返回",
                modifier = Modifier.size(24.dp)
            )
        }
        //作者信息
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "作者头像",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = authorName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        //关注按钮
        IconButton(
            onClick = onFollowClick,
            modifier = Modifier.size(64.dp) // 调整按钮大小
        ) {
            Image(
                painter = painterResource(id = R.drawable.follow), // 你的关注图片
                contentDescription = "关注",
                modifier = Modifier
                    .width(64.dp)
                    .height(28.dp)
            )
        }
    }
}

@Composable
private fun PostContent(
    post: Post,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        //title是可空的，需要处理
        post.title?.let { title -> //不为空才执行lambda表达式
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        //正文
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        //发布日期，这里随便使用一个数据
        Text(
            text = "1天前",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun BottomInteractionBar(
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onSharedClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //快捷评论框
        //快捷评论框 - 使用Box包装确保垂直居中
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { /*todo*/ },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "说点什么",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        //点赞按钮
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(48.dp)
        ) {
            if (isLiked) {
                Icon(
                    painter = painterResource(id = R.drawable.like),
                    contentDescription = "点赞",
                    tint = Color.Red, // 只给点赞状态设置红色
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.notlike2),
                    contentDescription = "点赞",
                    // 不设置 tint，显示图标原始颜色
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        //评论按钮
        IconButton(onClick = onCommentClick) {
            Icon(
                painter = painterResource(R.drawable.comment),
                contentDescription = "评论",
                modifier = Modifier.size(24.dp)
            )
        }
        //收藏按钮
        IconButton(onClick = onCollectionClick) {
            Icon(
                painter = painterResource(R.drawable.collect),
                contentDescription = "收藏",
                modifier = Modifier.size(24.dp)
            )
        }
        //分享按钮
        IconButton(onClick = onSharedClick) {
            Icon(
                painter = painterResource(R.drawable.share),
                contentDescription = "分享",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

//关注状态管理类
class FollowManager(private val context: Context) {
    //懒加载，第一次使用的时候才加载
    private val sharedPreferences by lazy {
        context.getSharedPreferences("follow_prefs", Context.MODE_PRIVATE)
    }
    //查询指定作者关注状态
    fun isFollowing(authorName: String): Boolean {
        return sharedPreferences.getBoolean(authorName, false)
    }
    //切换关注状态方法
    fun toggleFollow(authorName: String) {
        val currentState = isFollowing(authorName)
        sharedPreferences.edit { putBoolean(authorName, !currentState) }
    }
    //设置关注状态方法
    fun setFollowState(authorName: String, isFollowing: Boolean) {
        sharedPreferences.edit { putBoolean(authorName, isFollowing) }
    }
}

@Composable
fun rememberFollowManager(): FollowManager {
    val context = LocalContext.current
    return remember { FollowManager(context) }
}