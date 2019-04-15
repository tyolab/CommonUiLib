package au.com.tyo.common.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import au.com.tyo.android.services.ImageDownloader;

/**
 * Created by "Eric Tang (dev@tyo.com.au)" on 14/1/17.
 */

public class ImageViewAutoRefreshed extends AutoRefresher {

    private static final String LOG_TAG = "ImageViewAutoRefreshed";

    public int NEXT_IMAGE_TIMEOUT = 5000; // 5 seconds;

    private ImageViewHolder imageViewHolder;

    private boolean useGlide = false;

    private Drawable defaultImage;

    protected List images;

    public interface ImageItem {
        String getImageUrl();
        Drawable getDrawable();
        int getTimeout();
        CharSequence getAlt();
    }

    /**
     * Image downloader
     */
    private ImageDownloader imageDownloader;

    public ImageViewAutoRefreshed(Context context) {
        init(context);
    }

    private void init(Context context) {
        images = null;
        imageDownloader = new ImageDownloader(context, "images");
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

    /**
     * error: cannot access Cache
     * class file for au.com.tyo.io.Cache not found
     *
     * @param quality
     */
    public void setImageQuality(int quality) {
        imageDownloader.setQuality(quality);
    }

    public boolean isUseGlide() {
        return useGlide;
    }

    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;
    }

    /**
     * Start auto refreshing images
     */
    public void display() {
        start();
    }

    protected void updateTarget(int current) throws Exception {
        Object item = images.get(current);
        int to = NEXT_IMAGE_TIMEOUT;
        if (null != item) {
            ImageView imageView = imageViewHolder.getImageView();

            if (item instanceof Uri) {
                imageView.setImageURI((Uri) item);
            }
            else {
                String url = null;
                Drawable drawable = null;

                // timeout
                to = timeout > NEXT_IMAGE_TIMEOUT ? timeout : NEXT_IMAGE_TIMEOUT;

                if (item instanceof Drawable)
                    drawable = (Drawable) item;
                else if (item instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) item;
                    drawable = new BitmapDrawable(bitmap);
                } else if (item instanceof String)
                    url = (String) item;
                else if (item instanceof ImageItem) {
                    ImageItem imageItem = (ImageItem) item;
                    drawable = imageItem.getDrawable();
                    url = (imageItem).getImageUrl();
                    to = imageItem.getTimeout() > to ? imageItem.getTimeout() : to;
                } else if (item instanceof Integer) {
                    // resource id
                    drawable = imageView.getContext().getResources().getDrawable((Integer) item);
                } else {
                    onRefreshRoundFinished(to);
                    throw new IllegalStateException("Image item must be a String type or a type implementing interface ImageItem");
                }

                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
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
                    Glide.with(imageView.getContext())
                            .load(url)
                            //.error(defaultImage)
                            .into(imageView);
                } else
                    imageDownloader.fetch(url, imageView);
            }
        }

        super.updateTarget(current);
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

        if (null != getImageViewHolder().getImageView())
            getImageViewHolder().getImageView().setImageBitmap(null);
    }

    public int getImagesCount() {
        return images != null ? images.size() : 0;
    }

    public List getImageList() {
        return images;
    }

    public void addImage(Object item) {
        if (null == item)
            return;

        if (null == images)
            images = new ArrayList();

        images.add(item);
        current = images.size() - 1;
        update();
    }

    /**
     *
     * @param images
     */
    public void setImages(List images) {
        this.images = images;

        update();
    }

    @Override
    public int getRefreshTimes() {
        return null == images ? 0 : images.size();
    }

    public Object getCurrentImage() {
        return null != images ? images.get(current) : null;
    }
}
