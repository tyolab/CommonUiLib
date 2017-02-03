package au.com.tyo.common.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.android.services.ImageDownloader;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

public class ImageViewAutoRefreshed extends ImageView {

    private static final String LOG_TAG = "ImageViewAutoRefreshed";

    public int NEXT_IMAGE_TIMEOUT = 5000; // 5 seconds;

    public interface OnImageRefreshStateListener {
        void onEachRoundFinished();
    }

    private OnImageRefreshStateListener listener;

    public static interface ImageItem {
        String getImageUrl();
        int getTimeout();
        CharSequence getAlt();
    }

    private List images;

	/**
	 * index of current showing image
	 */
	protected int current = -1;

    /**
     *
     */
    private int timeout = -1;

    /**
     * Image downloader
     */
    private ImageDownloader imageDownloader;

    private Handler handler;

    /**
	 * @param context
	 */
	public ImageViewAutoRefreshed(Context context) {
		super(context);
        init(context);
	}

    /**
	 * @param context
	 * @param attrs
	 */
	public ImageViewAutoRefreshed(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);

	}
	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public ImageViewAutoRefreshed(Context context, AttributeSet attrs, int defStyleAttr) {
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
	public ImageViewAutoRefreshed(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

    private void init(Context context) {
        images = null;
        imageDownloader = new ImageDownloader(context, "images");
        listener = null;
    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setImageQuality(int quality) {
        imageDownloader.setQuality(quality);
    }

    public void setOnImageRefreshStateListener(OnImageRefreshStateListener listener) {
        this.listener = listener;
    }

    public void addImage(Object item) {
        if (null == images)
            images = new ArrayList();

        images.add(item);
        current = images.size() - 1;
        try {
            updateImage(current);
        }
        catch (Exception ex) {}
    }

    public Object getCurrentImage() {
        return null != images ? images.get(current) : null;
    }

    /**
	 *
	 * @param images
     */
	public void setImages(List images) {
		this.images = images;
	}

    /**
     *
     * @param timeout, in millisecond
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        ++current;
        current %= images.size();
        try {
            updateImage(current);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        }
    };

    /**
     * Start auto refreshing images
     */
    public void display() throws Exception {
        if (null != images && images.size() > 0) {
            current = 0;

            if (images.size() > 1) {
                handler = new Handler();
            }
            updateImage(current);
        }
        else {
            if (null != listener)
                listener.onEachRoundFinished();
        }
    }

    private void updateImage(int current) throws Exception {
        Object item = images.get(current);

        String url;

        // timeout
        int to = timeout > NEXT_IMAGE_TIMEOUT ? timeout : NEXT_IMAGE_TIMEOUT;

        if (item instanceof String)
            url = (String) item;
        else if (item instanceof ImageItem) {
            ImageItem imageItem = (ImageItem) item;
            url = (imageItem).getImageUrl();
            to = imageItem.getTimeout() > to ? imageItem.getTimeout() : to;
        }
        else {
            if (null != listener)
                listener.onEachRoundFinished();
            throw new Exception("Image item must be a String type or a type implementing interface ImageItem");
        }

        if (null == url)
            return;

        imageDownloader.download(url, this);

        if (null != handler)
            handler.postDelayed(runnable, to);

        if (null != listener && current >= (images.size() - 1))
            listener.onEachRoundFinished();
    }

    public void pause() {
        handler = null;
    }

    public int getCurrentImageIndex() {
        return current;
    }
}
