/**
 * 
 */

package com.rollingscenes.src.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.rollingscenes.src.domain.RSDatetime;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.domain.RSImage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteHelper class used to iteract with local database
 * @author Neebal Technologies
 */
public class RSAppDbHelper extends SQLiteOpenHelper {

	private static final long MILLISECONDS_PER_DAY = 86400000;
	
	private static final String DB_NAME = "RSAppDb.sqlite";
	private static final int DB_VERSION = 1;

	//private static final String KEY_ID = "rowid";

	private static final String TABLE_EVENTS = "events";
	private static final String KEY_REMOTE_EVENT_ID = "remote_event_id";
	private static final String KEY_EVENT_CATEGORY_NAME = "category_name";
	private static final String KEY_EVENT_AREA = "area";
	private static final String KEY_EVENT_CITY = "city";
	private static final String KEY_EVENT_COST = "cost";
	private static final String KEY_EVENT_LOCATION = "location";
	private static final String KEY_EVENT_NAME = "name";
	private static final String KEY_EVENT_OVERVIEW = "overview";
	private static final String KEY_EVENT_VENUE = "venue";
	private static final String KEY_IMAGES_DOWNLOADED = "downloaded";
	private static final String KEY_EVENT_COLOR = "color";
	private static final String KEY_EVENT_VERSION = "version";
	private static final String KEY_EVENT_LATITUDE = "latitude";
	private static final String KEY_EVENT_LONGITUDE = "longitude";
	private static final String CREATE_TABLE_EVENTS = "create table "
			+ TABLE_EVENTS + "(" 
			+ KEY_REMOTE_EVENT_ID + " integer not null,"
			+ KEY_EVENT_CATEGORY_NAME + " text not null," 
			+ KEY_EVENT_AREA + " text not null," 
			+ KEY_EVENT_CITY + " text not null,"
			+ KEY_EVENT_COST + " integer not null,"
			+ KEY_EVENT_LOCATION + " text not null,"
			+ KEY_EVENT_NAME + " text not null,"
			+ KEY_EVENT_OVERVIEW + " text not null,"
			+ KEY_EVENT_VENUE + " text not null," 
			+ KEY_EVENT_COLOR + " text not null," 
			+ KEY_IMAGES_DOWNLOADED + " integer not null,"
			+ KEY_EVENT_LATITUDE + " integer null,"
			+ KEY_EVENT_LONGITUDE + " integer null,"
			+ KEY_EVENT_VERSION + " integer not null" 
			+ ");";
	
	private static final String TABLE_HASHTAGS = "hashtags";
	
	private static final String KEY_EVENT_HASHTAG = "hashtag";
	
	private static final String CREATE_TABLE_HASHTAGS = "create table "
			+ TABLE_HASHTAGS + "("
			+ KEY_EVENT_HASHTAG + " text not null,"
			+ KEY_REMOTE_EVENT_ID + " integer not null,"
			+ "FOREIGN KEY(" + KEY_REMOTE_EVENT_ID + ") REFERENCES "
			+ TABLE_EVENTS + "(" + KEY_REMOTE_EVENT_ID + ")"
			+ ");";
	
	private static final String TABLE_DATETIMES = "datetimes";

	private static final String KEY_EVENT_START_TIME = "start_time";
	private static final String KEY_EVENT_END_TIME = "end_time";
	
	private static final String CREATE_TABLE_DATETIMES = "create table "
			+ TABLE_DATETIMES + "(" 
			+ KEY_REMOTE_EVENT_ID + " integer not null,"
			+ KEY_EVENT_START_TIME + " integer not null," 
			+ KEY_EVENT_END_TIME + " integer not null,"
			+ "FOREIGN KEY(" + KEY_REMOTE_EVENT_ID + ") REFERENCES "
			+ TABLE_EVENTS + "(" + KEY_REMOTE_EVENT_ID + ")"
			+ ");";
	
	private static final String TABLE_IMAGES = "images";
	
