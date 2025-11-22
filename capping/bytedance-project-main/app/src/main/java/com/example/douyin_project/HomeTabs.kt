package com.example.douyin_project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.platform.LocalConfiguration
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

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(44.dp)) //figma测量出来距离顶部44.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp) //figma上面测量出来是44高度
                .background(Color.White)
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            // Tab列表主逻辑
            Row {
                Image(
                    painter = painterResource(id = R.drawable.hometab1), // 替换为你的PNG资源ID
                    contentDescription = "自定义图标",
                    modifier = Modifier
                        .size(44.dp)  // figma测量出来图标大小44
                        .padding(start = 12.dp)
                        .clickable {
                            // 图标的点击事件
                            // 可以在这里添加图标的点击逻辑
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
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                            color = if(isSelected) MaterialTheme.colorScheme.primary
                            else Color.Gray,
                            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.hometab2), // 替换为你的PNG资源ID
                    contentDescription = "自定义图标",
                    modifier = Modifier
                        .size(44.dp)  // figma测量出来图标大小44
                        .padding(end = 12.dp)  // 图标和Tab列表之间的间距
                        .clickable {
                            // 图标的点击事件
                            // 可以在这里添加图标的点击逻辑
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
        // 底部指示器
        Divider(
            color = Color.LightGray.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}


