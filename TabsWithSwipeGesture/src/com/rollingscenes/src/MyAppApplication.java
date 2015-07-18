package com.rollingscenes.src;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.handlers.UnCaughtException;
import com.rollingscenes.src.interfaces.DateChangeObserver;
import com.rollingscenes.src.interfaces.DateChangeSubject;

public class MyAppApplication extends Application implements DateChangeSubject {

	private static final String PROPERTY_ID = "UA-60218848-2";
	private static RSEvent transientEventFromEventListToEventDesc;

	private static Tracker mTracker;
	
	private ArrayList<DateChangeObserver> observerList;
	
	@Override
	public void onCreate() {
		super.onCreate();
		observerList = new ArrayList<DateChangeObserver>();
        startCatcher();
	}
	
	public static RSEvent getTransientEventFromEventListToEventDesc() {
		return transientEventFromEventListToEventDesc;
	}

	public static void setTransientEventFromEventListToEventDesc(RSEvent event) {
		transientEventFromEventListToEventDesc = event;
	}

	public synchronized static void sendTrackerInfo(Context context, String screenName) {
		
		if (mTracker == null) {
			mTracker = GoogleAnalytics.getInstance(context).newTracker(PROPERTY_ID);
		}

		// Set screen name.
		mTracker.setScreenName(screenName);

		// Send a screen view.
		mTracker.send(new HitBuilders.AppViewBuilder().build());
		
		// Clear the screen name field when we're done.
		mTracker.setScreenName(null);
	}

	public synchronized static void sendTrackerAndEventInfo(Context context, String screenName, String eventCategory, String eventName) {
		
		if (mTracker == null) {
			mTracker = GoogleAnalytics.getInstance(context).newTracker(PROPERTY_ID);
		}

		// Set screen name.
		mTracker.setScreenName(screenName);

		// Send a screen view.
		mTracker.send(new HitBuilders.AppViewBuilder().build());
		
		// Build and send an Event.
		mTracker.send(new HitBuilders.EventBuilder()
			    .setCategory(eventCategory)
			    .setAction("Visited")
			    .setLabel(eventName)
			    .build());
		
		// Clear the screen name field when we're done.
        mTracker.setScreenName(null);
	}
	
    private void startCatcher() {
        while (true) {
        	Log.i("Application", "Catcher starterd");
            try {
                Looper.loop();
        		Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(new Handler()));
                throw new RuntimeException("Main thread loop unexpectedly exited");
            } 
            catch (com.rollingscenes.src.exceptions.BackgroundException e) {
            	Log.e("Application crash catched", e.getMessage());
                showCrashDisplayActivity(e);
            } 
            catch (Throwable e) {
                showCrashDisplayActivity(e);
            }
        }
    }
    
    void showCrashDisplayActivity(Throwable e) {
        Intent i = new Intent(this, com.rollingscenes.src.CrashDisplayActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("ErrorContent", e);
        startActivity(i);
    }

	@Override
	public synchronized void onDateChanged() {
		for (Iterator<DateChangeObserver> iterator = observerList.iterator(); iterator.hasNext();) {
			DateChangeObserver dateChangeObserver = iterator.next();
			dateChangeObserver.notifyDateChanged();
		}
	}

	@Override
	public synchronized void attach(DateChangeObserver observer) {
		observerList.add(observer);
	}

	@Override
	public synchronized void detach(DateChangeObserver observer) {
		observerList.remove(observer);
	}
}