	private static final String KEY_IMAGE_LOCAL_PATH = "local_path";
	private static final String KEY_IMAGE_SERVER_PATH = "server_path";
	private static final String KEY_IMAGE_PRIMARY = "is_primary";

	private static final String CREATE_TABLE_IMAGES = "create table "
			+ TABLE_IMAGES + "(" 
			+ KEY_REMOTE_EVENT_ID + " integer not null,"
			+ KEY_IMAGE_LOCAL_PATH + " text not null," 
			+ KEY_IMAGE_SERVER_PATH + " text not null,"
			+ KEY_IMAGE_PRIMARY + " integer not null,"
			+ "FOREIGN KEY(" + KEY_REMOTE_EVENT_ID + ") REFERENCES "
			+ TABLE_EVENTS + "(" + KEY_REMOTE_EVENT_ID + ")"
			+ ");";

	private static final String TAG = RSAppDbHelper.class.getSimpleName();	

	private SQLiteDatabase db;

	private static RSAppDbHelper rSAppDbHelper;

	public synchronized static RSAppDbHelper getInstance(
			Context context) {
		if (rSAppDbHelper == null) {
			rSAppDbHelper = new RSAppDbHelper(context);
		}
		return rSAppDbHelper;
	}

	private RSAppDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_EVENTS);
		db.execSQL(CREATE_TABLE_DATETIMES);
		db.execSQL(CREATE_TABLE_HASHTAGS);
		db.execSQL(CREATE_TABLE_IMAGES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	/**
	 * Open database for read.
	 */
	public void openForRead() {
		db = getReadableDatabase();
	}
	
	/**
	 * Open database for write.
	 */
	public void openForWrite() {
		db = getWritableDatabase();
	}
	
	/**
	 * Open database in transaction mode.
	 */
	public synchronized void startTransaction() {
		db = getWritableDatabase();
		db.beginTransaction();
	}
	
	/**
	 * Commit the changes to database made till now in transaction mode.
	 */
	public synchronized void commitTransaction() {
		db.setTransactionSuccessful();
	}
	
	/**
	 * End the transactions currently going on database.
	 */
	public synchronized void endTransaction() {
		db.endTransaction();
	}
		
	/**
	 * Insert event into event table
	 * @param event : event to be stored in local database
	 * @return boolean indicating event is inserted into event table or not
	 */
	public synchronized boolean insertEvent(RSEvent event) {
		openForWrite();
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_REMOTE_EVENT_ID, event.getRemoteId());
		contentValues.put(KEY_EVENT_CATEGORY_NAME, event.getCategoryName());
		contentValues.put(KEY_EVENT_AREA, event.getArea());
		contentValues.put(KEY_EVENT_CITY, event.getCity());
		contentValues.put(KEY_EVENT_COST, event.getCost());
		contentValues.put(KEY_EVENT_LOCATION, event.getLocation());
		contentValues.put(KEY_EVENT_LATITUDE, event.getLatitude());
		contentValues.put(KEY_EVENT_LONGITUDE, event.getLongitude());
		contentValues.put(KEY_EVENT_NAME, event.getName());
		contentValues.put(KEY_EVENT_OVERVIEW, event.getOverview());
		contentValues.put(KEY_EVENT_COLOR, event.getColor());
		contentValues.put(KEY_EVENT_VENUE, event.getVenue());
		contentValues.put(KEY_IMAGES_DOWNLOADED, event.isImagesDownloaded());
		contentValues.put(KEY_EVENT_VERSION, event.getVersion());
		
		for (Iterator<RSDatetime> iterator = event.getDatetimes().iterator(); iterator.hasNext();) {
			insertDatetime(event.getRemoteId(), iterator.next());
		}
		for (Iterator<String> iterator = event.getHashtags().iterator(); iterator.hasNext();) {
			insertHashtag(event.getRemoteId(), iterator.next());
		}
		for (Iterator<RSImage> iterator = event.getImages().iterator(); iterator.hasNext();) {
			insertImage(event.getRemoteId(), iterator.next());
		}
		return db.insert(TABLE_EVENTS, null, contentValues) >= 0;
	}
	
	public synchronized void deleteOldEvents() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		String sql = "SELECT "+TABLE_EVENTS+".* FROM "+ TABLE_EVENTS +" INNER JOIN "+TABLE_DATETIMES+" ON "+TABLE_EVENTS+"."+KEY_REMOTE_EVENT_ID+"="+TABLE_DATETIMES+"."+KEY_REMOTE_EVENT_ID
				+" AND " + TABLE_DATETIMES+"."+KEY_EVENT_START_TIME + " < ? ";
		
		String[] selectionArgs = {"" + calendar.getTimeInMillis()};

		openForWrite();
		
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		
		ArrayList<Long> oldEventIds = new ArrayList<Long>();
		while (cursor.moveToNext()) {
			oldEventIds.add(cursor.getLong(cursor.getColumnIndex(KEY_REMOTE_EVENT_ID)));
		}
		cursor.close();
		ArrayList<RSDatetime> datetimes = null;
		for (Iterator<Long> iterator = oldEventIds.iterator(); iterator.hasNext();) {
			Long long1 = iterator.next();
			datetimes = getEventDatetimes(long1);
			if(datetimes.size() > 0 && datetimes.get(datetimes.size() - 1).getStartTime() > calendar.getTimeInMillis()) {
				iterator.remove();
			}
		}
		
		String whereClause = KEY_REMOTE_EVENT_ID +" = ?";
		for (Iterator<Long> iterator = oldEventIds.iterator(); iterator.hasNext();) {
			Long long1 = iterator.next();
			
			String[] whereArgs = {""+long1.longValue()};
			db.delete(TABLE_DATETIMES, whereClause, whereArgs);
			ArrayList<RSImage> images = getEventImages(long1.longValue());
			for (Iterator<RSImage> iterator2 = images.iterator(); iterator2.hasNext();) {
				RSImage rsImage = iterator2.next();
				new File(rsImage.getLocalImagePath()).delete();				
			}
			db.delete(TABLE_IMAGES, whereClause, whereArgs);
			db.delete(TABLE_HASHTAGS, whereClause, whereArgs);
			db.delete(TABLE_EVENTS, whereClause, whereArgs);
		}

	}
	
	public synchronized boolean eventAlreadyPresent(long eventRemoteId) {
		openForRead();		
		String selection = KEY_REMOTE_EVENT_ID + " = ? ";
		String[] selectionArgs = {""+eventRemoteId};
		
		
		Cursor cursor = db.query(TABLE_EVENTS, null, selection, selectionArgs, null, null, null);
		
		boolean eventPresent = cursor.moveToNext();
		cursor.close();
		
		return eventPresent;
	}
	/**
	 * Insert image data into images table
	 * @param remoteEventId : event id of the event, whose image to be stored
	 * @param image : image data to be stored in local database
	 * @return boolean indicating image data is inserted into images table or not
	 */
	public synchronized boolean insertImage(long remoteEventId, RSImage image) {
		openForWrite();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_IMAGE_LOCAL_PATH, image.getLocalImagePath());
		contentValues.put(KEY_IMAGE_SERVER_PATH, image.getServerImagePath());
		contentValues.put(KEY_IMAGE_PRIMARY, image.isPrimary());
		contentValues.put(KEY_REMOTE_EVENT_ID, remoteEventId);
		
		return db.insert(TABLE_IMAGES, null, contentValues) >= 0;
	}
	
	/**
	 * Insert event hashtag into hashtags table
	 * @param remoteEventId : event id of the event, whose hashtag to be stored
	 * @param hashtag : event hashtag to be stored in local database
	 * @return boolean indicating event hashtag is inserted into hashtags table or not
	 */
	public synchronized boolean insertHashtag(long remoteEventId, String hashtag) {
		openForWrite();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_EVENT_HASHTAG, hashtag);
		contentValues.put(KEY_REMOTE_EVENT_ID, remoteEventId);
		
		return db.insert(TABLE_HASHTAGS, null, contentValues) >= 0;
	}

	/**
	 * Insert event datetime into datetimes table
	 * @param remoteEventId : event id of the event, whose datetime to be stored
	 * @param datetime : event datetime to be stored in local database
	 * @return boolean indicating event datetime is inserted into datetimes table or not
	 */
	public synchronized boolean insertDatetime(long remoteEventId, RSDatetime datetime) {
		openForWrite();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_EVENT_START_TIME, datetime.getStartTime());
		contentValues.put(KEY_EVENT_END_TIME, datetime.getEndTime());
		contentValues.put(KEY_REMOTE_EVENT_ID, remoteEventId);
		
		return db.insert(TABLE_DATETIMES, null, contentValues) >= 0;
	}
	
	/**
	 * Return the todays events from events stored in local storage.
	 * @return : return the list of events
	 */
	public synchronized ArrayList<RSEvent> getTodaysEvents() {
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.AM_PM, Calendar.AM);
		
		long startMilli = today.getTimeInMillis();
		long endMilli = startMilli + MILLISECONDS_PER_DAY;
		
		return getEventsBetweenMillis(startMilli, endMilli);
	}

	/**
	 * Return the tomorrows events from events stored in local storage.
	 * @return : return the list of events
	 */
	public synchronized ArrayList<RSEvent> getTomorrowsEvents() {
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.AM_PM, Calendar.AM);		
		
		long startMilli = today.getTimeInMillis() + MILLISECONDS_PER_DAY;
		long endMilli = startMilli + MILLISECONDS_PER_DAY ;
		
		ArrayList<RSEvent> tomorrowsEvents = getEventsBetweenMillis(startMilli, endMilli);
		ArrayList<RSEvent> todaysEvents = getTodaysEvents();
		for (Iterator<RSEvent> iterator = tomorrowsEvents.iterator(); iterator.hasNext();) {
			RSEvent rsEvent = iterator.next();
			if(todaysEvents.contains(rsEvent)) {
				iterator.remove();
			}			
		}
		return tomorrowsEvents;
	}

	/**
	 * Return the later events from events stored in local storage.
	 * @return : return the list of events
	 */
	public synchronized ArrayList<RSEvent> getLatersEvents(int laterDayRange) {
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.AM_PM, Calendar.AM);		
		
		long startMilli = today.getTimeInMillis() + (MILLISECONDS_PER_DAY * 2);
		long endMilli = startMilli + (MILLISECONDS_PER_DAY * laterDayRange);
		
		ArrayList<RSEvent> tomorrowsEvents = getTomorrowsEvents();
		ArrayList<RSEvent> todaysEvents = getTodaysEvents();
		ArrayList<RSEvent> laterEvents = getEventsBetweenMillis(startMilli, endMilli);
		for (Iterator<RSEvent> iterator = laterEvents.iterator(); iterator.hasNext();) {
			RSEvent rsEvent = iterator.next();
			if(todaysEvents.contains(rsEvent)) {
				iterator.remove();
			}			
			else if(tomorrowsEvents.contains(rsEvent)){
				iterator.remove();
			}
		}

		return laterEvents;
	}

	/**
	 * Return the events which has start time in between startMilli and endMilli from local storage.
	 * @return : return the list of events
	 */
	private ArrayList<RSEvent> getEventsBetweenMillis(long startMilli, long endMilli) {
		ArrayList<RSEvent> events = new ArrayList<RSEvent>();
		
		String sql = "SELECT "+TABLE_EVENTS+".* FROM "+ TABLE_EVENTS +" INNER JOIN "+TABLE_DATETIMES+" ON "+TABLE_EVENTS+"."+KEY_REMOTE_EVENT_ID+"="+TABLE_DATETIMES+"."+KEY_REMOTE_EVENT_ID
				+" AND " + TABLE_DATETIMES+"."+KEY_EVENT_START_TIME + " >= ? AND " + TABLE_DATETIMES + "." + KEY_EVENT_START_TIME + " <= ?";
		String[] selectionArgs = {"" + startMilli, "" + endMilli};

		openForRead();
		
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		
		RSEvent event = null;
		while (cursor.moveToNext()) {
			event = new RSEvent(cursor.getLong(cursor.getColumnIndex(KEY_REMOTE_EVENT_ID)),
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_CATEGORY_NAME)), 
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_AREA)),
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_CITY)), 
					cursor.getInt(cursor.getColumnIndex(KEY_EVENT_COST)),
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_LOCATION)), 
					cursor.getDouble(cursor.getColumnIndex(KEY_EVENT_LATITUDE)), 
					cursor.getDouble(cursor.getColumnIndex(KEY_EVENT_LONGITUDE)), 
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_NAME)), 
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_OVERVIEW)), 
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_VENUE)), 
					getEventImages(cursor.getLong(cursor.getColumnIndex(KEY_REMOTE_EVENT_ID))), 
					getEventHashtags(cursor.getLong(cursor.getColumnIndex(KEY_REMOTE_EVENT_ID))),
					getEventDatetimes(cursor.getLong(cursor.getColumnIndex(KEY_REMOTE_EVENT_ID))),
					cursor.getString(cursor.getColumnIndex(KEY_EVENT_COLOR)),
					cursor.getInt(cursor.getColumnIndex(KEY_IMAGES_DOWNLOADED)) == 1 ? true : false, 
					cursor.getInt(cursor.getColumnIndex(KEY_EVENT_VERSION)), true);
		
			if(!events.contains(event) && event.getDatetimes().size() > 0) {
				events.add(event);
			}
		}
		cursor.close();
		return events;

	}
	
	/**
	 * Fetch list of images related to the event
	 * @param eventRemoteId : remote id of the event
	 * @return ArrayList of RSImage
	 */
	public synchronized ArrayList<RSImage> getEventImages(long eventRemoteId) {
		ArrayList<RSImage> eventImages = new ArrayList<RSImage>();
		
		String selection = KEY_REMOTE_EVENT_ID + " = ? ";
		String[] selectionArgs = {""+eventRemoteId};
		
		openForRead();
		
		Cursor cursor = db.query(TABLE_IMAGES, null, selection, selectionArgs, null, null, null);
		
		RSImage eventImage;
		while(cursor.moveToNext()) {
			eventImage = new RSImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_LOCAL_PATH)),
					cursor.getString(cursor.getColumnIndex(KEY_IMAGE_SERVER_PATH)), 
					cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_PRIMARY)) == 1 ? true : false);
			eventImages.add(eventImage);
		}
		
		cursor.close();
		return eventImages;
	}

	/**
	 * Fetch array list of hashtags related to the event
	 * @param eventRemoteId : event id of the event of which hashtags want to be fetched
	 * @return : ArrayList of String
	 */
	public synchronized ArrayList<String> getEventHashtags(long eventRemoteId) {
		ArrayList<String> eventHashtags = new ArrayList<String>();
		
		String selection = KEY_REMOTE_EVENT_ID + " = ? ";
		String[] selectionArgs = {""+eventRemoteId};
		
		openForRead();
		
		Cursor cursor = db.query(TABLE_HASHTAGS, null, selection, selectionArgs, null, null, null);
		
		while(cursor.moveToNext()) {
			eventHashtags.add(cursor.getString(cursor.getColumnIndex(KEY_EVENT_HASHTAG)));
		}
		
		cursor.close();
		return eventHashtags;
	}

	/**
	 * Fetch list of datetime related to event
	 * @param eventRemoteId : event id of event of which datetimes have to be fetched
	 * @return ArrayList of datetimes
	 */
	public synchronized ArrayList<RSDatetime> getEventDatetimes(long eventRemoteId) {
		ArrayList<RSDatetime> eventDatetimes = new ArrayList<RSDatetime>();
		
		String selection = KEY_REMOTE_EVENT_ID + " = ? ";
		String[] selectionArgs = {""+eventRemoteId};
		
		openForRead();
		
		Cursor cursor = db.query(TABLE_DATETIMES, null, selection, selectionArgs, null, null, null);
		
		RSDatetime eventDatetime;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		while(cursor.moveToNext()) {
			eventDatetime = new RSDatetime(cursor.getLong(cursor.getColumnIndex(KEY_EVENT_START_TIME)), 
					cursor.getLong(cursor.getColumnIndex(KEY_EVENT_END_TIME)));
			if(eventDatetime.getStartTime() > calendar.getTimeInMillis()) {
				eventDatetimes.add(eventDatetime);
			}
		}
		
		cursor.close();
		Collections.sort(eventDatetimes, DATETIME);
		return eventDatetimes;
	}
	
	public static Comparator<RSDatetime> DATETIME = new Comparator<RSDatetime>() {
        @Override
        public int compare(RSDatetime o1, RSDatetime o2) {
            return (int)(o1.getStartTime() - o2.getStartTime());
        }
    };

	/**
	 * Update the downloaded flag of the image
	 * @param imageRemoteId :id of the event of which flag to be updated
	 * @param flag : flag to be updated
	 */
	public synchronized void updateImagesDownloadedFlagOfEvent(long imageRemoteId, boolean flag) {
		
		ContentValues values = new ContentValues();
		values.put(KEY_IMAGES_DOWNLOADED, flag);
		String whereClause = KEY_REMOTE_EVENT_ID+" = ?";
		String[] whereArgs = {""+imageRemoteId};
		openForWrite();
		db.update(TABLE_EVENTS, values, whereClause, whereArgs);
	}
	
	public synchronized void updateLatLongOfEvent(long imageRemoteId, double latitude, double longitude) {
		ContentValues values = new ContentValues();
		values.put(KEY_EVENT_LATITUDE, latitude);
		values.put(KEY_EVENT_LONGITUDE, longitude);
		String whereClause = KEY_REMOTE_EVENT_ID+" = ?";
		String[] whereArgs = {""+imageRemoteId};
		openForWrite();
		db.update(TABLE_EVENTS, values, whereClause, whereArgs);
	}
	
	private synchronized ArrayList<RSImage> getAllImages() {
		ArrayList<RSImage> eventImages = new ArrayList<RSImage>();
		
		openForRead();
		
		Cursor cursor = db.query(TABLE_IMAGES, null, null, null, null, null, null);
		
		RSImage eventImage;
		while(cursor.moveToNext()) {
			eventImage = new RSImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_LOCAL_PATH)),
					cursor.getString(cursor.getColumnIndex(KEY_IMAGE_SERVER_PATH)), 
					cursor.getInt(cursor.getColumnIndex(KEY_IMAGE_PRIMARY)) == 1 ? true : false);
			eventImages.add(eventImage);
		}
		
		cursor.close();
		return eventImages;
	}
	
	/**
	 * Truncate all the tables from local database
	 */
	public synchronized void truncateAllTables()
	{
		openForWrite();
		String whereClause = "1";
		Log.e(TAG, "Number of events deleted :"+db.delete(TABLE_EVENTS, whereClause, null));
		for (Iterator<RSImage> iterator = getAllImages().iterator(); iterator.hasNext();) {
			File file = new File(iterator.next().getLocalImagePath());
			if(file.exists()) {
				file.delete();
			}
		}
		Log.e(TAG, "Number of images deleted :"+db.delete(TABLE_IMAGES, whereClause, null));
		Log.e(TAG, "Number of hashtags deleted :"+db.delete(TABLE_HASHTAGS, whereClause, null));
		Log.e(TAG, "Number of datetimes deleted :"+db.delete(TABLE_DATETIMES, whereClause, null));
	}
	
}
