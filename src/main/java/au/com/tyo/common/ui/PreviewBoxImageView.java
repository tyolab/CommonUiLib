package au.com.tyo.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by monfee on 13/3/17.
 */

public class PreviewBoxImageView extends android.support.v7.widget.AppCompatImageView implements ImageViewHolder {

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
