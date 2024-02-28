package be.hcpl.android.glidesvg.svg;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import java.io.IOException;
import java.io.InputStream;

/** Decodes an SVG internal representation from an {@link InputStream}. */
public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {

  @Override
  public boolean handles(@NonNull InputStream source, @NonNull Options options) {
    // TODO: Can we tell?
    return true;
  }

  public Resource<SVG> decode(
      @NonNull InputStream source, int width, int height, @NonNull Options options)
      throws IOException {
    try {
      // this is the implementation from lib v4.10 working w/o issues in RecyclerView
      SVG svg = SVG.getFromInputStream(source);

      // region these lines were added in v4.12 but cause issues with RecyclerView
      if (width != SIZE_ORIGINAL) {
        svg.setDocumentWidth(width);
      }
      if (height != SIZE_ORIGINAL) {
        svg.setDocumentHeight(height);
      }
      // endregion

      return new SimpleResource<>(svg);
    } catch (SVGParseException ex) {
      throw new IOException("Cannot load SVG from stream", ex);
    }
  }
}