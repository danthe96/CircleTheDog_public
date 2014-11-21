package com.danthe.dogeescape.deprecated;

import android.content.Context;
import android.content.SharedPreferences;

public class RateAppManager {
	static final int numWonGamesTillPrompt = 5;

	static boolean bRateNow(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("apprater", 0);
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

	static void neverPromptAgain(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("apprater", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("dontshowagain", true);
		editor.commit();

	}

	static void clear(Context c) {
		SharedPreferences prefs = c.getSharedPreferences("apprater", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("dontshowagain", false);
		editor.commit();
	}

}
