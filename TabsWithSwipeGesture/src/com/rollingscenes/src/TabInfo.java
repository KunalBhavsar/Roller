package com.rollingscenes.src;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TabInfo {
    private String tag;
	private Class<?> clss;
    private Bundle args;
    private Fragment eventsFragment;
    private Context activityContext;
    
    public TabInfo(Context context, String tag) {
    	this.tag = tag;
    	this.activityContext = context;
    }
    
    public TabInfo(Context context, String tag, Class<?> clazz, Bundle args, Fragment fragment) {
        this.tag = tag;
        this.clss = clazz;
        this.args = args;
        this.eventsFragment = fragment;
        this.activityContext = context;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + activityContext.hashCode();
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabInfo other = (TabInfo) obj;
		if (!activityContext.equals(other.activityContext))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} 
		else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public String getTag() {
		return tag;
	}

	public Class<?> getClss() {
		return clss;
	}

	public Bundle getArgs() {
		return args;
	}

	public Fragment getEventsFragment() {
		return eventsFragment;
	}

	public Context getActivityContext() {
		return activityContext;
	}
	
}
