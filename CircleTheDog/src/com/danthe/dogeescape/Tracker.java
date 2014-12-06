package com.danthe.dogeescape;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Singleton to handle all the tracking towards google. Not much in here by now.
 * 
 * @author Daniel
 * 
 */
public class Tracker {
	private EasyTracker easyTracker;

	private Activity activity;

	private static Tracker instance;

	private static final String LABEL = "TURNS NEEDED";

	public enum LevelSuccess {
		FAIL("DEFEAT"), WIN("WIN");
		private String name;

		LevelSuccess(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public static Tracker getInstance() {
		if (instance == null)
			throw new RuntimeException("Tracker not initialized");
		else
			return instance;
	}

	public static void init(Activity activity) {
		instance = new Tracker(activity);
	}

	private Tracker(Activity activity) {
		easyTracker = EasyTracker.getInstance(activity.getApplicationContext());
		this.activity = activity;
	}

	public void triggerOnStart() {
		easyTracker.activityStart(activity);
	}

	public void triggerOnStop() {
		easyTracker.activityStop(activity);
	}

	public void triggerLevel(int levelID, LevelSuccess levelSuccess, long turns) {
		easyTracker.send(MapBuilder.createEvent("Level "+levelID, levelSuccess.toString(), LABEL, turns).build());
	}
}
