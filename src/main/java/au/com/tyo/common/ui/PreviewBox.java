package au.com.tyo.common.ui;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import au.com.tyo.common.ui.ImageViewAutoRefreshed.ImageItem;

public class PreviewBox extends FrameLayout {

    private static final String LOG_TAG = "PreviewBox";

    public enum PreviewType { HIGHLIGHT, CATEGORY, CATEGORY_ALL };

    // UI components
    private ImageViewAutoRefreshed imgView;

    private TextView textView;

    private PreviewType type;

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

    public PreviewType getType() {
        return type;
    }

    public void setType(PreviewType type) {
        this.type = type;
    }

    private void init() {
        this.setClickable(true);
        this.setType(PreviewType.HIGHLIGHT);
    }

    public boolean isCategoryType() {
        return type == PreviewType.CATEGORY;
    }

    public boolean isCategoryAllType() {
        return type == PreviewType.CATEGORY_ALL;
    }

    public boolean isHighlightType() {
        return type == PreviewType.HIGHLIGHT;
    }

    public void addPreviewItem(ImageItem item) {
        imgView.addImage(item);
    }

    public void setPreviewItems(List<ImageItem> items) {
        imgView.setImages(items);
    }

    public ImageItem getCurrentItem() {
        return (ImageItem) imgView.getCurrentImage();
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

    public void setTitle(CharSequence title) {
        textView.setText(title);
    }

    /**
     *
     */
    public void start() {
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
