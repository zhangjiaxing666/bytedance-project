package com.example.douyin_project

import android.content.SharedPreferences

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