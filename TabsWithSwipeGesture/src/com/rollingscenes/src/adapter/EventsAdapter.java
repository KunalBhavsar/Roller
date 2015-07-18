package com.rollingscenes.src.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuubonandroid.sugaredlistanimations.GPlusListAdapter;
import com.cuubonandroid.sugaredlistanimations.SpeedScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rollingscenes.R;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.utils.KeyConstants;

public class EventsAdapter extends GPlusListAdapter {

	private static final String TAG = null;
	private Typeface dsItalic;
	private Typeface dsRegular;
	private Typeface helveticaBold;
	//private String[] hashtags;
	private ArrayList<RSEvent> events;
	private Context context;
	private Date today;
	private Date tomorrow;
	private SimpleDateFormat formatterForDisplay;
	
	class AddsAdapterViewholder {
		ImageView eventBg;
		TextView imageLoadingText;
		/*TextView eventHashtag1;
		TextView eventHashtag2;
		TextView eventHashtag3;*/
		TextView eventName;
		TextView eventCategory;
		TextView veneuName;
		TextView areaName;
		TextView eventTime;
		TextView eventCost;
		View blankSpace;
	}
	@SuppressWarnings("deprecation")
	public EventsAdapter(Context context, ArrayList<RSEvent> events) {
		super(context, new SpeedScrollListener(), events);
		this.events = events;
		this.context = context;
		helveticaBold = Typeface.createFromAsset(context.getAssets(),"fonts/helvetica_bold.otf");
		dsRegular = Typeface.createFromAsset(context.getAssets(),
				"fonts/ds_regular.ttf");
		dsItalic = Typeface.createFromAsset(context.getAssets(),
				"fonts/ds_bolditalic.ttf");
		
		formatterForDisplay = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
		
		Calendar cal = Calendar.getInstance();
		today = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		tomorrow = null;
		if(cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			if(cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
				tomorrow = new Date(cal.get(Calendar.YEAR)-1900+1, 0, 1);
			}
			else {
				tomorrow = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH)+1, 1);
			}
		}
		else {
			tomorrow = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1);
		}
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
		ImageLoader.getInstance().init(configuration);
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	protected View getRowView(int position, View convertView, ViewGroup parent) {
		final AddsAdapterViewholder viewHolder;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event, null);
			viewHolder = new AddsAdapterViewholder();
			viewHolder.eventBg = (ImageView)convertView.findViewById(R.id.img_event_background);
			viewHolder.imageLoadingText = (TextView)convertView.findViewById(R.id.txt_image_loading);
			/*viewHolder.eventHashtag1 = (TextView)convertView.findViewById(R.id.txt_event_hashtag1);
			viewHolder.eventHashtag2 = (TextView)convertView.findViewById(R.id.txt_event_hashtag2);
			viewHolder.eventHashtag3 = (TextView)convertView.findViewById(R.id.txt_event_hashtag3);*/
			viewHolder.eventName = (TextView)convertView.findViewById(R.id.txt_event_name);
			viewHolder.eventCategory = (TextView)convertView.findViewById(R.id.txt_event_category);
			viewHolder.veneuName = (TextView)convertView.findViewById(R.id.txt_venue_name);
			viewHolder.areaName = (TextView)convertView.findViewById(R.id.txt_area_name);
			viewHolder.eventTime = (TextView)convertView.findViewById(R.id.txt_event_time);
			viewHolder.eventCost = (TextView)convertView.findViewById(R.id.txt_event_cost);
			viewHolder.blankSpace = (View)convertView.findViewById(R.id.blank_space);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (AddsAdapterViewholder)convertView.getTag();
		}
		
		RSEvent event = (RSEvent)getItem(position);
		
		viewHolder.imageLoadingText.setTypeface(dsRegular);
		/*viewHolder.eventHashtag1.setTypeface(dsRegular);
		viewHolder.eventHashtag2.setTypeface(dsRegular);
		viewHolder.eventHashtag3.setTypeface(dsRegular);*/
		viewHolder.eventName.setTypeface(dsItalic);
		viewHolder.eventCategory.setTypeface(dsRegular);
		viewHolder.veneuName.setTypeface(helveticaBold);
		viewHolder.areaName.setTypeface(helveticaBold);
		viewHolder.eventTime.setTypeface(dsRegular);
		viewHolder.eventCost.setTypeface(dsRegular);
		
		if(event.isImagesDownloaded()) {
			String path = "";
			for (Iterator<RSImage> iterator = event.getImages().iterator(); iterator.hasNext();) {
				RSImage image = iterator.next();
				if(image.isPrimary()) {
					path = image.getLocalImagePath();
					break;
				}
			}
			viewHolder.imageLoadingText.setVisibility(View.INVISIBLE);
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			if(bitmap != null) {
				viewHolder.eventBg.setImageBitmap(bitmap);
			}
			else {
				viewHolder.eventBg.setImageDrawable(context.getResources().getDrawable(R.drawable.events));
			}			
		}
		else {
			viewHolder.imageLoadingText.setVisibility(View.VISIBLE);
			viewHolder.eventBg.setImageBitmap(null);
		}
		/*hashtags = event.getEvent_hashtags();
		if(hashtags != null) {
			if(hashtags[0] != null && !hashtags[0].isEmpty()) {
				viewHolder.eventHashtag1.setText("#"+hashtags[0]);
			}
			else {
				viewHolder.eventHashtag1.setText("");
			}
			if(hashtags[1] != null && !hashtags[1].isEmpty()) {
				viewHolder.eventHashtag2.setText("#"+hashtags[1]);
			}
			else {
				viewHolder.eventHashtag2.setText("");
			}
			if(hashtags[2] != null && !hashtags[2].isEmpty()) {
				viewHolder.eventHashtag3.setText("#"+hashtags[2]);
			}
			else {
				viewHolder.eventHashtag3.setText("");
			}
		}*/
		
		if(event.getName() != null) {
			viewHolder.eventName.setText(" " + event.getName());
		}

		if(event.getCategoryName() != null) {
			viewHolder.eventCategory.setText(" " + event.getCategoryName()+" ");
		}
		
		if(event.getVenue() != null) {
			viewHolder.veneuName.setText(" " + event.getVenue()+",");
		}
		
		if(event.getArea() != null) {
			viewHolder.areaName.setText(event.getArea());
		}
		
		Date date = new Date(event.getDatetimes().get(0).getStartTime());
		
		if(date.equals(today)){
			viewHolder.eventTime.setText(KeyConstants.TODAY+" "+date.getHours()+":"+(date.getMinutes() == 0 ? "00" : date.getMinutes()));
		}
		else if(date.equals(tomorrow)) {
			viewHolder.eventTime.setText(KeyConstants.TOMORROW+" "+date.getHours()+":"+(date.getMinutes() == 0 ? "00" : date.getMinutes()));
		}
		else {
			viewHolder.eventTime.setText(formatterForDisplay.format(date)+" "+date.getHours()+":"+(date.getMinutes() == 0 ? "00" : date.getMinutes()));
		}

		viewHolder.eventCost.setText("₹"+event.getCost());
		
		return convertView;
	}
}
