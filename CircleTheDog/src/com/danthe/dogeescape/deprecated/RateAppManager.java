package com.danthe.dogeescape.deprecated;

import android.content.Context;
import android.content.SharedPreferences;

public class RateAppManager {
	public static final int numWonGamesTillPrompt = 4;

	public static boolean bRateNow(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("AppRater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return false;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long wonGamesCount = prefs.getLong("win_count", 0) + 1;
		System.out.println("win_count " + wonGamesCount);
		editor.putLong("win_count", wonGamesCount);

		// Wait at least n days before opening
		if (wonGamesCount >= numWonGamesTillPrompt) {
			editor.putLong("win_count", -1);
			editor.commit();

			return true;
		}

		editor.commit();

		return false;
	}

	public static void neverPromptAgain(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("AppRater", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("dontshowagain", true);
		editor.commit();

	}

	public static void clear(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("AppRater", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("dontshowagain", false);
		editor.commit();
	}

}
