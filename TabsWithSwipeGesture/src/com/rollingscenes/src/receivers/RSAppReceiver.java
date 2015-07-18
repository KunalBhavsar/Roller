package com.rollingscenes.src.receivers;
import com.rollingscenes.src.services.RSAppService;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


public class RSAppReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = RSAppReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// Explicitly specify that GcmIntentService will handle the intent.
		Intent service1 = new Intent(context, RSAppService.class);
		context.startService(service1);
	}

}
