package com.example.douyin_project

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val isClickable: Boolean = true
){
    object Home : BottomNavItem("home", Icons.Default.Home, "首页", true)
    object Friends : BottomNavItem("friends", Icons.Default.People, "朋友", false)
    object Camera : BottomNavItem("camera", Icons.Default.Add, "相机", false)
    object Messages : BottomNavItem("messages", Icons.Default.Message, "消息", false)
    object Profile : BottomNavItem("profile", Icons.Default.Person, "我", true)
}