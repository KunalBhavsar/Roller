package com.rollingscenes.src.web.entities;

import java.io.Serializable;

public class WeSocialLogin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String user_id;
	private String user_name;
	private String user_email_id;
	private String user_dob;
	private String user_city;
	private String social_login_id;
	private String social_login_service;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_email_id() {
		return user_email_id;
	}
	public void setUser_email_id(String user_email_id) {
		this.user_email_id = user_email_id;
	}
	public String getUser_dob() {
		return user_dob;
	}
	public void setUser_dob(String user_dob) {
		this.user_dob = user_dob;
	}
	public String getUser_city() {
		return user_city;
	}
	public void setUser_city(String user_city) {
		this.user_city = user_city;
	}
	public String getSocial_login_id() {
		return social_login_id;
	}
	public void setSocial_login_id(String social_login_id) {
		this.social_login_id = social_login_id;
	}
	public String getSocial_login_service() {
		return social_login_service;
	}
	public void setSocial_login_service(String social_login_service) {
		this.social_login_service = social_login_service;
	}
	
}
