package com.rollingscenes.src;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rollingscenes.R;
import com.rollingscenes.src.adapter.EventsAdapter;
import com.rollingscenes.src.asynctasks.GetEvents;
import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.interfaces.GetEventsLoaderIntf;
import com.rollingscenes.src.utils.ConnectionDetectorUtils;
import com.rollingscenes.src.utils.KeyConstants;
import com.todddavies.components.progressbar.ProgressWheel;

public class EventsFragment extends Fragment implements GetEventsLoaderIntf {
	private static final String SAVED_STATE_URL = null;
	private static final String TAG = EventsFragment.class.getSimpleName();
	private ArrayList<RSEvent> events;
	private ListView eventList;
	private EventsAdapter eventsAdapter;
	private Context context;
	private TextView txtNoEventsAvailable;
	private String url;
	private RelativeLayout relSpinner;
	private ProgressWheel spinner;
	private boolean loadingEvents;
	private Activity activity;
	public EventsFragment() {
	}

	public EventsFragment(String url, Context context) {
		super();
		this.context = context;
		this.url = url;
		events = new ArrayList<RSEvent>();
		if (url != null) {
			if (new ConnectionDetectorUtils(context).isConnectedToInternet()) {
				loadingEvents = true;
				Log.i(TAG, "calling get events for URl :" + url);
				int corePoolSize = 60;
				int maximumPoolSize = 80;
				int keepAliveTime = 10;

				BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
				Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
				new GetEvents(this, true).executeOnExecutor(threadPoolExecutor, url);
			} 
			else {
				Toast.makeText(context, KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void updateEvents(String url) {
		if (url != null) {
			if (new ConnectionDetectorUtils(context).isConnectedToInternet()) {
				int corePoolSize = 60;
				int maximumPoolSize = 80;
				int keepAliveTime = 10;

				BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
				Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
				new GetEvents(this, true).executeOnExecutor(threadPoolExecutor, url);
			} 
			else {
				Toast.makeText(context,
						KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			events = new ArrayList<RSEvent>();
			url = savedInstanceState.getString(SAVED_STATE_URL);
			context = getActivity().getApplicationContext();
			if (url != null) {
				if (new ConnectionDetectorUtils(context).isConnectedToInternet()) {
					loadingEvents = true;
					Log.i(TAG, "calling get events for URl :" + url);
					int corePoolSize = 60;
					int maximumPoolSize = 80;
					int keepAliveTime = 10;

					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
					Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
					new GetEvents(this, true).executeOnExecutor(threadPoolExecutor, url);
				} 
				else {
					Toast.makeText(context, KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
				}
			}
		}


		View rootView = inflater.inflate(R.layout.fragment_event_list,
				container, false);

		activity = getActivity();
		eventList = (ListView) rootView.findViewById(R.id.lst_adds);
		eventsAdapter = new EventsAdapter(activity.getApplicationContext(), events);
		eventList.setAdapter(eventsAdapter);

		eventList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyAppApplication.setTransientEventFromEventListToEventDesc(events.get(position));
				Intent intent = new Intent(activity, EventDescActivity.class);
				startActivity(intent);
			}
		});
		txtNoEventsAvailable = ((TextView) rootView.findViewById(R.id.txt_no_events));

		spinner = (ProgressWheel) rootView.findViewById(R.id.pw_spinner);
		relSpinner = (RelativeLayout) rootView.findViewById(R.id.rel_progress_wheel);
		if (loadingEvents) {
			showProgressWheel();
		}

		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		notifyAdapter();
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
	
	@Override
	public void eventsLoaded(final ArrayList<RSEvent> events) {
		this.events.clear();
		this.events.addAll(events);
		loadingEvents = false;
		if(activity != null) {
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (eventsAdapter != null) {
						hideProgressWheel();
						if (events.size() == 0) {
							txtNoEventsAvailable.setVisibility(View.VISIBLE);
						} 
						else {
							txtNoEventsAvailable.setVisibility(View.GONE);
						}
						notifyAdapter();
					}
				}
			});
		}
		new LoadEventsImages().loadAllImages();				
	}

	private void showProgressWheel() {
		relSpinner.setVisibility(View.VISIBLE);
		spinner.spin();

	}

	private void hideProgressWheel() {
		spinner.stopSpinning();
		relSpinner.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(SAVED_STATE_URL, url);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void errorInLoadingEvents(String error) {
		Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
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
