package com.danthe.dogeescape;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
/**
 * Singelton to handle all the tracking towards google. Not much in here by now.
 * @author Daniel
 *
 */
public class Tracker{
	private EasyTracker easyTracker;

	private Activity activity;

	private static Tracker instance;
	
	public enum LevelSuccess{
		FAIL,
		WIN,
		RETRY
	}
	
	public static Tracker getInstance() {
		if (instance == null) throw new RuntimeException("Tracker not initialized");
		else return instance;
	}
	
	public static void init(Activity activity) {
		instance = new Tracker(activity);
	}
	

	private Tracker(Activity activity) {
		easyTracker = EasyTracker.getInstance(activity.getApplicationContext());
		this.activity = activity;
	}
	
	public void triggerOnStart() {

		EasyTracker.getInstance(activity.getApplicationContext()).activityStart(activity);
	}
	
	public void triggerOnStop() {

		EasyTracker.getInstance(activity.getApplicationContext()).activityStop(activity);
	}
	
	public void triggerLevel(int LevelID, LevelSuccess levelSuccess) {
//		HitBuilders h;
//		easyTracker.send(new EventBuilder()
//	    .setCategory("Barren Fields")
//	    .setAction("Visited")
//	    .setLabel("Magic Tree")
//	    .setValue(1)
//	    .build());
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}
}
