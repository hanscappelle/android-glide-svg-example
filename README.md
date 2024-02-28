# android-glide-svg-example

An example on how to load SVG vector images into android `ImageView` using `Glide` and `AndroidSvg`
libraries.

Based on this `Glide` example app https://github.com/bumptech/glide/tree/v4.16.0/samples/svg.

![app screenshot](https://github.com/hanscappelle/android-glide-svg-example/blob/cd0d3185626b2f81b1227fe44ba751016008639e/screenshots/Screenshot_20240228_084619.png)

# Steps

## Dependencies

Add dependencies to `app/build.gradle` or `app/build.gradle.kts`:

```
repositories {
  google()
  mavenCentral()
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.caverock:androidsvg:1.4")
    // use annotationProcessor for non kotlin projects
    // annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
}
```

For kapt to work you'll have to enable the plugin also in `build.gradle(.kts)`:
```
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22"
}
```
and on top of `app/build.gradle(.kts)`:
```
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}
```

Also see setup instructions from Glide documentation https://bumptech.github.io/glide/doc/download-setup.html

## AppGlideModule

Next implement your own `AppGlideModule` using annotation `@GlideModule` to register `SVGDecoder` 
and such.

```
@GlideModule
class SvgModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry
            .register(SVG::class.java, PictureDrawable::class.java, SvgDrawableTranscoder())
            .append(InputStream::class.java, SVG::class.java, SvgDecoder())
    }

    // Disable manifest parsing to avoid adding similar modules twice.
    override fun isManifestParsingEnabled() = false
}
```

SVGDecoder implementation
```
class SvgDecoder : ResourceDecoder<InputStream, SVG> {
    override fun handles(source: InputStream, options: Options) = true

    @Throws(IOException::class)
    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SVG>? {
        return try {
            // this is the implementation from lib v4.10 working w/o issues in RecyclerView
            val svg = SVG.getFromInputStream(source)
            SimpleResource(svg)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }
    }
}
```

And finally the SvgDrawableTranscoder implementation
```
class SvgDrawableTranscoder : ResourceTranscoder<SVG?, PictureDrawable> {
    override fun transcode(
        toTranscode: Resource<SVG?>, options: Options
    ): Resource<PictureDrawable>? {
        val svg = toTranscode.get()
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        return SimpleResource(drawable)
    }
}
```

For more info see https://bumptech.github.io/glide/doc/configuration.html

## Load Images

Finally load images using `Glide` like so:

```
val svgPath = "https://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg"
val imageView: ImageView = findViewById(R.id.single_image)
Glide.with(baseContext).load(svgPath).into(imageView)
```

# Troubleshooting

## SVG Parsing errors

This example is using `https://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg` as an SVG 
source. If your SVG file doesn't load check logcat for errors. SVG is an XML based image format 
so has to be parsed and parsing can cause errors.

## scaleType not working

Since `Glide` version 4.12 the width and height dimensions from the SVG source are set in the 
`SVGDecoder` example project. This is causing issues with `scaleType` set on the `ImageView`.

```
class SvgDecoder : ResourceDecoder<InputStream, SVG> {
    override fun handles(source: InputStream, options: Options) = true

    @Throws(IOException::class)
    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SVG>? {
        return try {
            // this is the implementation from lib v4.10 working w/o issues in RecyclerView
            val svg = SVG.getFromInputStream(source)

            // region these lines were added in v4.12 but cause issues with RecyclerView
            //if (width != SIZE_ORIGINAL) {
            //  svg.setDocumentWidth(width);
            //}
            //if (height != SIZE_ORIGINAL) {
            //  svg.setDocumentHeight(height);
            //}
            // endregion
            
            SimpleResource(svg)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }
    }
}
```

## Unresolved reference GlideApp

`GlideApp` has to be used instead of `Glide` when you want to work with your own `GlideModule` 
configuration. If this is not generated in your project double check that kapt plugin is configured
(or if you use Java check for `annotationProcessor`).

# Resources

Glide version 4.16.0 from https://github.com/bumptech/glide
```
implementation("com.github.bumptech.glide:glide:4.16.0")
```

Android SVG parser from https://bigbadaboom.github.io/androidsvg/
```
implementation("com.caverock:androidsvg:1.2.1")
``` 