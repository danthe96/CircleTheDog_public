package com.danthe.dogeescape.model;

import com.danthe.dogeescape.model.Level.Status;

public class LevelManager {

	private static LevelManager levelManager = new LevelManager();
	private static final int numLevels = 20;
	
	
	public static LevelManager getInstance() {
		return levelManager;
	}
	
	public int getNumLevels() {
		return numLevels;
	}
	
	/** TODO change this block into something meaningful
	 * @param LevelID
	 * @return
	 */
	public Status getStatus(int LevelID) {
		switch(LevelID) {
		case 0:case 1: return Status.SOLVED;
		case 2:case 3:case 4: return Status.PLAYABLE;
		default: return Status.LOCKED;
		}
	}
}
