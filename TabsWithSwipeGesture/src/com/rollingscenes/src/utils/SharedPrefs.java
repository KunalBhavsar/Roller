package com.rollingscenes.src.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
	private SharedPreferences sPrefs;
	private static SharedPrefs sPrefClass;

	private static final String IS_LOGGED_IN = "logged_in";
	private static final String IS_LOGOUT_CLICKED = "logout_clicked";
	private static final String LOGGED_IN_WITH = "logged_in_with";
	private static final String USER_EMAIL = "user_email";
	private static final String SOCIAL_LOGIN_USER_ID = "social_login_user_id";

	public static SharedPrefs getInstance(Context context) {
		if(sPrefClass == null) {
			sPrefClass = new SharedPrefs(context);
		}
		return sPrefClass;		
	}
	
	public SharedPrefs(Context context) {
		sPrefs = context.getSharedPreferences("app_shared_pref", Context.MODE_PRIVATE);
	}
	
	public void setLoggedIn(boolean loggedIn) {
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putBoolean(IS_LOGGED_IN, loggedIn);
		editor.commit();
	}
	
	public void setLogoutClicked(boolean logoutClicked) {
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putBoolean(IS_LOGOUT_CLICKED, logoutClicked);
		editor.commit();
	}

	public void setLoggedInWith(String loggedInWith) {
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(LOGGED_IN_WITH, loggedInWith);
		editor.commit();
	}

	public void setUserEmail(String userEmail) {
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(USER_EMAIL, userEmail);
		editor.commit();
	}

	public void setSocialLoginUserID(String socialLoginUserId) {
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(SOCIAL_LOGIN_USER_ID, socialLoginUserId);
		editor.commit();
	}
	
	public boolean isLoggedIn() {
		return sPrefs.getBoolean(IS_LOGGED_IN, false);
	}

	public boolean isLogoutClicked() {
		return sPrefs.getBoolean(IS_LOGOUT_CLICKED, false);
	}

	public String getLoggedInWith() {
		return sPrefs.getString(LOGGED_IN_WITH, null);
	}

	public String getUserEmail() {
		return sPrefs.getString(USER_EMAIL, null);
	}

	public String getSocialLoginUserID() {
		return sPrefs.getString(SOCIAL_LOGIN_USER_ID, null);
	}
	
	public void resetSharedPrefs() {
		setLoggedIn(false);
		setLogoutClicked(false);
		setLoggedInWith("");
		setSocialLoginUserID("");
		setUserEmail("");
	}
}
