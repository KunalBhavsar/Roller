package com.rollingscenes.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rollingscenes.R;
import com.rollingscenes.src.adapter.TabPagerAdapter;
import com.rollingscenes.src.utils.ConnectionDetectorUtils;
import com.rollingscenes.src.utils.KeyConstants;
import com.todddavies.components.progressbar.ProgressWheel;

public class SearchEventsActivity extends FragmentActivity {
	
	protected static final String TAG = SearchEventsActivity.class.getSimpleName();
	private static final String THIS_ACTIVITY = "Search Event Activity";
	private static final String BASE_URL_FOR_SEARCH = KeyConstants.BASE_URL_FOR_USER_CONTROLLER + "getSearchResults&city=mumbai&q=";
	
    protected TabHost mTabHost;
	private ViewPager viewPager;
	private TabPagerAdapter adapterPager;
    private ArrayList<TabInfo> tabInfos;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_without_nav_fragment);
		
		tabInfos = new ArrayList<TabInfo>();
		
		// Initilization
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		viewPager = (ViewPager) findViewById(R.id.pager);

		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5e35b1")));
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		adapterPager = new TabPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(adapterPager);

		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				mTabHost.setCurrentTabByTag(tabId);
				viewPager.setCurrentItem(mTabHost.getCurrentTab());
			}
		});
		
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				mTabHost.setCurrentTab(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		viewPager.setOffscreenPageLimit(3);
		
		addTabToTabHost(KeyConstants.TODAY, EventsFragment.class, new EventsFragment(null, getApplicationContext()));
		addTabToTabHost(KeyConstants.TOMORROW, EventsFragment.class, new EventsFragment(null, getApplicationContext()));
		addTabToTabHost(KeyConstants.LATER, EventsFragment.class, new EventsFragment(null, getApplicationContext()));

		mTabHost.setVisibility(View.INVISIBLE);

		MyAppApplication.sendTrackerInfo(getApplicationContext(), THIS_ACTIVITY);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_event_menu, menu);
		
		SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchViewMenuItem = menu.findItem(R.id.item_search_button);    
	    SearchView searchView = (SearchView)searchViewMenuItem.getActionView();		
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setQueryHint(getResources().getString(R.string.hint_search_text));
		
		int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
	    ImageView imageView = (ImageView)searchView.findViewById(searchImgId);
	    imageView.setAdjustViewBounds(true);
	    Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
	    imageView.setMaxHeight(display.getHeight()/12);
	    imageView.setMaxWidth(display.getWidth()/7);
	    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_actionbar));
	    
	    int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
	    EditText editText = (EditText)searchView.findViewById(searchTextId);
	    ((View)editText.getParent()).setBackgroundColor(Color.WHITE);
	    editText.setTextColor(Color.BLACK);
	    try {
	        // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
	        Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
	        f.setAccessible(true);
	        f.set(editText, R.drawable.cursor_drawable);
	    } 
	    catch (Exception ignored) {
	    	Log.e(TAG, "Error in changing the cusror color : "+ignored.getMessage());
	    }
	    
	    editText.setHintTextColor(Color.GRAY);
	    Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/helvetica_roman.otf");
		editText.setTypeface(tf);
		//editText.requestFocus();
		searchView.onActionViewExpanded();
		
		searchView.setOnQueryTextListener(new OnQueryTextListener() {	
			@Override
			public boolean onQueryTextSubmit(String queryString) {
				
				if(queryString.length() >= 1) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus()
											.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
					if (new ConnectionDetectorUtils(getApplicationContext()).isConnectedToInternet()) {
						try {
							queryString = URLEncoder.encode(queryString, "UTF-8");
							Log.i(TAG, "Encoded URL :"+queryString);
						} 
						catch (UnsupportedEncodingException e) {
							Log.e(TAG, "Error in encoding URL");
						}
						
						showProgressWheel();
						new GetSearchResults().execute(BASE_URL_FOR_SEARCH + queryString);
					} 
					else {
						Toast.makeText(getApplicationContext(), KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
			@Override
			public boolean onQueryTextChange(String arg0) {
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			default :
				onBackPressed();
				return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    protected void onDestroy() {
		ImageLoader.getInstance().stop();
		File tempImagesDir = new File(Environment.getExternalStorageDirectory(), KeyConstants.TEMP_IMAGE_DIRECTORY);
		
		if(tempImagesDir.exists() && tempImagesDir.isDirectory()) {
			for (File image : tempImagesDir.listFiles()) {
				image.delete();
			}
		}
    	super.onDestroy();
    }
    
	private void showProgressWheel() {
		((RelativeLayout)findViewById(R.id.rel_progress_wheel)).setVisibility(View.VISIBLE);
		((ProgressWheel)findViewById(R.id.pw_spinner)).spin();
		
	}
	
	private void hideProgressWheel() {
		((ProgressWheel)findViewById(R.id.pw_spinner)).stopSpinning();
		((RelativeLayout)findViewById(R.id.rel_progress_wheel)).setVisibility(View.INVISIBLE);
	}
	
	private class GetSearchResults extends AsyncTask<String, Void, ArrayList<SearchResult>> {
		
		@Override
		protected void onPreExecute() {
			mTabHost.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected ArrayList<SearchResult> doInBackground(String... params) {
			Log.e(TAG, "URL: "+params[0]);
			
			HttpURLConnection connection = null;
			int statusCode = 0;
			BufferedReader reader = null;
			ArrayList<SearchResult> searchResults = null;
			try {
				URL eventsUrl = new URL(params[0]);

				connection = (HttpURLConnection)eventsUrl.openConnection();
				connection.setRequestMethod(KeyConstants.GET_REQUEST_METHOD);
		        connection.setRequestProperty(KeyConstants.CONTENT_TYPE,KeyConstants.JSON_APPLICATION);
		        connection.setConnectTimeout(15000);
		        connection.setDoOutput(true);
				statusCode = connection.getResponseCode();
				if (statusCode == KeyConstants.GET_OK) {
					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));
					String line = "";
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					String responseData = sb.toString();
					Log.e(TAG, responseData);
					searchResults = new Gson().fromJson(responseData, new TypeToken<ArrayList<SearchResult>>() {}.getType());
				}
				else {
					reader = new BufferedReader(new InputStreamReader(
							connection.getErrorStream()));
					String line = "";
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Log.e(TAG, "Error occured with status code : "+statusCode+" "+sb.toString());
				}
			} 
			catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());
			}
			catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			
			return searchResults;
		}
		
		@Override
		protected void onPostExecute(ArrayList<SearchResult> results) {
			hideProgressWheel();
			if(results != null && results.size() > 0) {
				String searchedEventsGetUrl = KeyConstants.BASE_URL_FOR_USER_CONTROLLER + 
						"getEventBySearch&searchParam=" + results.get(0).area_name
						+"&tablename="+results.get(0).type+"&index=0&which_day=";
				TabInfo tabInfo = new TabInfo(SearchEventsActivity.this, KeyConstants.TODAY);
				((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(searchedEventsGetUrl+"today");
				tabInfo = new TabInfo(SearchEventsActivity.this, KeyConstants.TOMORROW);
				((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(searchedEventsGetUrl+"tomorrow");
				tabInfo = new TabInfo(SearchEventsActivity.this, KeyConstants.LATER);
				((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(searchedEventsGetUrl+"later");
				mTabHost.setVisibility(View.VISIBLE);
			}
			else {
				Toast.makeText(getApplicationContext(), "No matching results found...", Toast.LENGTH_SHORT).show();
			}
		}
	}
	class SearchResult {
		String area_name;
		String type;
	}
	
	@SuppressLint("InflateParams")
	private void addTabToTabHost(String tag, Class<?> classFragment, EventsFragment fragment) {
		TabInfo tabInfo = new TabInfo(SearchEventsActivity.this, tag, classFragment, null, fragment);
		
		adapterPager.addToFragmentList(fragment);
		
		View tabview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) tabview.findViewById(R.id.tabsText);
        tv.setText(tabInfo.getTag());

    	TabSpec tabSpec = 
    			mTabHost.newTabSpec(tag)
    			.setIndicator(tag)
    			.setIndicator(tabview)
    			.setContent(new TabFactory(getApplicationContext()));

    	mTabHost.addTab(tabSpec);
    	
    	if(!tabInfos.contains(tabInfo)) {
			tabInfos.add(tabInfo);
		}

	}
}
