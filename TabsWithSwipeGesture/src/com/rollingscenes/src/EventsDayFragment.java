package com.rollingscenes.src;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rollingscenes.R;
import com.rollingscenes.src.adapter.EventsAdapter;
import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.utils.KeyConstants;
import com.todddavies.components.progressbar.ProgressWheel;

public class EventsDayFragment extends Fragment {
	
	private static final int LATER_EVENTS_DAY_RANGE = 5;
	private static final String SAVED_STATE_EVENT_DAY = "event_day";
	private ArrayList<RSEvent> events;
	private ListView eventList;
	private EventsAdapter eventsAdapter;
	private Context context;
	private TextView txtNoEventsAvailable;
	private String eventDay;
	private RelativeLayout relSpinner;
	private ProgressWheel spinner;
	private Activity activity;
	public EventsDayFragment() {}
	
	public EventsDayFragment(String eventDay, Context context) {
		super();
		this.context = context;
		this.eventDay = eventDay;
		events = new ArrayList<RSEvent>();
		if(eventDay.equals(KeyConstants.TODAY)) {
			events.addAll(RSAppDbHelper.getInstance(context).getTodaysEvents());
		}
		else if(eventDay.equals(KeyConstants.TOMORROW)) {
			events.addAll(RSAppDbHelper.getInstance(context).getTomorrowsEvents());
		}
		else if(eventDay.equals(KeyConstants.LATER)) {
			events.addAll(RSAppDbHelper.getInstance(context).getLatersEvents(LATER_EVENTS_DAY_RANGE));
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			events = new ArrayList<RSEvent>();	
			eventDay = savedInstanceState.getString(SAVED_STATE_EVENT_DAY);
			context = getActivity().getApplicationContext();
			if(eventDay.equals(KeyConstants.TODAY)) {
				events.addAll(RSAppDbHelper.getInstance(context).getTodaysEvents());
			}
			else if(eventDay.equals(KeyConstants.TOMORROW)) {
				events.addAll(RSAppDbHelper.getInstance(context).getTomorrowsEvents());
			}
			else if(eventDay.equals(KeyConstants.LATER)) {
				events.addAll(RSAppDbHelper.getInstance(context).getLatersEvents(LATER_EVENTS_DAY_RANGE));
			}
		}
		
		View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
		
		eventList = (ListView)rootView.findViewById(R.id.lst_adds);
		activity = getActivity();
		eventsAdapter = new EventsAdapter(activity.getApplicationContext(), events);
		eventList.setAdapter(eventsAdapter);
		
		eventList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				MyAppApplication.setTransientEventFromEventListToEventDesc(events.get(position));
				Intent intent = new Intent(activity, EventDescActivity.class);
				startActivity(intent);
			}
		});
		
		txtNoEventsAvailable = ((TextView)rootView.findViewById(R.id.txt_no_events));
		if(events.size() > 0) {
			txtNoEventsAvailable.setVisibility(View.GONE);
		}
		else {
			txtNoEventsAvailable.setVisibility(View.VISIBLE);
		}
		spinner = (ProgressWheel)rootView.findViewById(R.id.pw_spinner);
		relSpinner = (RelativeLayout)rootView.findViewById(R.id.rel_progress_wheel);
		
		new LoadEventsImages().loadAllImages();
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	public void notifyAdapter() {
		if(eventsAdapter != null) {
			eventsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		notifyAdapter();
	}
	
	public void showProgressWheel() {
		if(relSpinner != null) {
			txtNoEventsAvailable.setVisibility(View.GONE);
			relSpinner.setVisibility(View.VISIBLE);
			spinner.spin();
		}
	}
	
	public void hideProgressWheel() {
		if(relSpinner != null) {
			spinner.stopSpinning();
			relSpinner.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(SAVED_STATE_EVENT_DAY, eventDay);
		super.onSaveInstanceState(outState);
	}
		
	void updateEvents() {
		events.clear();
		if(eventDay.equals(KeyConstants.TODAY)) {
			events.addAll(RSAppDbHelper.getInstance(context).getTodaysEvents());
		}
		else if(eventDay.equals(KeyConstants.TOMORROW)) {
			events.addAll(RSAppDbHelper.getInstance(context).getTomorrowsEvents());
		}
		else if(eventDay.equals(KeyConstants.LATER)) {
			events.addAll(RSAppDbHelper.getInstance(context).getLatersEvents(LATER_EVENTS_DAY_RANGE));
		}
		if(txtNoEventsAvailable != null) {
			if(events.size() > 0) {
				txtNoEventsAvailable.setVisibility(View.GONE);
			}
			else {
				txtNoEventsAvailable.setVisibility(View.VISIBLE);
			}
		}
		notifyAdapter();
		new LoadEventsImages().loadAllImages();
	}

	class LoadEventsImages extends ImageLoader implements ImageLoadingListener {
		private static final int IMAGE_SIZE = 250;
		private ImageSize imageSize;
		private int eventIndex;
		private int imageIndex;

		public void loadAllImages() {
			eventIndex = 0;
			imageIndex = 0;

			if (imageSize != null) {
				imageSize = new ImageSize(IMAGE_SIZE, IMAGE_SIZE);
			}

			super.init(ImageLoaderConfiguration.createDefault(context));

			loadImagesOfEvent();
		}

		private void loadImagesOfEvent() {
			if(activity != null) {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						notifyAdapter();					
					}
				});
			}
			if (events.size() > eventIndex) {
				RSEvent event = events.get(eventIndex);
				boolean primaryPresent = false;
				for (Iterator<RSImage> iterator = event.getImages().iterator(); iterator.hasNext();) {
					if (iterator.next().isPrimary()) {
						primaryPresent = true;
						break;
					}
				}
				if (!primaryPresent) {
					event.setImagesDownloaded(true);
				}
				if (!event.isImagesDownloaded() && events.get(eventIndex).getImages().size() > 0) {
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
			if (images.size() > imageIndex) {
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
			if (image.isPrimary()) {
				event.setImagesDownloaded(true);
				if (event.isLocallyStored()) {
					RSAppDbHelper.getInstance(context)
						.updateImagesDownloadedFlagOfEvent(event.getRemoteId(), true);
				}
			}
			loadNextImage();
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			RSEvent event = events.get(eventIndex);
			RSImage image = event.getImages().get(imageIndex);
			File imageFile = new File(image.getLocalImagePath());
			try {
				// Use the compress method on the Bitmap object to write image
				// to
				// the OutputStream
				FileOutputStream fos = new FileOutputStream(imageFile);

				// Writing the bitmap to the output stream
				loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			} 
			catch (Exception e) {
				Log.e("Error in storing image : ", e.getMessage());
			}
			if (image.isPrimary()) {
				event.setImagesDownloaded(true);
				if (event.isLocallyStored()) {
					RSAppDbHelper.getInstance(context).
						updateImagesDownloadedFlagOfEvent(event.getRemoteId(), true);
				}
			}
			loadNextImage();
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			Log.e(TAG, "Error in downloading image cancelled");
		}
	}

}
