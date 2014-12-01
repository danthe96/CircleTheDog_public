package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.TextureManager;

/**
 * This "scene" only contains the two menu buttons at the very bottom of the game scene.
 * 
 * It could probably use a lot of redundant code from the EndScene, but I have no clue what your doing there.
 * @author Daniel
 *
 */
public class GameSceneMenuButtons extends MenuScene{
	private static final String TAG = "GAME_SCENE_MENU_BUTTONS";

	public static final int BACK_ID=1; //See, Danthy, these are these CONSTANTS everyone is talking about
	public static final int RETRY_ID=2; //Not that hard?
	
	public static final float BUTTON_SIZE =0.5f;
	public static final float EXP_BUTTON_SIZE =1.1f*BUTTON_SIZE;
	
	public static final float DISTANCE_BETWEEN_BUTTONS=0.2f;
	
	
	
	public GameSceneMenuButtons(VertexBufferObjectManager vbom, Camera cam) {
		super(cam);
		Log.d(TAG, "Init");
		
		setBackgroundEnabled(false);
		
		IMenuItem back = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK_ID,
				TextureManager.backToMenuTextureReg, vbom), EXP_BUTTON_SIZE,
				BUTTON_SIZE);
//
		back.setPosition(GameActivity.CAMERA_WIDTH/2-back.getWidth()/2-DISTANCE_BETWEEN_BUTTONS*GameActivity.CAMERA_WIDTH/2, 0);
		addMenuItem(back);
		
		IMenuItem retry = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK_ID,
				TextureManager.retryTextureReg, vbom), EXP_BUTTON_SIZE,
				BUTTON_SIZE);
//
		retry.setPosition(GameActivity.CAMERA_WIDTH/2-retry.getWidth()/2+DISTANCE_BETWEEN_BUTTONS*GameActivity.CAMERA_WIDTH/2, 0);
		addMenuItem(retry);
	}
}
