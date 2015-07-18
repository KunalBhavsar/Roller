package com.rollingscenes.src.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.interfaces.GetEventsLoaderIntf;
import com.rollingscenes.src.utils.KeyConstants;
import com.rollingscenes.src.utils.WeToRS;
import com.rollingscenes.src.web.entities.WeEvent;

import android.os.AsyncTask;
import android.util.Log;

public class GetEvents extends AsyncTask<String, Void, ArrayList<WeEvent>> {

	private static final String TAG = GetEvents.class.getSimpleName();
	private GetEventsLoaderIntf eventsLoaderIntf;
	private boolean temperory;
	
	public GetEvents(GetEventsLoaderIntf eventsLoaderIntf, boolean temperory) {
		this.eventsLoaderIntf = eventsLoaderIntf;
		this.temperory = temperory;
	}
	
	@Override
	protected ArrayList<WeEvent> doInBackground(String... params) {
		
		Log.e(TAG, "URL: "+params[0]);
		
		HttpURLConnection connection = null;
		int statusCode = 0;
		BufferedReader reader = null;
		ArrayList<WeEvent> events = null;
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
				//Log.e(TAG, responseData);
				events = new Gson().fromJson(responseData, new TypeToken<ArrayList<WeEvent>>() {}.getType());
			}
			else {
				reader = new BufferedReader(new InputStreamReader(
						connection.getErrorStream()));
				String line = "";
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				Log.e(TAG, "Error occured with status code : "+statusCode+"\n"+sb.toString());
			}
		} 
		catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return events;
	}
	@Override
	protected void onPostExecute(ArrayList<WeEvent> result) {
		if(result != null) {
			ArrayList<RSEvent> events = new ArrayList<RSEvent>();
			for (Iterator<WeEvent> iterator = result.iterator(); iterator.hasNext();) {
				events.add(WeToRS.weToRSEvent(iterator.next(), temperory));
			}
			eventsLoaderIntf.eventsLoaded(events);
		}
		else {
			eventsLoaderIntf.errorInLoadingEvents("Error in loading events. Please, try again later...");
		}
	}
}
