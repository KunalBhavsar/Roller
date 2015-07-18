package com.rollingscenes.src.web.entities;

import java.io.Serializable;

public class WeEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String category_name;
	private String event_area;
	private String event_city;
	private int event_cost;
	private long event_detail_id;
	private String event_location;
	private String event_name;
	private String event_overview;
	private String venue_name;
	private WeImage[] image;
	private String[] event_hashtags;
	private WeDatetime[] datetime;
	private String category_color;
	private double event_latitude;
	private double event_longitude;
	
	public String getCategory_name() {
		return category_name;
	}

	public String getEvent_area() {
		return event_area;
	}

	public String getEvent_city() {
		return event_city;
	}

	public int getEvent_cost() {
		return event_cost;
	}

	public long getEvent_detail_id() {
		return event_detail_id;
	}

	public String getEvent_location() {
		return event_location;
	}

	public String getEvent_name() {
		return event_name;
	}

	public String getEvent_overview() {
		return event_overview;
	}

	public String getVenue_name() {
		return venue_name;
	}

	public WeImage[] getImage() {
		return image;
	}

	public String[] getEvent_hashtags() {
		return event_hashtags;
	}

	public void setImage(WeImage[] image) {
		this.image = image;
	}

	public void setDatetime(WeDatetime[] datetime) {
		this.datetime = datetime;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public void setEvent_area(String event_area) {
		this.event_area = event_area;
	}

	public void setEvent_city(String event_city) {
		this.event_city = event_city;
	}

	public void setEvent_cost(int event_cost) {
		this.event_cost = event_cost;
	}

	public void setEvent_detail_id(long event_detail_id) {
		this.event_detail_id = event_detail_id;
	}

	public void setEvent_location(String event_location) {
		this.event_location = event_location;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public void setEvent_overview(String event_overview) {
		this.event_overview = event_overview;
	}

	public void setVenue_name(String venue_name) {
		this.venue_name = venue_name;
	}

	public void setEvent_hashtags(String[] event_hashtags) {
		this.event_hashtags = event_hashtags;
	}

	public WeDatetime[] getDatetime() {
		return datetime;
	}

	public String getCategory_color() {
		return category_color;
	}

	public void setCategory_color(String category_color) {
		this.category_color = category_color;
	}

	public double getEvent_latitude() {
		return event_latitude;
	}

	public void setEvent_latitude(double event_latitude) {
		this.event_latitude = event_latitude;
	}

	public double getEvent_longitude() {
		return event_longitude;
	}

	public void setEvent_longitude(double event_longitude) {
		this.event_longitude = event_longitude;
	}
	
}
