package com.rollingscenes.src.interfaces;

public interface DateChangeSubject {
	public void onDateChanged();
	public void attach(DateChangeObserver observer);
	public void detach(DateChangeObserver observer);
}
