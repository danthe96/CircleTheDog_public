package com.danthe.dogeescape.model.level;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.model.level.Level.Status;

public class LevelManager {

	private static LevelManager levelManager;
	public static final int numLevels = 32;
	public static final int numLevelsPerStory = 16;
	private static final int numOpenLevelsPerStory = 3;
	// private static final Status DEFAULT_STATUS = Status.LOCKED;
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

		/**
		 * String to be used in SharedPreferences
		 * 
		 * @return
		 */
		public String getSaveString() {
			return "NUM_SOLVED_LEVELS_" + toString();
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
	 * gets the Status of the game. if the Status of a level is not set yet
	 * (occurs when it was solved), the DefaultStatus is returned, which is
	 * determined by the number of solved levels.
	 * 
	 * @param LevelID
	 * @return
	 */
	public Status getStatus(int LevelID) {
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		Status defaultValue = getDefaultStatus(LevelID);
		Status result = Status.values()[sharedPref.getInt(
				activity.getString(R.string.shared_pref_level_status_string)
						+ LevelID, defaultValue.ordinal())];

		return result;
	}

	private Status getDefaultStatus(int levelID) {
		Story story = getStoryForLevelID(levelID);
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);

		if (sharedPref.getInt(story.getSaveString(), 0) + numOpenLevelsPerStory > levelID
				% numLevelsPerStory) {
			return Status.PLAYABLE;
		} else
			return Status.LOCKED;

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
	 * This method flags the level as solved and increases the amount of solved
	 * levels, what results in the unlocking of a new level
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
		increaseNumSolvedLevels(getStoryForLevelID(LevelID));
	}

	private void increaseNumSolvedLevels(Story story) {
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		editor.putInt(story.getSaveString(),
				sharedPref.getInt(story.getSaveString(), 0) + 1);

		editor.commit();

	}

	public boolean isOpenToPlay(int LevelID) {
		Status status = getStatus(LevelID);
		return (status != Status.LOCKED);
	}

	private Story getStoryForLevelID(int levelID) {
		return Story.values()[levelID / numLevelsPerStory];
	}
}
