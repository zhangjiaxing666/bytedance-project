package com.example.douyin_project

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringSetKey
// 用于创建和管理Android Jetpack DataStore 的实例
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class DataStoreManager(private val context: Context)
{
    private object PreferencesKeys {
        val LIKED_POSTS = stringSetKey("liked_posts")
    }
}