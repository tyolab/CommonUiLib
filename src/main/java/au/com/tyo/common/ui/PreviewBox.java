package au.com.tyo.common.ui;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.common.ui.ImageViewAutoRefreshed.ImageItem;

public class PreviewBox extends FrameLayout {

    private static final String LOG_TAG = "PreviewBox";

    public enum Type {HIGHTLIGHT, CATEGORY};

    // UI components
    private ImageViewAutoRefreshed imgView;

    private TextView textView;

    // Data
    private List<ImageViewAutoRefreshed.ImageItem> items;


    /**
     * @param context
     */
    public PreviewBox(Context context) {
        super(context);

        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public PreviewBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PreviewBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @TargetApi(21)
    public PreviewBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.setClickable(true);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.setOnClickListener(listener);
    }

    public void addPreviewItem(ImageItem item) {
        if (null == items)
            items = new ArrayList<ImageItem>();
        items.add(item);
    }

    public void setPreviewItems(List items) {
        this.items = items;
    }

    public ImageItem getCurrentItem() {
        return (ImageItem) imgView.getCurrentItem();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        imgView = (ImageViewAutoRefreshed) this.findViewById(R.id.preview_box_bg_img);
        //imgView.setScaleType(ImageView.ScaleType.CENTER_CROP );

        textView = (TextView) this.findViewById(R.id.preview_box_title);
        setTitleResource(R.string.empty_string);
    }

    public void setBackgroudImageResource(int resId) {
        imgView.setImageResource(resId);
    }

    public void setTitleResource(int resId) {
        textView.setText(resId);
    }

    /**
     *
     */
    public void start() {
        start(items);
    }

    /**
     *
     * @param images
     */
    public void start(List images) {
        imgView.setImages(images);

        try {
            imgView.display();
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    public void stop() {
        imgView.pause();
    }
}
