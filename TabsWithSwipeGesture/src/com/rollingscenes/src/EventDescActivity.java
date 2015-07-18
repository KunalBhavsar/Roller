package com.rollingscenes.src;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rollingscenes.R;
import com.rollingscenes.slider.library.SliderLayout;
import com.rollingscenes.slider.library.SliderTypes.BaseSliderView;
import com.rollingscenes.slider.library.SliderTypes.TextSliderView;
import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSDatetime;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.utils.ConnectionDetectorUtils;
import com.rollingscenes.src.utils.KeyConstants;



public class EventDescActivity extends FragmentActivity implements BaseSliderView.OnSliderClickListener {
	
	private static final String TAG = EventDescActivity.class.getSimpleName();
	private static final String THIS_ACTIVITY = "Event Description Activity";
	private RSEvent eventSelected;
	private GoogleMap map;
	private Typeface helveticaLight;
	private TabHost mTabHost;
	@SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_desc);
        
		// Remove the action bar
		getActionBar().hide();
        
		eventSelected = MyAppApplication.getTransientEventFromEventListToEventDesc();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if(eventSelected == null) {
			finish();
			return;
		}
		else {
			MyAppApplication.setTransientEventFromEventListToEventDesc(null);
		}
		
		SliderLayout mDemoSlider = (SliderLayout)findViewById(R.id.slider);
		if(eventSelected.isImagesDownloaded()) {
	        for(RSImage image : eventSelected.getImages()) {
	            TextSliderView textSliderView = new TextSliderView(this);
	
	            // initialize a SliderLayout
	            textSliderView.description(eventSelected.getName())
	            .setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(EventDescActivity.this);
	
	            File file = new File(image.getLocalImagePath());
	            if(file.exists()) {
	            	textSliderView.image(file);
	            }
	            else {
	            	textSliderView.image(R.drawable.events);	            	
	            }
	            //add your extra information
	            textSliderView.getBundle().putString("extra",eventSelected.getName());
	
	            mDemoSlider.addSlider(textSliderView);
	        }
		}
