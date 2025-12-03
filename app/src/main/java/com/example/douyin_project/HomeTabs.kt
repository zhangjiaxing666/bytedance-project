package com.example.douyin_project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//首页Tab栏组件

@Composable
fun HomeTabs(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val tabs = listOf("北京", "团购", "关注", "社区", "推荐")

    // 获取状态栏高度
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(modifier = modifier) {
        // 状态栏占位空间 - 显示系统状态栏信息
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(Color.Transparent)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.Transparent)
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row {
                Image(
                    painter = painterResource(id = R.drawable.hometab1),
                    contentDescription = "自定义图标",
                    modifier = Modifier
                        .size(44.dp)
                        .padding(start = 12.dp)
                        .clickable {
                            // 图标的点击事件
                        },
                    contentScale = ContentScale.Fit
                )
                tabs.forEach { tab ->
                    val isSelected = tab == currentTab

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                if (tab == "社区")
                                    onTabSelected(tab)
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = tab,
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 12.dp)
                                .drawBehind {
                                    if (isSelected) {
                                        val lineWidth = size.width  // 使用完整宽度
                                        val startX = (size.width - lineWidth) / 2  // 从中间开始
                                        val lineBottom = size.height + 6.dp.toPx()
                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(startX, lineBottom),
                                            end = Offset(startX + lineWidth, lineBottom),
                                            strokeWidth = 2f
                                        )
                                    }
                                },
                            color = if(isSelected) Color.Black else Color.Gray,
                            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.hometab2),
                    contentDescription = "自定义图标",
                    modifier = Modifier
                        .size(44.dp)
                        .padding(end = 12.dp)
                        .clickable {
                            // 图标的点击事件
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

