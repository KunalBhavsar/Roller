package com.rollingscenes.src.web.entities;

import java.io.Serializable;

public class WeDatetime implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int year;
	private int month;
	private int day;
	private int start_time_hour;
	private int start_time_min;
	private int end_time_hour;
	private int end_time_min;

	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int getDay() {
		return day;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getStart_time_hour() {
		return start_time_hour;
	}
	public void setStart_time_hour(int start_time_hour) {
		this.start_time_hour = start_time_hour;
	}
	public int getStart_time_min() {
		return start_time_min;
	}
	public void setStart_time_min(int start_time_min) {
		this.start_time_min = start_time_min;
	}
	public int getEnd_time_hour() {
		return end_time_hour;
	}
	public void setEnd_time_hour(int end_time_hour) {
		this.end_time_hour = end_time_hour;
	}
	public int getEnd_time_min() {
		return end_time_min;
	}
	public void setEnd_time_min(int end_time_min) {
		this.end_time_min = end_time_min;
	}
}
