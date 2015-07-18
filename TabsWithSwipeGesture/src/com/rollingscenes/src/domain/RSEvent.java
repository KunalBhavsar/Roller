package com.rollingscenes.src.domain;

import java.util.ArrayList;

public class RSEvent {
	private long remoteId;
	private String categoryName;
	private String area;
	private String city;
	private int cost;
	private String location;
	private String name;
	private String overview;
	private String venue;
	private String color;
	private ArrayList<RSImage> images;
	private ArrayList<String> hashtags;
	private ArrayList<RSDatetime> datetimes;
	private boolean imagesDownloaded;
	private int version;
	private boolean locallyStored;
	private double latitude;
	private double longitude;
	public RSEvent(long remoteId, String categoryName, String area,
			String city, int cost, String location, double latitude, double longitude, 
			String name, String overview, String venue,
			ArrayList<RSImage> images, ArrayList<String> hashtags,
			ArrayList<RSDatetime> datetimes, String color,
			boolean imagesDownloaded, int version, boolean locallyStored) {
		super();
		this.remoteId = remoteId;
		this.categoryName = categoryName;
		this.area = area;
		this.city = city;
		this.cost = cost;
		this.location = location;
		this.name = name;
		this.overview = overview;
		this.venue = venue;
		this.color = color;
		this.images = images;
		this.hashtags = hashtags;
		this.datetimes = datetimes;
		this.imagesDownloaded = imagesDownloaded;
		this.version = version;
		this.locallyStored = locallyStored;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(long remoteId) {
		this.remoteId = remoteId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public ArrayList<RSImage> getImages() {
		return images;
	}

	public void setImages(ArrayList<RSImage> images) {
		this.images = images;
	}

	public ArrayList<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(ArrayList<String> hashtags) {
		this.hashtags = hashtags;
	}

	public ArrayList<RSDatetime> getDatetimes() {
		return datetimes;
	}

	public void setDatetimes(ArrayList<RSDatetime> datetimes) {
		this.datetimes = datetimes;
	}
	
	public boolean isImagesDownloaded() {
		return imagesDownloaded;
	}

	public void setImagesDownloaded(boolean imagesDownloaded) {
		this.imagesDownloaded = imagesDownloaded;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public boolean isLocallyStored() {
		return locallyStored;
	}

	public void setLocallyStored(boolean locallyStored) {
		this.locallyStored = locallyStored;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (remoteId ^ (remoteId >>> 32));
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
		RSEvent other = (RSEvent) obj;
		if (remoteId != other.remoteId)
			return false;
		return true;
	}
	
}
