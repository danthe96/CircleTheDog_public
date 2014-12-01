package com.danthe.dogeescape.model.level;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.model.level.Level.Status;

public class LevelManager {

	private static LevelManager levelManager;
	public static final int numLevels = 20;
	public static final int numLevelsPerStory = 16;
	private static final Status DEFAULT_STATUS = Status.LOCKED;
	private Activity activity;

	/**
	 * Each story is 16 levels long and should introduce something new. Like
	 * story one introduces the basic game. Story 2 introduces multiple dogs.
	 * Story 3 maybe ice.
	 * 
	 * @author Daniel
	 * 
	 */
	public enum Story {
		THE_GARDEN, MULTIPLYING_PROBLEMS;
		String[] storylines = { "The Garden", "Multiplying Problems" };

		public int[] getLevelIDs() {
			int lowestLevelID = ordinal() * numLevelsPerStory;
			int[] result = new int[Math.min(numLevels - lowestLevelID,
					numLevelsPerStory)];

			for (int i = 0; i < result.length; i++) {
				result[i] = i + lowestLevelID;
			}
			return result;
		}

		public CharSequence getOutputString() {
			return storylines[ordinal()];
		}
	}

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

		for (Story s : Story.values()) {
			if (getStatus(s.ordinal() * numLevelsPerStory) == Status.LOCKED) {
				setStatus(s.ordinal() * numLevelsPerStory, Status.PLAYABLE);
			}
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
	 * 
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
	public void setLevelSolved(int LevelID, int turns) {
		if (turns <= 5)
			setStatus(LevelID, Status.SOLVED3STAR);
		else if (turns <= 10)
			setStatus(LevelID, Status.SOLVED2STAR);
		else
			setStatus(LevelID, Status.SOLVED1STAR);

		// open up the next level
		if (LevelID < numLevels - 1 && getStatus(LevelID + 1) == Status.LOCKED)
			setStatus(LevelID + 1, Status.PLAYABLE);
	}

	public boolean isOpenToPlay(int LevelID) {
		Status status = getStatus(LevelID);
		return (status != Status.LOCKED);
	}
}
