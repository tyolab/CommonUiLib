package au.com.tyo.common.ui;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

    private boolean useGlide;

    /**
     * @param context
     */
    public PreviewBox(Context context) {
        super(context);

        init(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public PreviewBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PreviewBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        imageRefresher = new ImageViewAutoRefreshed(context);
        this.setClickable(true);

        if (null != attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PreviewBox, 0, 0);
            try {
                useGlide = ta.getBoolean(R.styleable.PreviewBox_useGlide, false);
                String item = ta.getString(R.styleable.PreviewBox_item);
                if (null != item && item.length() > 0)
                    imageRefresher.addImage(item);
            } finally {
                ta.recycle();
            }
        }
    }

    public void addPreviewItem(String url) {
        imageRefresher.addImage(url);
    }

    public void addPreviewItem(PreviewItem item) {
        addPreviewObject(item);
        CharSequence alt = item.getAlt();
        if (null != alt)
            setAlt(alt);
    }

    public void addPreviewObject(Object item) {
        imageRefresher.addImage(item);
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

    public int getImagesCount() {
        return imageRefresher.getImagesCount();
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
        imageRefresher.setUseGlide(useGlide);

        textView = (TextView) this.findViewById(R.id.preview_box_title);
        if (null == textView) {
            textView = new TextView(this.getContext());
            bgView.addView(textView);
        }
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

    public void showTitle(boolean show) {
        if (null != textView) {
            if (show)
                textView.setVisibility(View.VISIBLE);
            else
                textView.setVisibility(View.GONE);
        }

    }

    public void setAlt(CharSequence alt) {
        textAlt.setText(alt);
    }

    public void setAlpha(float f) {
        imageRefresher.getImageViewHolder().getImageView().setAlpha(f);
    }

    /**
     * Start slide show
     */
    public void start() {
        try {
            imageRefresher.display();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    /**
     * Sto slide show
     */
    public void stop() {
        imageRefresher.pause();
    }

    public void setEachPreviewRoundFinishedListener(AutoRefresher.OnRefreshStateListener listener) {
        imageRefresher.setOnViewRefreshStateListener(listener);
    }

    public ImageDownloader getImageDownloader() {
        return imageRefresher.getImageDownloader();
    }

    public ImageViewHolder getImageViewHolder() {
        return imageRefresher.getImageViewHolder();
    }

    public ImageViewAutoRefreshed getImageRefresher() {
        return imageRefresher;
    }

    /**
     * Assuming we get the first one in the list
     *
     * @return
     */
    public Object getPreviewItem() {
        return imageRefresher.getImageItem(0);
    }

    public List getPreviewItemList() {
        return imageRefresher.getImageList();
    }

    /**
     *
     * @return
     */
    public boolean isUseGlide() {
        return useGlide;
    }

    /**
     *
     * @param useGlide
     */
    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;

        if (null != imageRefresher)
            imageRefresher.setUseGlide(useGlide);
    }

    /**
     *
     */
    public void updateImage() {
        imageRefresher.updateTarget();
    }

    /**
     *
     */
    public void clear() {
        imageRefresher.clear();
    }

    /**
     *
     * @param cacheLifespan
     */
    public void setCacheLifespan(long cacheLifespan) {
        imageRefresher.getImageDownloader().setCacheSpan(cacheLifespan);
    }

    /**
     *
     * @param angle
     * @param x
     * @param y
     */
    public void rotate(float angle, int x, int y) {
        Matrix matrix = new Matrix();
        ImageView imageView = getImageViewHolder().getImageView();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate((float) angle, x, y);
        imageView.setImageMatrix(matrix);
    }
}
