package com.example.douyin_project

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 作品卡片组件
@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    onLikeClick: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable{ onPostClick(post) },
        shape = MaterialTheme.shapes.small, //设置卡片的圆角形状
        //设置卡片轻微阴影效果
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 作品封面逻辑
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(post.imageAspectRatio.coerceIn(0.75f, 1.33f)) //限制宽高比在指定范围内
            ) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = "作品封面",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            //作品内容区域
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                //作品标题/正文，并且需要有限显示标题
                Text(
                    text = post.title ?: post.content,
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 2, //限制行数
                    overflow = TextOverflow.Ellipsis, //超过用...表示
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if(post.title != null) FontWeight.SemiBold else FontWeight.Normal,
                    lineHeight = 20.sp
                )
                //作者信息以及点赞
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //作者头像和昵称
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = post.avatarRes),
                            contentDescription = "作者头像",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape), //圆形裁剪
                            contentScale = ContentScale.Crop //缩放模式,图片充满圆形区域不会变形
                        )
                        Spacer(modifier = Modifier.width(8.dp)) //图片和文字的间隔
                        Text(
                            text = post.author,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1, //限制行数
                            overflow = TextOverflow.Ellipsis, //超过用...表示
                            color = Color.Gray
                        )
                    }
                    //点赞图标和数量
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onLikeClick(post.id) }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if(isLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite, //心心图片
                            contentDescription = if(isLiked) "取消点赞" else "点赞",
                            tint = if(isLiked) Color.Red else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp)) //心心和点赞数量之间的距离
                        Text(
                            text = formatCount(post.likeCount + if(isLiked) 1 else 0), //点击了就+1点赞数量
                            style = MaterialTheme.typography.labelSmall,
                            color = if(isLiked) Color.Red else Color.Gray //点击了就呈现红色心心
                        )
                    }
                }
            }
        }
    }
}

//点赞数目多的话可以用k和w来表示
private fun formatCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 10000}w"
        count >= 1000 -> "${count / 1000}k"
        else -> count.toString()
    }
}