package com.example.douyin_project

data class Post(
    val id: String,
    val author: String,
    val avatarRes: Int,
    val imageRes: Int,
    val title: String?,
    val content: String,
    val likeCount: Int,
    val imageAspectRatio: Float //图片宽高比
)

// 使用listof保存几个数据
fun generateMockPosts(): List<Post> {
    return listOf(
        Post("1", "设计师小王", R.drawable.begin2, R.drawable.main1,
            "现代简约风格设计", "这次的设计采用了极简主义风格，注重空间利用和光线效果", 156, 0.75f),
        Post("2", "摄影爱好者", R.drawable.begin2, R.drawable.main2,
            null, "今天在公园拍到了一只可爱的小松鼠，阳光正好，心情愉悦", 89, 1.33f),
        Post("3", "美食博主", R.drawable.begin2, R.drawable.main3,
            "家常菜分享", "红烧肉的做法很简单，关键是火候要掌握好", 234, 0.8f),
        Post("4", "旅行达人", R.drawable.begin2, R.drawable.main4,
            null, "西藏的星空真的太美了，仿佛触手可及", 567, 1.25f),
        Post("5", "程序员日常", R.drawable.begin2, R.drawable.main5,
            "代码优化技巧分享", "分享几个提升代码性能的小技巧", 78, 0.85f),
        Post("6", "读书笔记", R.drawable.begin2, R.drawable.main6,
            "《活着》读后感", "人生无常，珍惜当下，感恩拥有", 145, 1.15f),
        Post("7", "健身打卡", R.drawable.begin2, R.drawable.main7,
            null, "今天完成了10公里跑步，感觉整个人都轻松了", 299, 0.9f),
        Post("8", "宠物日常", R.drawable.begin2, R.drawable.main8,
            "我家猫咪", "每天最幸福的事就是看着猫咪睡觉", 432, 1.1f)
    )
}