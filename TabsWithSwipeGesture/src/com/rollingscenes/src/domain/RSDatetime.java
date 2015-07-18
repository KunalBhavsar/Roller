package com.rollingscenes.src.domain;


public class RSDatetime {
	private long startTime;
	private long endTime;

	public RSDatetime(long startTime, long endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
