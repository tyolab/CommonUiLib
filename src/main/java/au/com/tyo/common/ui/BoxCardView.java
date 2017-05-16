package au.com.tyo.common.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 14/3/17.
 */

public class BoxCardView extends CardView implements ImageViewHolder {

    private ImageView imageView;

    public BoxCardView(Context context) {
        super(context);
    }

    public BoxCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoxCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        imageView = (ImageView) findViewById(R.id.box_image_view);
    }
}
