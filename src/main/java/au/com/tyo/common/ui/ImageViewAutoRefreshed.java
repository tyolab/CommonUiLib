package au.com.tyo.common.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import au.com.tyo.io.Cache;

import au.com.tyo.android.services.ImageDownloader;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

public class ImageViewAutoRefreshed {

    private static final String LOG_TAG = "ImageViewAutoRefreshed";

    public int NEXT_IMAGE_TIMEOUT = 5000; // 5 seconds;

    private ImageViewHolder imageViewHolder;

    private boolean useGlide = false;

    private Drawable defaultImage;

    public int getImagesCount() {
        return images != null ? images.size() : 0;
    }

    public List getImageList() {
        return images;
    }

    public interface OnImageRefreshStateListener {
        void onEachRoundFinished(int timeout);
    }

    private OnImageRefreshStateListener listener;

    public interface ImageItem {
        String getImageUrl();
        Drawable getDrawable();
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

    public ImageViewAutoRefreshed(Context context) {
        init(context);
    }

    private void init(Context context) {
        images = null;
        imageDownloader = new ImageDownloader(context, "images");
        listener = null;
        defaultImage = null;
    }

    public ImageViewHolder getImageViewHolder() {
        return imageViewHolder;
    }

    public void setImageViewHolder(ImageViewHolder imageViewHolder) {
        this.imageViewHolder = imageViewHolder;
    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * error: cannot access Cache
     * class file for au.com.tyo.io.Cache not found
     *
     * @param quality
     */
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
        update();
    }

    public void updateImage() {
        if (images.size() == 0)
            return;

        if (current < -1 && current >= images.size())
            current = 0;

        update();
    }

    private void update() {
        try {
            updateImage(current);
        }
        catch (Exception ex) { Log.e(LOG_TAG, "error in updating the image"); }
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

        update();
	}

    /**
     *
     * @param timeout, in millisecond
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isUseGlide() {
        return useGlide;
    }

    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        ++current;
        current %= images.size();
        try {
            if (null != handler)
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
                listener.onEachRoundFinished(0);
        }
    }

    private void updateImage(int current) throws Exception {
        Object item = images.get(current);
        int to = NEXT_IMAGE_TIMEOUT;
        if (null != item) {

            String url = null;
            Drawable drawable = null;

            // timeout
            to = timeout > NEXT_IMAGE_TIMEOUT ? timeout : NEXT_IMAGE_TIMEOUT;

            if (item instanceof Drawable)
                drawable = (Drawable) item;
            else if (item instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) item;
                drawable = new BitmapDrawable(bitmap);
            }
            else if (item instanceof String)
                url = (String) item;
            else if (item instanceof ImageItem) {
                ImageItem imageItem = (ImageItem) item;
                drawable = imageItem.getDrawable();
                url = (imageItem).getImageUrl();
                to = imageItem.getTimeout() > to ? imageItem.getTimeout() : to;
            }
            else if (item instanceof Integer) {
                // resource id
                drawable = imageViewHolder.getImageView().getContext().getResources().getDrawable((Integer) item);
            }
            else {
                if (null != listener)
                    listener.onEachRoundFinished(to);
                throw new IllegalStateException("Image item must be a String type or a type implementing interface ImageItem");
            }

            if (drawable != null) {
                imageViewHolder.getImageView().setImageDrawable(drawable);
                return;
            }

            if (null == url)
                return;

            if (url.contains("%")) {
                String oldUrl = url;
                try {
                    url = URLDecoder.decode(url, "UTF-8");
                } catch (Exception ex) {
                    url = oldUrl;
                }
            }

            if (useGlide) {
                Glide.with(imageViewHolder.getImageView().getContext())
                        .load(url)
                        //.error(defaultImage)
                        .into(imageViewHolder.getImageView());
            } else
                imageDownloader.fetch(url, imageViewHolder.getImageView());


            if (null != handler)
                handler.postDelayed(runnable, to);
        }

        if (null != listener && current >= (images.size() - 1))
            listener.onEachRoundFinished(to);
    }

    public void pause() {
        handler = null;

        /**
         * It seems that it is not a good idea to recycle bitmap here
         */
        /** example recycle code
        ImageView imageView = imageViewHolder.getImageView();
        if (null != imageView) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();
            }
        }
        */
    }

    public int getCurrentImageIndex() {
        return current;
    }

    public Object getImageItem(int i) {
        if (images.size() > 0 && images.size() > i)
            return images.get(i);
        return null;
    }

    public void clear() {
        if (null != images)
            images.clear();
    }
}
