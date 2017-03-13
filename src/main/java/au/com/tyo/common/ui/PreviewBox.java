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

import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.common.ui.ImageViewAutoRefreshed.ImageItem;

public class PreviewBox extends FrameLayout {

    public interface PreviewItem extends ImageItem {

    }

    private static final String LOG_TAG = "PreviewBox";

    // UI components
    private ImageViewAutoRefreshed imageRefresher;

    private TextView textView;

    private TextView textAlt;

    private FrameLayout bgView;

    /**
     * @param context
     */
    public PreviewBox(Context context) {
        super(context);

        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public PreviewBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PreviewBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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
        init(context);
    }

    private void init(Context context) {
        imageRefresher = new ImageViewAutoRefreshed(context);
        this.setClickable(true);
    }

    public void addPreviewItem(String url) { imageRefresher.addImage(url); }

    public void addPreviewItem(PreviewItem item) {
        imageRefresher.addImage(item);
        CharSequence alt = item.getAlt();
        if (null != alt)
            setAlt(alt);
    }

    public void setPreviewItems(List items) {
        imageRefresher.setImages(items);
    }

    public void setPreviewItemQuality(int quality) {
        imageRefresher.setImageQuality(quality);
    }

    public PreviewItem getCurrentItem() {
        return (PreviewItem) imageRefresher.getCurrentImage();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bgView = (FrameLayout) this.findViewById(R.id.preview_box_bg);

        textAlt = (TextView) this.findViewById(R.id.preview_box_alt);
        if (null == textAlt) {
            textAlt = new TextView(this.getContext());
            bgView.addView(textAlt, 0);
        }

        imageRefresher.setImageViewHolder((ImageViewHolder) this.findViewById(R.id.preview_box_bg_img));
        //imageRefresher.setScaleType(ImageView.ScaleType.CENTER_CROP );

        textView = (TextView) this.findViewById(R.id.preview_box_title);
        if (null == textView) {
            textView = new TextView(this.getContext());
            bgView.addView(textView);
        }
        setTitleResource(R.string.empty_string);
    }

    public void setInnerBackgroundResource(int resId) {
        bgView.setBackgroundResource(resId);
    }

    public void setBackgroundImageResource(int resId) {
        imageRefresher.getImageViewHolder().getImageView().setImageResource(resId);
    }

    public void setTitleResource(int resId) {
        textView.setText(resId);
    }

    public void setTitle(CharSequence title) {
        textView.setText(title);
    }

    public void setAlt(CharSequence alt) { textAlt.setText(alt); }

    public void setAlpha(float f) {
        imageRefresher.getImageViewHolder().getImageView().setAlpha(f);
    }

    /**
     *
     */
    public void start() {
        try {
            imageRefresher.display();
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    public void stop() {
        imageRefresher.pause();
    }

    public void setEachPreviewRoundFinishedListener(ImageViewAutoRefreshed.OnImageRefreshStateListener listener) {
        imageRefresher.setOnImageRefreshStateListener(listener);
    }

    public ImageDownloader getImageDownloader() {
        return imageRefresher.getImageDownloader();
    }
}
