package com.rollingscenes.src.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.os.Environment;

import com.rollingscenes.src.domain.RSDatetime;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;
import com.rollingscenes.src.web.entities.WeDatetime;
import com.rollingscenes.src.web.entities.WeEvent;
import com.rollingscenes.src.web.entities.WeImage;

public class WeToRS {
	
	public static ArrayList<RSDatetime> weToRSDatetime(WeDatetime[] weDatetime) {
		ArrayList<RSDatetime> rsDatetimes = new ArrayList<RSDatetime>();
		for(int i = 0; i < weDatetime.length; i++) {
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.set(weDatetime[i].getYear(), weDatetime[i].getMonth()-1, weDatetime[i].getDay());
			endCal.set(weDatetime[i].getYear(), weDatetime[i].getMonth()-1, weDatetime[i].getDay());
			startCal.set(Calendar.HOUR_OF_DAY, weDatetime[i].getStart_time_hour());
			startCal.set(Calendar.MINUTE, weDatetime[i].getStart_time_min());
			endCal.set(Calendar.HOUR_OF_DAY, weDatetime[i].getEnd_time_hour());
			endCal.set(Calendar.MINUTE, weDatetime[i].getEnd_time_min());
			rsDatetimes.add(new RSDatetime(startCal.getTimeInMillis(), endCal.getTimeInMillis()));
		}
		
		Collections.sort(rsDatetimes, DATETIME);
		return rsDatetimes;
	}
	
	public static Comparator<RSDatetime> DATETIME = new Comparator<RSDatetime>() {
        @Override
        public int compare(RSDatetime o1, RSDatetime o2) {
            return (int)(o1.getStartTime() - o2.getStartTime());
        }
    };
    
	public static ArrayList<RSImage> weToRSImage(WeImage[] weImages, long eventId, boolean temperory) {
		String appFolderPath = Environment.getExternalStorageDirectory() + File.separator + KeyConstants.APP_DIRECTORY;
		File file = new File(appFolderPath);
		if(!file.exists()) {
			file.mkdir();
			if(temperory) {
				file = new File(appFolderPath, KeyConstants.TEMP_IMAGE_DIRECTORY);
			}
			else {
				file = new File(appFolderPath, KeyConstants.IMAGE_DIRECTORY);
			}
			if(!file.exists()) {
				file.mkdir();
			}
		}
		
		ArrayList<RSImage> rsImages = new ArrayList<RSImage>();
		
		RSImage rsImage = null;
		WeImage weImage = null;
		for(int i = 0; i < weImages.length; i++) {
			weImage = weImages[i];
			rsImage = new RSImage(file.getAbsolutePath() + File.separator + eventId + "_" + i + ".jpeg", 
					KeyConstants.BASE_URL + weImage.getImage_path(), weImage.isPrimary());
			rsImages.add(rsImage);
		}
		return rsImages;
	}
	
	public static ArrayList<String> weToRSHashtags(String[] weHashtags) {
		ArrayList<String> rsHashtags = new ArrayList<String>();
		for(int i = 0; i < weHashtags.length; i++) {
			rsHashtags.add(weHashtags[i]);
		}
		return rsHashtags;
	}
	
	public static RSEvent weToRSEvent(WeEvent weEvent, boolean temperory) {
		return new RSEvent(weEvent.getEvent_detail_id(), weEvent.getCategory_name(),
				weEvent.getEvent_area(), weEvent.getEvent_city(), weEvent.getEvent_cost(),
				weEvent.getEvent_location(), weEvent.getEvent_latitude(), weEvent.getEvent_longitude(),
				weEvent.getEvent_name(), weEvent.getEvent_overview(), weEvent.getVenue_name(),
				weToRSImage(weEvent.getImage(), weEvent.getEvent_detail_id(), temperory),
				weToRSHashtags(weEvent.getEvent_hashtags()), weToRSDatetime(weEvent.getDatetime()),
				weEvent.getCategory_color(), false, 1, false);
	}
}
