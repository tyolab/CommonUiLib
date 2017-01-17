package au.com.tyo.common.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

import au.com.tyo.android.services.ImageDownloader;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

public class ImageViewAutoRefreshed extends ImageView {

    public int NEXT_IMAGE_TIMEOUT = 5000; // 5 seconds;

    public static interface ImageItem {
        String getUrl();
        int getTimeout();
    }

    private ArrayList images;

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
	public ImageViewAutoRefreshed(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

    private void init(Context context) {
        images = null;
        imageDownloader = new ImageDownloader(context, "cache");
    }

    public int getTimeout() {
        return timeout;
    }

    /**
	 *
	 * @param images
     */
	public void setImages(ArrayList images) {
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
            updateImage(current);

            if (null != handler)
                handler.postDelayed(this, timeout);
        }
    };

    /**
     * Start auto refreshing images
     */
    public void display() throws Exception {
        if (null != images && images.size() > 0) {
            current = 0;

            updateImage(current);
        }
    }

    private void updateImage(int current) throws Exception {
        Object item = images.get(current);

        String url;

        // timeout
        int to = timeout > -1 ? timeout : NEXT_IMAGE_TIMEOUT;

        if (item instanceof String)
            url = (String) item;
        else (item instanceof ImageItem) {
            ImageItem imageItem = (ImageItem) item;
            url = (imageItem).getUrl();
            to = imageItem.getTimeout() > -1 ? imageItem.getTimeout() : timeout;
        }
        else
            throw new Exception("Image item must be a String type or a type implementing interface ImageItem");

        imageDownloader.download(url, this);

        if (images.size() > 1) {
            handler = new Handler();
            handler.postDelayed(runnable, to);
        }
    }

    public void pause() {
        handler = null;
    }

    public int getCurrentImageIndex() {
        return current;
    }
}
