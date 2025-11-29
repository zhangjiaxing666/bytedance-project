package com.example.douyin_project

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
    var selectedTopic by remember { mutableStateOf<String?>(null) } // 新增：选中的话题

    //从本地存储读取关注状态
    LaunchedEffect(
        post.author
    ) {
        isFollowing = followManager.isFollowing(post.author)
    }

    // 如果选中了话题，显示话题详情页
    if (selectedTopic != null) {
        TopicDetailScreen(
            topic = selectedTopic!!,
            onBackClick = { selectedTopic = null }, // 返回时清空选中的话题
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            // 状态栏占位空间
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
                    followManager.setFollowState(post.author, isFollowing)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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

                        // 进度条
                        if (post.imageResList.size > 1) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp)
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
                    onTopicClick = { topic -> selectedTopic = topic }, // 传递话题点击回调
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
}

@Composable
private fun FullWidthProgressIndicator(
    total: Int, // 总图片数
    current: Int, // 当前进度位置
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
    imageResList: List<Int>, // 图片资源ID列表
    currentIndex: Int, // 当前显示的图片索引
    onIndexChange: (Int) -> Unit, // 当图片切换时的回调函数
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
                tint = Color.Black,
                contentDescription = "返回",
                modifier = Modifier.size(24.dp)
            )
        }
        //作者信息
        Row(
            modifier = Modifier
                .weight(1f),
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
fun TopicDetailScreen(
    topic: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 话题标题
            Text(
                text = "#$topic",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF04498D)
            )
        }

        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "话题：$topic",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "这里是关于「$topic」话题的内容页面\n\n可以展示相关帖子、讨论等内容",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PostContent(
    post: Post,
    onTopicClick: (String) -> Unit, // 新增：话题点击回调
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // title是可空的，需要处理
        post.title?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 正文
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 话题词 - 显示在正文末尾
        if (post.topics.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                post.topics.forEach { topic ->
                    Text(
                        text = "#$topic",
                        color = Color(0xFF04498D),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .clickable {
                                // 处理话题点击，跳转到话题页面
                                onTopicClick(topic)
                            }
                            .padding(vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 发布日期 - 使用格式化日期
        Text(
            text = formatPostDate(post.createTime),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

// 日期格式化函数
private fun formatPostDate(createTime: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - createTime
    val days = diff / (24 * 60 * 60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val minutes = diff / (60 * 1000)

    return when {
        minutes < 1 -> "刚刚"
        hours < 1 -> "${minutes}分钟前" // 1小时内显示分钟
        hours < 24 -> {
            // 24小时内显示具体时间或"昨天"
            val calendar = Calendar.getInstance().apply { timeInMillis = createTime }
            val currentCalendar = Calendar.getInstance()

            if (currentCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                // 同一天显示具体时间
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(createTime))
            } else {
                // 昨天显示"昨天 + 时间"
                "昨天 ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(createTime))}"
            }
        }
        days < 7 -> "${days}天前" // 7天内显示天数
        else -> SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(createTime)) // 其他显示具体日期
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
) {
    // 添加状态管理点击次数
    var likeCount by remember { mutableStateOf(156) } // 初始点赞数
    var commentCount by remember { mutableStateOf(89) } // 初始评论数
    var collectCount by remember { mutableStateOf(23) } // 初始收藏数
    var shareCount by remember { mutableStateOf(45) } // 初始分享数

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //快捷评论框
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

        // 点赞按钮和计数
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            IconButton(
                onClick = {
                    onLikeClick()
                    // 点赞/取消点赞时更新计数
                    likeCount = if (isLiked) likeCount - 1 else likeCount + 1
                },
                modifier = Modifier.size(32.dp)
            ) {
                if (isLiked) {
                    Icon(
                        painter = painterResource(id = R.drawable.like),
                        contentDescription = "取消点赞",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.notlike2),
                        contentDescription = "点赞",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = formatCount(likeCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 评论按钮和计数
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            IconButton(
                onClick = onCommentClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.comment),
                    contentDescription = "评论",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = formatCount(commentCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 收藏按钮和计数
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            IconButton(
                onClick = onCollectionClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.collect),
                    contentDescription = "收藏",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = formatCount(collectCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 分享按钮和计数
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            IconButton(
                onClick = {
                    onSharedClick()
                    // 分享时计数加一
                    shareCount += 1
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = "分享",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = formatCount(shareCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
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