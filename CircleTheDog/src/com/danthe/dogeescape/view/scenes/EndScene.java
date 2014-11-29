package com.danthe.dogeescape.view.scenes;

import org.andengine.entity.scene.Scene;

public class EndScene extends Scene{
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;
	
	public static EndScene createScene(){
		if(instance == null)
			instance = new EndScene();
		
		return instance;		
	}
	
	private EndScene(){
		
		
		
	}

}
