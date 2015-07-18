package com.rollingscenes.src.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.interfaces.ImageListner;

public class LoadEventsImages extends ImageLoader implements
		ImageLoadingListener {
	private static final int IMAGE_SIZE = 250;

	private static ImageSize imageSize;

	private ArrayList<RSEvent> events;
	private int eventIndex;
	private int imageIndex;
	private ImageListner listener;
	private Context context;

	public void loadAllImages(ArrayList<RSEvent> events, ImageListner listener, Context context) {
		this.events = events;
		this.listener = listener;
		this.context = context;
		
		eventIndex = 0;
		imageIndex = 0;
		
		if(imageSize != null) {
			imageSize = new ImageSize(IMAGE_SIZE, IMAGE_SIZE);
		}
		
		super.init(ImageLoaderConfiguration.createDefault(context));
		
		loadImagesOfEvent();
	}
	
	private void loadImagesOfEvent() {
		if(events.size() > eventIndex) {
			RSEvent event = events.get(eventIndex);
			boolean primaryPresent = false;
			for (Iterator<RSImage> iterator = event.getImages().iterator(); iterator.hasNext();) {
				if(iterator.next().isPrimary()) {
					primaryPresent = true;
					break;
				}
			}
			if(!primaryPresent) {
				event.setImagesDownloaded(true);
			}
			if(!event.isImagesDownloaded() && events.get(eventIndex).getImages().size() > 0) {
				super.loadImage(event.getImages().get(imageIndex).getServerImagePath(), imageSize, this);
			}
			else {
				loadImagesForNextEvent();
			}
		}
		else {
			ImageLoader.getInstance().stop();
		}
	}
	
	private void loadImagesForNextEvent() {
		eventIndex++;
		imageIndex = 0;
		loadImagesOfEvent();
	}
	
	private void loadNextImage() {
		imageIndex++;
		ArrayList<RSImage> images = events.get(eventIndex).getImages();
		if(images.size() > imageIndex) {
			super.loadImage(images.get(imageIndex).getServerImagePath(), imageSize, this);
		}
		else {
			loadImagesForNextEvent();
		}
	}
	
	@Override
	public void onLoadingStarted(String imageUri, View view) {
		
	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {
		RSEvent event = events.get(eventIndex);
		RSImage image = event.getImages().get(imageIndex);
		if(image.isPrimary()) {
		    event.setImagesDownloaded(true);
		    if(event.isLocallyStored()) {
		    	RSAppDbHelper.getInstance(context).updateImagesDownloadedFlagOfEvent(event.getRemoteId(), true);
		    }
		    listener.imageDownloaded();
		}
		loadNextImage();
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		RSEvent event = events.get(eventIndex);
		RSImage image = event.getImages().get(imageIndex);
		File imageFile = new File(image.getLocalImagePath());
		try {
		    // Use the compress method on the Bitmap object to write image to
		    // the OutputStream
		    FileOutputStream fos = new FileOutputStream(imageFile);

		    // Writing the bitmap to the output stream
		    loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		    fos.close();
	    } 
		catch (Exception e) {
		    Log.e("Error in storing image : ", e.getMessage());
		}
		if(image.isPrimary()) {
		    event.setImagesDownloaded(true);
		    if(event.isLocallyStored()) {
			    RSAppDbHelper.getInstance(context).updateImagesDownloadedFlagOfEvent(event.getRemoteId(), true);
		    }
		    listener.imageDownloaded();
		}
		loadNextImage();
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		Log.e(TAG,"Error in downloading image cancelled");
	}
}
