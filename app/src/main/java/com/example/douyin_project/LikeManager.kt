package com.example.douyin_project

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

//用于管理点赞的
class LikeManager(private val sharedPreferences: SharedPreferences) {
    // 获取用户所有点赞过的帖子ID集合
    fun getLikedPosts(): Set<String> {
        return sharedPreferences.getStringSet("liked_posts", mutableSetOf()) ?: mutableSetOf()
    }
    //用于切换点赞状态
    fun toggleLike(postId: String) {
        val currentLiked = getLikedPosts().toMutableSet() //转化为可变的set
        if(currentLiked.contains(postId)) {
            currentLiked.remove(postId)
        }else{
            currentLiked.add(postId)
        }
        sharedPreferences.edit().putStringSet("liked_posts", currentLiked).apply()
    }
    //返回bool值来判断是否点了赞
    fun isLiked(postId: String): Boolean {
        return getLikedPosts().contains(postId)
    }
}

@Composable
fun rememberLikeManager(): LikeManager {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_likes", Context.MODE_PRIVATE)
    }
    return remember { LikeManager(sharedPreferences) }
}