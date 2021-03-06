package com.danthe.dogeescape.model.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	private int[][] levelRanking = new int[numLevels][2];

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

		/**
		 * Hardcoded strings should not be used
		 */
		// public CharSequence getOutputString() {
		// return storylines[ordinal()];
		// }

		/**
		 * String to be used in SharedPreferences
		 * 
		 * @return
		 */
		public String getSaveString() {
			return "NUM_SOLVED_LEVELS_" + toString();
		}

		public int[] getTutorialRange() {
			switch (this) {
			case THE_GARDEN:
				int[] result = { 0, 4 };
				return result;
			case MULTIPLYING_PROBLEMS:
				int[] result2 = { 4, 6 };
				return result2;
			}

			return null;
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

		/**
		 * TODO: load 3 star ranking
		 */

		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					activity.getAssets().open("rankings.txt")));

			for (int i = 0; i < levelRanking.length; i++) {
				String s;
				if ((s = bfr.readLine()) == null)
					break;
				if (s.charAt(0) == '#') {
					i--;
					continue;
				}

				String[] rankings = s.replace(" ", "").split(",");
				levelRanking[i][0] = Integer.parseInt(rankings[0]);
				levelRanking[i][1] = Integer.parseInt(rankings[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	 * gets the Status of the game. if the Status of a level is not set yet
	 * (occurs when it was solved), the DefaultStatus is returned, which is
	 * determined by the number of solved levels.
	 * 
	 * @param LevelID
	 * @return
	 */
	public Status getStatus(int levelID) {
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		Status defaultValue = getDefaultStatus(levelID);
		Status result = Status.values()[sharedPref.getInt(
				activity.getString(R.string.shared_pref_level_status_string)
						+ levelID, defaultValue.ordinal())];

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
	private void setStatus(int levelID, Status status) {
		SharedPreferences.Editor editor = activity.getPreferences(
				Context.MODE_PRIVATE).edit();
		editor.putInt(
				activity.getString(R.string.shared_pref_level_status_string)
						+ levelID, status.ordinal());

		editor.commit();
	}

	/**
	 * This method flags the level as solved and increases the amount of solved
	 * levels, what results in the unlocking of a new level
	 * 
	 * @param LevelID
	 */
	public void setLevelSolved(int levelID, int turns) {
		// open up the next level if current level hasn't been solved before
		Status status = getStatus(levelID);
		if (status == Status.PLAYABLE)
			increaseNumSolvedLevels(getStoryForLevelID(levelID));

		if (turns <= levelRanking[levelID][0])
			setStatus(levelID, Status.SOLVED3STAR);
		else if (turns <= levelRanking[levelID][1]
				&& status != Status.SOLVED3STAR)
			setStatus(levelID, Status.SOLVED2STAR);
		else if (status != Status.SOLVED3STAR && status != Status.SOLVED2STAR)
			setStatus(levelID, Status.SOLVED1STAR);

	}

	private void increaseNumSolvedLevels(Story story) {
		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		int solvedLevels = sharedPref.getInt(story.getSaveString(), 0);

		if (solvedLevels < numLevelsPerStory)
			editor.putInt(story.getSaveString(), solvedLevels + 1);

		editor.commit();

	}

	public boolean isOpenToPlay(int levelID) {
		return levelID < numLevels && getStatus(levelID) != Status.LOCKED;
	}

	private Story getStoryForLevelID(int levelID) {
		return Story.values()[levelID / numLevelsPerStory];
	}
}
