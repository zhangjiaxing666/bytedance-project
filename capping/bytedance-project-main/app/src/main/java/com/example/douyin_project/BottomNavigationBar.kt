package com.example.douyin_project

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//底部导航栏
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
){
    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val navItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Friends,
            BottomNavItem.Camera,
            BottomNavItem.Messages,
            BottomNavItem.Profile
        )

        navItems.forEach {
            //判断当前遍历的导航栏是否对应正在显示的页面
            item -> val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if(item.isClickable)
                    {
                        onItemSelected(item)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if(selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if(selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}