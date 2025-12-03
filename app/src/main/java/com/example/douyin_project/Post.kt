package com.example.douyin_project

data class Post(
    val id: String,
    val author: String,
    val avatarRes: Int,
    val imageResList: List<Int>, // 改为图片资源列表
    val title: String?,
    val content: String,
    val likeCount: Int,
    val imageAspectRatio: Float, // 图片宽高比
    val topics: List<String>,
    val createTime: Long
)

// 使用listof保存几个数据
fun generateMockPosts(): List<Post> {
    val currentTime = System.currentTimeMillis()
    val oneDayMillis = 24 * 60 * 60 * 1000L
    val threeDaysMillis = 3 * oneDayMillis
    val sixDaysMillis = 6 * oneDayMillis
    val eightDaysMillis = 8 * oneDayMillis

    return listOf(
        Post(
            "1", "设计师小王", R.drawable.begin2,
            listOf(R.drawable.main1, R.drawable.main2, R.drawable.main3),
            "现代简约风格设计",
            "这次的设计采用了极简主义风格，注重空间利用和光线效果",
            156, 0.75f,
            listOf("设计", "极简主义", "室内设计", "装修"),
            currentTime - 2 * 60 * 60 * 1000 // 2小时前
        ),
        Post(
            "2", "摄影爱好者", R.drawable.begin2,
            listOf(R.drawable.main2),
            null,
            "今天在公园拍到了一只可爱的小松鼠，阳光正好，心情愉悦",
            89, 1.33f,
            listOf("摄影", "自然", "小动物", "公园随拍"),
            currentTime - 25 * 60 * 60 * 1000 // 25小时前（昨天）
        ),
        Post(
            "3", "美食博主", R.drawable.begin2,
            listOf(R.drawable.main3, R.drawable.main4),
            "家常菜分享",
            "红烧肉的做法很简单，关键是火候要掌握好",
            234, 0.8f,
            listOf("美食", "家常菜", "红烧肉", "烹饪技巧"),
            currentTime - threeDaysMillis // 3天前
        ),
        Post(
            "4", "旅行达人", R.drawable.begin2,
            listOf(R.drawable.main4, R.drawable.main5, R.drawable.main6),
            null,
            "西藏的星空真的太美了，仿佛触手可及",
            567, 1.25f,
            listOf("旅行", "西藏", "星空", "摄影", "高原风光"),
            currentTime - sixDaysMillis // 6天前
        ),
        Post(
            "5", "程序员日常", R.drawable.begin2,
            listOf(R.drawable.main5),
            "代码优化技巧分享",
            "分享几个提升代码性能的小技巧",
            78, 0.85f,
            listOf("编程", "代码优化", "性能", "技术分享"),
            currentTime - eightDaysMillis // 8天前
        ),
        Post(
            "6", "读书笔记", R.drawable.begin2,
            listOf(R.drawable.main6, R.drawable.main7),
            "《活着》读后感",
            "人生无常，珍惜当下，感恩拥有",
            145, 1.15f,
            listOf("读书", "活着", "余华", "文学", "人生感悟"),
            currentTime - 30 * oneDayMillis // 30天前
        ),
        Post(
            "7", "健身打卡", R.drawable.begin2,
            listOf(R.drawable.main7),
            null,
            "今天完成了10公里跑步，感觉整个人都轻松了",
            299, 0.9f,
            listOf("健身", "跑步", "运动", "健康生活"),
            currentTime - 12 * 60 * 60 * 1000 // 12小时前
        ),
        Post(
            "8", "宠物日常", R.drawable.begin2,
            listOf(R.drawable.main8, R.drawable.main1, R.drawable.main2, R.drawable.main3),
            "我家猫咪",
            "每天最幸福的事就是看着猫咪睡觉",
            432, 1.1f,
            listOf("宠物", "猫咪", "萌宠", "日常"),
            currentTime - 4 * oneDayMillis // 4天前
        )
    )
}