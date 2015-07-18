/**
 * 
 */
package com.rollingscenes.src.services;

import com.rollingscenes.src.interfaces.DateChangeSubject;

import android.app.IntentService;
import android.content.Intent;

/**
 * @author root
 * 
 */
public class RSAppService extends IntentService {
	public static final String TAG = RSAppService.class
			.getSimpleName();

	public RSAppService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((DateChangeSubject)getApplicationContext()).onDateChanged();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