/*		else {
			for(RSImage image : eventSelected.getImages()) {
	            TextSliderView textSliderView = new TextSliderView(this);
	
	            // initialize a SliderLayout
	            textSliderView.description(eventSelected.getName()).image(image.getServerImagePath().replaceAll(" ", "%20"))
	            .setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(EventDescActivity.this);
	
	            //add your extra information
	            textSliderView.getBundle().putString("extra",eventSelected.getName());
	
	            mDemoSlider.addSlider(textSliderView);
	        }
		}*/
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
       
        
		helveticaLight = Typeface.createFromAsset(getAssets(),"fonts/helvetica_light.otf");
		Typeface helveticaRoman = Typeface.createFromAsset(getAssets(),"fonts/helvetica_roman.otf");
		Typeface helveticaBold = Typeface.createFromAsset(getAssets(),"fonts/helvetica_bold.otf");
		
		TextView addTitle = (TextView)findViewById(R.id.txt_add_title);
		addTitle.setTypeface(helveticaBold);
		
		((TextView)findViewById(R.id.txt_add_synopsis)).setTypeface(helveticaRoman);
		((TextView)findViewById(R.id.txt_add_category)).setTypeface(helveticaRoman);
		((TextView)findViewById(R.id.txt_add_cost)).setTypeface(helveticaRoman);
		((TextView)findViewById(R.id.txt_add_venue)).setTypeface(helveticaRoman);
		
		TextView addVenue = (TextView)findViewById(R.id.txt_add_venue_name);
		addVenue.setTypeface(helveticaLight);
		TextView addDesc = (TextView)findViewById(R.id.txt_add_synopsis_detail);
		addDesc.setTypeface(helveticaLight);
		TextView addCost = (TextView)findViewById(R.id.txt_add_cost_value);
		addCost.setTypeface(helveticaLight);
		TextView addCategory = (TextView)findViewById(R.id.txt_add_category_name);
		addCategory.setTypeface(helveticaLight);
		
        addTitle.setText(eventSelected.getName());
		addVenue.setText(eventSelected.getVenue());
		addDesc.setText(eventSelected.getOverview());
		addCategory.setText(eventSelected.getCategoryName());
		addCost.setText("₹ "+eventSelected.getCost());
		
		ArrayList<String> dates = new ArrayList<>(); 
		SimpleDateFormat formatterForDisplay = new SimpleDateFormat("dd MMM, yyyy\n hh:mm a", Locale.ENGLISH);
		
		Calendar cal = Calendar.getInstance();
		Date today = new Date(cal.get(Calendar.YEAR)-1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		Date tomorrow = null;
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
		
		ArrayList<RSDatetime> datetimes = eventSelected.getDatetimes();
		for(int i = 0; i < datetimes.size(); i++) {
			Date date = new Date(datetimes.get(i).getStartTime());
			
			if(date.equals(today)){
				dates.add(KeyConstants.TODAY);
			}
			else if(date.equals(tomorrow)) {
				dates.add(KeyConstants.TOMORROW);
			}
			else {
				dates.add(formatterForDisplay.format(date));
			}
		}
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				mTabHost.setCurrentTabByTag(tabId);
			}
		});
		
		for (Iterator<String> iterator = dates.iterator(); iterator.hasNext();) {
			String date = iterator.next();
			addTabToTabHost(date);
		}
		
		setColor(Color.parseColor(eventSelected.getColor()));
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }
        else { // Google Play Services are available
        	
         	((RelativeLayout)findViewById(R.id.rel_map)).setVisibility(View.GONE);
			if (new ConnectionDetectorUtils(getApplicationContext()).isConnectedToInternet()) {
				if(eventSelected.getLatitude() != 0 && eventSelected.getLongitude() != 0) {
					showMap(new LatLng(eventSelected.getLatitude(), eventSelected.getLongitude()));
				}
				else {
					int corePoolSize = 60;
					int maximumPoolSize = 80;
					int keepAliveTime = 10;
	
					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
					Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	
		        	new GetLocationFromAddress(getApplicationContext()).executeOnExecutor(threadPoolExecutor, eventSelected.getLocation());
				}
			}
        }

		MyAppApplication.sendTrackerAndEventInfo(getApplicationContext(), THIS_ACTIVITY, eventSelected.getCategoryName(), eventSelected.getName());
		
    }
	
	private void setColor(int color) {
		((RelativeLayout)findViewById(R.id.rel_add_title)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_venue)).setTextColor(color);
		((View)findViewById(R.id.view_below_venue)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_cost)).setTextColor(color);
		((View)findViewById(R.id.view_below_cost)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_category)).setTextColor(color);
		((View)findViewById(R.id.view_below_category)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_synopsis)).setTextColor(color);
		((View)findViewById(R.id.view_below_synopsis)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_date)).setTextColor(color);
		((View)findViewById(R.id.view_below_date)).setBackgroundColor(color);
		((TextView)findViewById(R.id.txt_add_location)).setTextColor(color);
	}
	
	private void addTabToTabHost(String tag) {
		View tabview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.tab_layout, null);
		RelativeLayout rl = (RelativeLayout) tabview.findViewById(R.id.tabsLayout);
		rl.setBackgroundColor(Color.parseColor(eventSelected.getColor()));
		TextView tv = (TextView) tabview.findViewById(R.id.tabsText);
		tv.setTypeface(helveticaLight);
		tv.setText(tag);

    	TabSpec tabSpec = mTabHost.newTabSpec(tag)
    			.setIndicator(tag)
    			.setIndicator(tabview)
    			.setContent(new TabFactory(EventDescActivity.this));

    	mTabHost.addTab(tabSpec);	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			default :
				return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }
    
    	
	class GetLocationFromAddress extends AsyncTask<String, Void, LatLng> {
		Geocoder geocoder;
		
		public GetLocationFromAddress(Context context) {
			 geocoder = new Geocoder(context, Locale.getDefault());
		}
		
		@Override
		protected LatLng doInBackground(String... params) {
			LatLng latLng = null;
			try {
				List<Address> geoResults = geocoder.getFromLocationName(params[0], 1);
				while (geoResults.size() == 0) {
			        geoResults = geocoder.getFromLocationName(params[0], 1);
			    }
			    if (geoResults.size() > 0) {
			        Address addr = geoResults.get(0);
			        latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
			        if(eventSelected.isLocallyStored()) {
				        RSAppDbHelper.getInstance(getApplicationContext()).
				        updateLatLongOfEvent(eventSelected.getRemoteId(), addr.getLatitude(), addr.getLongitude());
			        }
			    }
			    else {
					Log.e(TAG, "No result for :"+params[0]);
			    }
			}
			catch (IOException e) {
				Log.e(TAG, "Unable connect to Geocoder :", e);
			} 
			return latLng;
		}
		
		@Override
		protected void onPostExecute(LatLng result) {
			showMap(result);
		}
	}
	
	private void showMap(LatLng latLng) {
		if(latLng != null) {
			map.addMarker(new MarkerOptions().position(latLng));
			if(map != null){
				((RelativeLayout)findViewById(R.id.rel_map)).setVisibility(View.VISIBLE);
				map.getUiSettings().setZoomControlsEnabled(true);
				map.getUiSettings().setMapToolbarEnabled(true);
				map.getUiSettings().setCompassEnabled(true);
				map.getUiSettings().setAllGesturesEnabled(false);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
			}
		}
	}
}

