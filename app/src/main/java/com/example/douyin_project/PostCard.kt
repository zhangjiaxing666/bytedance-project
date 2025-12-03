package com.example.douyin_project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage

// 作品卡片组件 - 优化版（保持原有结构）
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
            .clickable { onPostClick(post) },
        // 优化1：使用更小的圆角，减少CircularRRectOp
        shape = MaterialTheme.shapes.small,
        // 优化2：移除阴影，消除ShadowCircularRRectOp
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // 改为0dp
    ) {
        Column {
            // 作品封面逻辑
            // 优化3：直接使用AsyncImage作为作品封面逻辑不使用Box对齐再进行包装
            AsyncImage(
                model = post.imageResList[0], // 直接传资源ID
                contentDescription = "作品封面",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(post.imageAspectRatio.coerceIn(0.75f, 1.33f)),
                contentScale = ContentScale.Crop
            )

            // 作品内容区域
            Column(
                // 优化4：减小内边距
                modifier = Modifier.padding(8.dp)
            ) {
                // 作品标题/正文
                Text(
                    text = post.title ?: post.content,
                    modifier = Modifier.padding(bottom = 6.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    // 优化5：简化字体样式
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (post.title != null) FontWeight.Medium else FontWeight.Normal,
                        lineHeight = 18.sp // 减小行高
                    )
                )

                // 作者信息以及点赞
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 作者头像和昵称
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = post.avatarRes,
                            contentDescription = "作者头像",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape), //圆形裁剪
                            contentScale = ContentScale.Crop //缩放模式,图片充满圆形区域不会变形
                        )
                        Spacer(modifier = Modifier.width(6.dp)) // 减小间距

                        Text(
                            text = post.author,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF666666) // 使用固定颜色
                        )
                    }

                    // 点赞图标和数量
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        // 优化6：移除内边距，减少FillRectOp
                        modifier = Modifier
                            .clickable { onLikeClick(post.id) }
                    ) {
                        // 优化7：使用Icon替代Image，减少纹理操作
                        Icon(
                            painter = painterResource(id = if (isLiked) R.drawable.like else R.drawable.notlike),
                            contentDescription = if (isLiked) "取消点赞" else "点赞",
                            tint = if (isLiked) Color.Red else Color(0xFF666666),
                            modifier = Modifier.size(16.dp) // 减小尺寸
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = formatCount(post.likeCount + if (isLiked) 1 else 0),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

// 点赞数目格式化
private fun formatCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 10000}w"
        count >= 1000 -> "${count / 1000}k"
        else -> count.toString()
    }
}