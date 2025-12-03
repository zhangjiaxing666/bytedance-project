package com.example.douyin_project

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//单例模式
object ImageLoaderManager {
    private var imageloader: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
        return imageloader ?: createImageLoader(context).also {
            imageloader = it
        }
    }

    private fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.3) //使用30%的最大内存
                    .weakReferencesEnabled(true) //内存不足时，自动清理缓存
                    .build()
            }
            .diskCache { //把图片存储在手机存储中
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(300 * 1024 * 1024) //300MB
                    .build()
            }
            .respectCacheHeaders(false) //使用优先缓存
            .crossfade(true) //启用淡入动画
            .allowHardware(true) //使用硬件位图
            .build()
    }

    // 预加载函数
    suspend fun preloadImage(
        context: Context,
        data: Any,
        width: Int = 0,
        height: Int = 0
    ) = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(data)
                .size(if(width > 0 && height > 0) Size(width, height) else Size.ORIGINAL)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()

            getImageLoader(context).enqueue(request) //加入队列异步执行
        } catch (e: Exception) {
            //静默失败不影响主流程
        }
    }

    //批量预加载
    suspend fun preloadImages(
        context: Context,
        imageList: List<Any>,
        width: Int = 0,
        height: Int = 0
    ) = withContext(Dispatchers.IO) {
        imageList.forEach { imageData ->
            preloadImage(context, imageData, width, height)
        }
    }
}