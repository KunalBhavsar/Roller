package com.rollingscenes.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rollingscenes.R;
import com.rollingscenes.src.adapter.TabPagerAdapter;
import com.rollingscenes.src.interfaces.DateChangeObserver;
import com.rollingscenes.src.utils.KeyConstants;

public class EventsByCategoryActivity extends FragmentActivity implements DateChangeObserver {

	protected static final String TAG = EventsByCategoryActivity.class
			.getSimpleName();

	private static final String BASE_URL_FOR_CATEGORY = KeyConstants.BASE_URL_FOR_USER_CONTROLLER
			+ "getEventsByCategoryAndroid&category=";
	private static final String THIS_ACTIVITY = "Category visited: ";

	protected TabHost mTabHost;
	private ViewPager viewPager;
	private TabPagerAdapter adapterPager;
	private ArrayList<TabInfo> tabInfos;
	private String getEventsByCategoryUrl;
	private String selectedCategory;
	private String categoryColor;
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_without_nav_fragment);

		tabInfos = new ArrayList<TabInfo>();
		// Initilization
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		selectedCategory = getIntent().getStringExtra(KeyConstants.SELECTED_CATEGORY);
		categoryColor = getIntent().getStringExtra(KeyConstants.SELECTED_CATEGORY_COLOR);
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(categoryColor)));
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(selectedCategory);
		
		adapterPager = new TabPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(adapterPager);

		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				mTabHost.setCurrentTabByTag(tabId);
				viewPager.setCurrentItem(mTabHost.getCurrentTab());
				for (Iterator<TabInfo> iterator = tabInfos.iterator(); iterator
						.hasNext();) {
					TabInfo tabInfo = iterator.next();
					((EventsFragment)tabInfo.getEventsFragment()).notifyAdapter();
				}
			}
		});

		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getBaseContext()));

		String categoryForUrl = null;
		if(selectedCategory.equals(KeyConstants.CATEGORY_FOOD_DRINKS)) {
			categoryForUrl = "Food%20+%20Drinks";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_COMEDY)) { //Comedy
			categoryForUrl = "Comedy";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_MUSIC)) { //Music
			categoryForUrl = "Music";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_OUTDOOR)) {
			categoryForUrl = "Outdoor";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_NIGHTLIFE)) {
			categoryForUrl = "Nightlife";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_HAPPY_HOURS)) {
			categoryForUrl = "Happy%20Hours";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_CULTURE)) {
			categoryForUrl = "Culture";
		}
		else if(selectedCategory.equals(KeyConstants.CATEGORY_THEATER)) {
			categoryForUrl = "Theatre";
		}
		
		getEventsByCategoryUrl = BASE_URL_FOR_CATEGORY + categoryForUrl + "&index=0&which_day=";
		
		addTabToTabHost(KeyConstants.TODAY, EventsFragment.class,
				new EventsFragment(getEventsByCategoryUrl + "today", getApplicationContext()));
		addTabToTabHost(KeyConstants.TOMORROW, EventsFragment.class,
				new EventsFragment(getEventsByCategoryUrl + "tomorrow", getApplicationContext()));
		addTabToTabHost(KeyConstants.LATER, EventsFragment.class,
				new EventsFragment(getEventsByCategoryUrl + "later", getApplicationContext()));

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

		viewPager.setOffscreenPageLimit(mTabHost.getChildCount());

		MyAppApplication.sendTrackerInfo(getApplicationContext(), THIS_ACTIVITY
				+ getIntent().getStringExtra(KeyConstants.SELECTED_CATEGORY));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_list_menu, menu);

		//menu.findItem(R.id.item_search_button).setVisible(false);
		menu.findItem(R.id.item_refresh_button).setVisible(false);
		menu.findItem(R.id.item_logout).setVisible(false);*/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.item_refresh_button:
				refreshEvents();
				
			default:
				onBackPressed();
				
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		((MyAppApplication)getApplicationContext()).detach(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		((MyAppApplication)getApplicationContext()).attach(this);
	}

	@Override
	protected void onStop() {
		ImageLoader.getInstance().stop();
		File tempImagesDir = new File(Environment.getExternalStorageDirectory(), KeyConstants.TEMP_IMAGE_DIRECTORY);
		
		if(tempImagesDir.exists() && tempImagesDir.isDirectory()) {
			for (File image : tempImagesDir.listFiles()) {
				image.delete();
			}
		}
		super.onStop();
	}
	
	private void addTabToTabHost(String tag, Class<?> classFragment,
			EventsFragment fragment) {
		TabInfo tabInfo = new TabInfo(EventsByCategoryActivity.this, tag,
				classFragment, null, fragment);

		adapterPager.addToFragmentList(fragment);

		View tabview = LayoutInflater.from(mTabHost.getContext()).inflate(
				R.layout.tab_layout, null);
		RelativeLayout rl = (RelativeLayout) tabview.findViewById(R.id.tabsLayout);
		rl.setBackgroundColor(Color.parseColor(categoryColor));
		TextView tv = (TextView) tabview.findViewById(R.id.tabsText);
		tv.setText(tabInfo.getTag());

		TabSpec tabSpec = mTabHost.newTabSpec(tag).setIndicator(tag)
				.setIndicator(tabview)
				.setContent(new TabFactory(EventsByCategoryActivity.this));

		mTabHost.addTab(tabSpec);

		if (!tabInfos.contains(tabInfo)) {
			tabInfos.add(tabInfo);
		}

	}
	
	private void refreshEvents() {
		TabInfo tabInfo = new TabInfo(EventsByCategoryActivity.this, KeyConstants.TODAY);
		((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(getEventsByCategoryUrl + "today");
		tabInfo = new TabInfo(EventsByCategoryActivity.this, KeyConstants.TOMORROW);
		((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(getEventsByCategoryUrl + "tomorrow");
		tabInfo = new TabInfo(EventsByCategoryActivity.this, KeyConstants.LATER);
		((EventsFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment()).updateEvents(getEventsByCategoryUrl + "later");
	}

	@Override
	public void notifyDateChanged() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				refreshEvents();				
			}
		});
	}
}
