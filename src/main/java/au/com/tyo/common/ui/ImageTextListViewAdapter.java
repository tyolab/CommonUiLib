package au.com.tyo.common.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import au.com.tyo.android.images.utils.BitmapUtils;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.android.adapter.ListViewItemAdapter;
import au.com.tyo.common.model.ImageTextListItem;

public class ImageTextListViewAdapter extends ListViewItemAdapter {

	protected boolean hideImageViewWhenImageUnavaiable;

	private ImageDownloader imageDownloader;

	public ImageTextListViewAdapter() {
		this(null, false);
	}

	public ImageTextListViewAdapter(ArrayList items, boolean hideImageViewWhenImageUnavaiable) {
		this(items, hideImageViewWhenImageUnavaiable, R.layout.image_text_list_cell_for_all);
	}
	
	public ImageTextListViewAdapter(ArrayList items, boolean hideImageViewWhenImageUnavaiable, int resId) {
		super(resId, items);
		this.hideImageViewWhenImageUnavaiable = hideImageViewWhenImageUnavaiable;
	}
	
	public synchronized void setImageDownload(ImageDownloader imageDownload) {
		this.imageDownloader = imageDownload;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageTextListItem page = (ImageTextListItem) items.get(position);
		
        convertView = super.getView(position, convertView, parent);
        
	    ImageView image = (ImageView) convertView.findViewById(R.id.itl_image_view);
	    
	    if (page.getImgBytes() != null) {
	    	image.setImageBitmap(BitmapUtils.bytesToBitmap(page.getImgBytes()));
	    }
	    else {
	        String link = page.getThumbnailLink();
	        if (link != null) {
	            int pos;
	            if ((pos = link.indexOf("file://")) > -1)
	            	link = link.substring(pos + 7);
		        	
	            imageDownloader.fetch(link, image);
	        }
	        else {
	        	if (hideImageViewWhenImageUnavaiable)
	        		image.setVisibility(View.GONE);
	        	else
	        		setDefaultImage();
//	        	else {
//	        		image.setImageResource(R.drawable.ic_image);
//	        		image.setBackgroundResource(R.color.theme_dark_background_color);
//	        	}
	        }
	    }
        	
//        TextView tvTitle = (TextView) convertView.findViewById(R.id.itl_text);
//        tvTitle.setText(page.getTitle());
        
        return convertView;
	}

	public void setDefaultImage() {
	}
}