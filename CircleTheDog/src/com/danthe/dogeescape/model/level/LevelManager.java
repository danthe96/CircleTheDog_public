package com.danthe.dogeescape.model.level;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.model.level.Level.Status;

public class LevelManager {

	private static LevelManager levelManager;
	public static final int numLevels = 12;
	private static final Status DEFAULT_STATUS = Status.LOCKED;
	private Activity activity;

	/**
	 * Must be called when the app starts
	 * 
	 * @param sharedPreferences
	 */
	public static void init(Activity activity) {
		levelManager = new LevelManager(activity);
	}

	private LevelManager(Activity activity) {
		this.activity = activity;

		if (getStatus(0) == Status.LOCKED) {
			setStatus(0, Status.PLAYABLE);
		}
	}

	public static LevelManager getInstance() {
		if (levelManager == null)
			throw new RuntimeException("Error: LevelManager not initialized.");
		return levelManager;
	}

	public int getNumLevels() {
		return numLevels;
	}

	/**
	 * TODO change this block into something meaningful
	 * 
	 * @param LevelID
	 * @return
	 */
	public Status getStatus(int LevelID) {
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		Status defaultValue = DEFAULT_STATUS;
		Status result = Status.values()[sharedPref.getInt(
				activity.getString(R.string.shared_pref_level_status_string)
						+ LevelID, defaultValue.ordinal())];
		return result;
	}

	/**
	 * Set the status of a level in the SharedPreferences. This method should
	 * not be called directly from outside to ensure that the next levels are
	 * opened correctly.
	 * 
	 * @param LevelID
	 * @param status
	 */
	private void setStatus(int LevelID, Status status) {
		SharedPreferences.Editor editor = activity.getPreferences(
				Context.MODE_PRIVATE).edit();
		editor.putInt(
				activity.getString(R.string.shared_pref_level_status_string)
						+ LevelID, status.ordinal());
		editor.commit();
	}

	/**
	 * This method flags the level as solved and the next level as playable
	 * 
	 * @param LevelID
	 */
	public void setLevelSolved(int LevelID) {
		setStatus(LevelID, Status.SOLVED);
		// open up the next level
		if (LevelID < numLevels - 1)
			setStatus(LevelID + 1, Status.PLAYABLE);
	}

	public boolean isOpenToPlay(int LevelID) {
		Status status = getStatus(LevelID);
		return (status == Status.PLAYABLE) || (status == Status.SOLVED);
	}
}
