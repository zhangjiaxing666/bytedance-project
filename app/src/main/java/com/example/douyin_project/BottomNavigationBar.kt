package com.example.douyin_project


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//底部导航栏
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(83.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Friends,
            BottomNavItem.Camera,
            BottomNavItem.Messages,
            BottomNavItem.Profile
        )

        navItems.forEach { item ->
            val selected = currentRoute == item.route

            // 每个导航项
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(49.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        enabled = item.isClickable,
                        onClick = { onItemSelected(item) }
                    )
                    .background(
                        Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (item is BottomNavItem.Camera) {
                        // Camera 显示图片
                        Image(
                            painter = painterResource(R.drawable.bottom1),
                            contentDescription = item.title,
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        // 其他四个项目只显示文字
                        Text(
                            text = item.title,
                            color = if (selected) Color.Black else Color.Gray,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}