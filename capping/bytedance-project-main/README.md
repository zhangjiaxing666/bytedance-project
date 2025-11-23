# bytedance-project

# 1. 项目背景
# 2. 技术方案选型
# 3.分模块设计与实现
## 3.1 关于底部导航栏的实现
首先封装一个**BottomNavItem**的密封类，然后我们定义出底部导航栏所需的五个元素分别为 **首页、朋友、相机、消息、我** 

然后接下来介绍**BottomNavigationBar**的具体实现，这个函数接受两个参数:当前所在的页面和一个接收**BottomNavItem**的函数

```kotlin
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {}
```

整体采用**Row**的布局方式，我们先定义一个包含了所有种类的list

```kotlin
val navItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Friends,
    BottomNavItem.Camera,
    BottomNavItem.Messages,
    BottomNavItem.Profile
)
```

然后使用forEach方法来遍历其中的每个Item，每个导航项使用Box来封装，权重都为1f，使其能均匀分布，这里需要注意的是，当遍历到相机这个模块的时候，我们不能显示出文字，而是显示出图片，所以需要if-else语句来判断遍历到的类型

```kotlin
if (item is BottomNavItem.Camera) {
    // Camera 显示图片
    Image(
        painter = painterResource(R.drawable.bottom1),
        contentDescription = item.title,
        modifier = Modifier.size(40.dp)
    )
} else {
    // 其他四个项目只显示文字
    Text(
        text = item.title,
        color = if (selected) Color.Black else Color.Gray,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 17.sp
    )
}
```

这样一个底部导航栏就做好了

## 3.2 关于上部导航栏的实现

我们将上面的导航栏叫做**Hometabs**，创建这个组件，这个函数接收三个参数，和底部导航栏差不多，如下代码所示

```kotlin
fun HomeTabs(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
){}
```

函数接受三个参数：当前位置，函数，还有一个缺省参数的**modifier**，同样，我们先定义一个list，里面有5个关键模块

```kotlin
val tabs = listOf("北京", "团购", "关注", "社区", "推荐")
```

众所周知，手机上面是有状态栏一项的，所以我们需要给他留出来，我们使用这段代码来获取状态栏的高度

```kotlin
val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

```

接下来整体采用Column布局，分为两个部分，一个是状态栏所站位的空间，另一个是上部导航栏。具体结构如下代码所示

```kotlin
Column(){
    Box()
    Row(){}
}
```

Box部分的实现逻辑较为简单，只需要传入**statusBarHeight**的高度然后采用透明的颜色即可

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(statusBarHeight)
        .background(Color.Transparent)
)
```

Row部分即使我们上部导航栏的主逻辑部分，经过我们使用figma测量它的高度为44dp，这里需要注意的是上部导航栏的最左和最右侧都有两个图标，然后中间夹着的是5个词语我们已经提前放到了listof里面，后面直接采用forEach来遍历即可。

上部导航栏的具体结构如下所示

```kotlin
Row{
    Image() //功能栏
    tabs.forEach{
        Box(){
            Text()
        }
    }
    Image() //搜索图标
}
```

关于两个图标的实现我们只需要调用存在R.drawable里面的两张图即可，调整好其大小以及距离即可。我们主要说一下两个图标中间的实现逻辑

我们先遍历之前创建的列表，如果他被选中了，我们就需要把字体颜色改成黑色并且还要在下面加上一根下划线，下划线如何实现呢？经过查询，我们可以使用modifier中的drawBehind中的drawline来实现，粗度设置为2f即可，如下代码所示

```kotlin
Text(
    text = tab,
    modifier = Modifier
        .padding(horizontal = 15.dp, vertical = 12.dp)
        .drawBehind {
            if (isSelected) {
                val lineWidth = size.width  // 使用完整宽度
                val startX = (size.width - lineWidth) / 2  // 从中间开始
                val lineBottom = size.height + 6.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(startX, lineBottom),
                    end = Offset(startX + lineWidth, lineBottom),
                    strokeWidth = 2f
                )
            }
        },
    color = if(isSelected) Color.Black else Color.Gray,
    fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
    fontSize = 16.sp
)
```



# 4.技术难点和解决方案

## 4.1 关于上部导航栏的下划线的实现

我们需要使用Modifier.drawLBehind来实现此功能,drawBehind里面有一个成员drawLine来实现此功能