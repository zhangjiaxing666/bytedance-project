package com.example.douyin_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

//主屏幕
@Composable
fun MainScreen(){
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var currentTab by remember { mutableStateOf("社区")}

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
    ) { paddingValues -> //自动计算的内边距
        Box(modifier = Modifier.fillMaxSize())
        {
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
                    //其他不可点击的页面显示提示
                    Box(
                        modifier = Modifier.fillMaxSize(),  // 移除 paddingValues
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

@Composable
fun HomeScreen(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier) {
        // Tab栏
        HomeTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected
        )
        // 内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "当前Tab: $currentTab\n\n这里是首页内容区域\n",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier)
{
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "个人主页",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "\n简洁的个人信息页面\n\n用户名: 用户123\n等级: VIP\n签名: 享受生活每一天",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

        }
    }
}