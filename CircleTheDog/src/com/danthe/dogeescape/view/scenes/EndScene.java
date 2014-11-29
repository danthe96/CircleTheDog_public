package com.danthe.dogeescape.view.scenes;

import org.andengine.entity.scene.menu.MenuScene;

public class EndScene extends MenuScene{
	private static final String TAG = "GAME_SCENE";
	private static EndScene instance = null;
	
	public static EndScene EndScene(){
		if(instance == null)
			instance = new EndScene();
		
		return instance;		
	}
	
	private EndScene(){
		
	}

}
