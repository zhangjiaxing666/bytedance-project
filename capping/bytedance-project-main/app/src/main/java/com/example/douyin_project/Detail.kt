package com.example.douyin_project

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回"
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
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = if(isFollowing) Color.Gray else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = if(isFollowing) "已关注" else "关注",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = if(isFollowing) Color.DarkGray else Color.White
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
private fun ImageSwiper(
    images: List<String>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = {images.size}
    )
    //同步pager状态和外部状态
    LaunchedEffect(pagerState.currentPage) {
        onIndexChange(pagerState.currentPage)
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Image(

            )
        }
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
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("说点什么") },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        //点赞按钮
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "评论",
                tint = Color.Gray
            )
        }
        //评论按钮
        IconButton(onClick = onCommentClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "评论",
                tint = Color.Gray
            )
        }
        //收藏按钮
        IconButton(onClick = onCollectionClick) {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = "收藏",
                tint = Color.Gray,
            )
        }
        //分享按钮
        IconButton(onClick = onSharedClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "分享",
                tint = Color.Gray
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