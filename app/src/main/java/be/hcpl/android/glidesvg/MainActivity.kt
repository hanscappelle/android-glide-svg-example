package be.hcpl.android.glidesvg

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainActivity : Activity() {

    private val svgPath = "https://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg"

    private val pngPath =
        "https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // add support for SVG
        // example from https://github.com/bumptech/glide/tree/v4.16.0/samples/svg
        // val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(baseContext)
        //    .`as`(PictureDrawable::class.java)
        //    .listener(SvgSoftwareLayerSetter())

        // single image, no issue
        val imageView: ImageView = findViewById(R.id.single_image)
        Glide.with(baseContext).load(svgPath).into(imageView)
        //requestBuilder.load(svgPath).centerCrop().into(imageView)

        // same image used in recyclerView = ISSUE with centerCrop, limited to SVG
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        val adapter = GlideAdapter(
            listOf(
                Image(svgPath, "SVG image CENTER_CROP", ScaleType.CENTER_CROP),
                Image(pngPath, "PNG image CENTER_CROP", ScaleType.CENTER_CROP),
                Image(svgPath, "SVG image CENTER_INSIDE", ScaleType.CENTER_INSIDE),
                Image(pngPath, "PNG image CENTER_INSIDE", ScaleType.CENTER_INSIDE),
                Image(svgPath, "SVG image FIT_CENTER", ScaleType.FIT_CENTER),
                Image(pngPath, "PNG image FIT_CENTER", ScaleType.FIT_CENTER),
                Image(svgPath, "SVG image FIT_XY", ScaleType.FIT_XY),
                Image(pngPath, "PNG image FIT_XY", ScaleType.FIT_XY),
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

}

data class Image(
    val path: String,
    val text: String,
    val scaleType: ScaleType,
)

class GlideAdapter(
    private val items: List<Image>,
) : RecyclerView.Adapter<GlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GlideViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
    )

    override fun onBindViewHolder(holder: GlideViewHolder, position: Int) {
        val image = items[position]
        // use Glide here for image loading
        holder.imageView.scaleType = image.scaleType
        Glide.with(holder.itemView.context).load(image.path).into(holder.imageView)
        holder.textView.text = image.text
    }

    override fun getItemCount() = items.size

}

class GlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageView: ImageView = itemView.findViewById(R.id.recycler_image)
    val textView: TextView = itemView.findViewById(R.id.recycler_text)

}
