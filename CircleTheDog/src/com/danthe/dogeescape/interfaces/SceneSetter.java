package com.danthe.dogeescape.interfaces;

import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.model.level.LevelManager.Story;

public interface SceneSetter {

	public void setLevelScene(int LevelID);
	public void setScene(SceneType sceneType);
	public void setLevelSelectScene(Story selectedStory);
	public void stopSounds();
	
}
