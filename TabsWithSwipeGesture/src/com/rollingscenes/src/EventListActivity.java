package com.rollingscenes.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rollingscenes.R;
import com.rollingscenes.src.adapter.TabPagerAdapter;
import com.rollingscenes.src.asynctasks.GetEvents;
import com.rollingscenes.src.asynctasks.StoreEvents;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.interfaces.DateChangeObserver;
import com.rollingscenes.src.interfaces.GetEventsLoaderIntf;
import com.rollingscenes.src.interfaces.StoreEventsIntf;
import com.rollingscenes.src.receivers.RSAppReceiver;
import com.rollingscenes.src.utils.ConnectionDetectorUtils;
import com.rollingscenes.src.utils.KeyConstants;
import com.rollingscenes.src.utils.SharedPrefs;

public class EventListActivity extends FragmentActivity implements GetEventsLoaderIntf, StoreEventsIntf, DateChangeObserver {
	public static String EXTERNAL_IMAGE_DIRECTORY;
	private static final long MILLISECONDS_PER_DAY = 86400000;

/*	private static final String TODAYS_EVENTS_URL = KeyConstants.BASE_URL_FOR_USER_CONTROLLER+"getTodaysEvents";
	private static final String TOMORROWS_EVENTS_URL = KeyConstants.BASE_URL_FOR_USER_CONTROLLER+"getTomorrowsEvents";
	private static final String LATERS_EVENTS_URL = KeyConstants.BASE_URL_FOR_USER_CONTROLLER+"getLatersEvents";*/
	private static final String THIS_ACTIVITY = "Main Activity";
	private static final String TAG = EventListActivity.class.getSimpleName();

    protected TabHost mTabHost;
	private ViewPager viewPager;
	private TabPagerAdapter adapterPager;
    private ArrayList<TabInfo> tabInfos;

	@SuppressLint({ "NewApi", "ShowToast" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tabInfos = new ArrayList<TabInfo>();
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.AM_PM, Calendar.AM);
		
		Intent myIntent = new Intent(getApplicationContext(),
				RSAppReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				today.getTimeInMillis() + MILLISECONDS_PER_DAY, AlarmManager.INTERVAL_DAY,
				pendingIntent);

		//initialize the navigation drawer fragment
		NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
		// Initilization
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		viewPager = (ViewPager) findViewById(R.id.pager);

		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5e35b1")));
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
					((EventsDayFragment)tabInfo.getEventsFragment()).notifyAdapter();
				}
				
			}
		});
		
		File file = new File(Environment.getExternalStorageDirectory(), KeyConstants.IMAGE_DIRECTORY);
		if(!file.exists()) {
			file.mkdir();
		}
		EXTERNAL_IMAGE_DIRECTORY = file.getAbsolutePath();

		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		
		addTabToTabHost(KeyConstants.TODAY, EventsDayFragment.class, new EventsDayFragment(KeyConstants.TODAY, getApplicationContext()));
		addTabToTabHost(KeyConstants.TOMORROW, EventsDayFragment.class, new EventsDayFragment(KeyConstants.TOMORROW, getApplicationContext()));
		addTabToTabHost(KeyConstants.LATER, EventsDayFragment.class, new EventsDayFragment(KeyConstants.LATER, getApplicationContext()));
		
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
		
		MyAppApplication.sendTrackerInfo(getApplicationContext(), THIS_ACTIVITY);
		
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
		updateEventFragments();
	}
		
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_list_menu, menu);
		
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		/*case R.id.item_search_button :
    			Intent intent = new Intent(getApplicationContext(), SearchEventsActivity.class);
    			startActivity(intent);
    			break;*/
    		case R.id.item_refresh_button :
    			refreshEvents();
    			break;
    		default :
				return super.onOptionsItemSelected(item);
    	}
		return super.onOptionsItemSelected(item);
    }
    
	@Override
    protected void onDestroy() {
		ImageLoader.getInstance().stop();
		deleteAllExistingImages();
    	super.onDestroy();
    }
    
    private void deleteAllExistingImages() {
		
		File dir = new File(getApplicationContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), KeyConstants.IMAGE_DIRECTORY); 
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(dir, children[i]).delete();
	        }
		}
    }

	@SuppressLint("InflateParams")
	private void addTabToTabHost(String tag, Class<?> classFragment, EventsDayFragment fragment) {
		TabInfo tabInfo = new TabInfo(EventListActivity.this, tag, classFragment, null, fragment);
		
		adapterPager.addToFragmentList(fragment);
		
		View tabview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) tabview.findViewById(R.id.tabsText);
        tv.setText(tabInfo.getTag());

    	TabSpec tabSpec = mTabHost.newTabSpec(tag)
    			.setIndicator(tag)
    			.setIndicator(tabview)
    			.setContent(new TabFactory(EventListActivity.this));

    	mTabHost.addTab(tabSpec);
    	
    	if(!tabInfos.contains(tabInfo)) {
			tabInfos.add(tabInfo);
		}
	}	
	
	private void refreshEvents() {
		if (new ConnectionDetectorUtils(getApplicationContext()).isConnectedToInternet()) {
			for (Iterator<TabInfo> iterator = tabInfos.iterator(); iterator.hasNext();) {
				TabInfo tabInfo = iterator.next();
				((EventsDayFragment)tabInfo.getEventsFragment()).showProgressWheel();
			}
			int corePoolSize = 60;
			int maximumPoolSize = 80;
			int keepAliveTime = 10;

			BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
			Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
			new GetEvents(this, false).executeOnExecutor(threadPoolExecutor, KeyConstants.BASE_URL_FOR_USER_CONTROLLER+"getAllEvents");
			
		} 
		else {
			Toast.makeText(getApplicationContext(),
					KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void eventsLoaded(ArrayList<RSEvent> events) {
		new StoreEvents(EventListActivity.this, getApplicationContext()).execute(events);
	}

	@Override
	public void errorInLoadingEvents(String error) {
		
	}

	@Override
	public void storedSuccessfully() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateEventFragments();				
			}
		});
	}
	
	private void updateEventFragments() {
		EventsDayFragment eventsDayFragment = null;
		TabInfo tabInfo = new TabInfo(EventListActivity.this, KeyConstants.TODAY);
		eventsDayFragment = (EventsDayFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment();
		eventsDayFragment.updateEvents();
		eventsDayFragment.hideProgressWheel();

		tabInfo = new TabInfo(EventListActivity.this, KeyConstants.TOMORROW);
		eventsDayFragment = (EventsDayFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment();
		eventsDayFragment.updateEvents();
		eventsDayFragment.hideProgressWheel();

		tabInfo = new TabInfo(EventListActivity.this, KeyConstants.LATER);
		eventsDayFragment = (EventsDayFragment)tabInfos.get(tabInfos.indexOf(tabInfo)).getEventsFragment();
		eventsDayFragment.updateEvents();
		eventsDayFragment.hideProgressWheel();
	}
	@Override
	public void errorInStoring() {
		
	}

	@Override
	public void notifyDateChanged() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateEventFragments();				
			}
		});
	}
}
