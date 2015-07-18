package com.rollingscenes.src.web.entities;

import java.io.Serializable;

public class WeImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String image_path;
	private boolean primary_bool;

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public boolean isPrimary() {
		return primary_bool;
	}

	public void setPrimary(boolean primary) {
		this.primary_bool = primary;
	}
}