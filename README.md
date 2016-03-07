# 无限循环pager
·单向的·、·双向的·、·水平的·、·垂直的·、·自动轮播的·、·无限循环的· pager，所有你想要的都在这里。该Pager目前提供了三种滑动效果———— 覆盖、滚动、栈。
#功能演示
![功能演示动画](https://github.com/sunstar1chen/InfinitePager/blob/master/pager_guide_video.gif "pager 功能演示")

#示例代码
```Java
        psv = (InfinitePager) findViewById(R.id.page_psv);
        psv.setSlideMode(InfinitePager.SlideMode.HORIZONTAL);  // 设置滑动模式
        psv.reverseContainer(false);   // 反转容器（栈动画模式时设置为true ， 其它为 false）
        psv.enableIntervalLoop(3000);  // 启动轮播模式，3000 ，轮播间隔时间
        psv.setTransformer(new HorizontalScroll(500));  // 设置动画模式
        psv.setMinmumLoopCount(10);   // 设置最小循环数量（当数据量小于该值时，循环模式设置为true 无效）
```

