package au.com.tyo.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/3/17.
 */

public class PreviewBoxImageView extends androidx.appcompat.widget.AppCompatImageView implements ImageViewHolder {

    public PreviewBoxImageView(Context context) {
        super(context);
    }

    public PreviewBoxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewBoxImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ImageView getImageView() {
        return this;
    }
}
