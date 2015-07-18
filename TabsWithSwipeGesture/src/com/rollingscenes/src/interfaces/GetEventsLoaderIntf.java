package com.rollingscenes.src.interfaces;

import java.util.ArrayList;

import com.rollingscenes.src.domain.RSEvent;

public interface GetEventsLoaderIntf {
	
	public void eventsLoaded(ArrayList<RSEvent> events);
	
	public void errorInLoadingEvents(String error);
}
