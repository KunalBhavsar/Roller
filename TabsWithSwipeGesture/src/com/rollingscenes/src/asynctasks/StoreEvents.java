package com.rollingscenes.src.asynctasks;

import java.util.ArrayList;
import java.util.Iterator;

import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.interfaces.StoreEventsIntf;

import android.content.Context;
import android.os.AsyncTask;

public class StoreEvents extends AsyncTask<ArrayList<RSEvent>, Void, Boolean> {
	
	private StoreEventsIntf storeEventsIntf;
	private RSAppDbHelper dbHelper;
	
	public StoreEvents(StoreEventsIntf storeEventsIntf, Context context) {
		this.storeEventsIntf = storeEventsIntf;
		dbHelper = RSAppDbHelper.getInstance(context);
	}
	
	@Override
	protected Boolean doInBackground(ArrayList<RSEvent>... params) {
		dbHelper.deleteOldEvents();
		boolean inserted = false;
		for (Iterator<RSEvent> iterator = params[0].iterator(); iterator.hasNext();) {
			RSEvent event = iterator.next();
			if(!dbHelper.eventAlreadyPresent(event.getRemoteId())) {
				inserted = dbHelper.insertEvent(event);	
				if(!inserted) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result.booleanValue() == true) {
			storeEventsIntf.storedSuccessfully();
		}
		else {
			storeEventsIntf.errorInStoring();
		}
	}
}
