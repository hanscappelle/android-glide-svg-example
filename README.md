# android-glide-svg-issue

An attempt to reproduce issues with `scaleType="centerCrop"` failing when `Glide` is used to load an SVG in a `RecyclerView`.

Glide version 4.16.0
```
implementation("com.github.bumptech.glide:glide:4.16.0")
```

Glide SVG example https://github.com/bumptech/glide/blob/v4.16.0/samples/svg/src/main/java/com/bumptech/glide/samples/svg/MainActivity.java
using
```
implementation("com.caverock:androidsvg:1.2.1")
``` 