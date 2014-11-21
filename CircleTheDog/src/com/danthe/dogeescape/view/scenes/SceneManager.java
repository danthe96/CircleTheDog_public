package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.Log;

import com.danthe.dogeescape.view.GameActivity;

/**
 * Class organizing the different Scenes. Ive decided to not do the game scenes
 * in native android but in the andengine aswell. Therefore well need different
 * scenes now. see:
 * http://stuartmct.co.uk/2012/07/16/andengine-scenes-and-scene-management/
 * 
 * Add different Scenes by creating different a new Sceneclass and changing the methods here.
 * 
 * @author Daniel
 * 
 */
public class SceneManager {
	private static final String TAG = "SCENE_MANAGER";

	private SceneType currentScene;
	private GameActivity activity;
	private Engine engine;
	private Camera camera;

	private Scene mainGameScene;

	private Scene splashScene;

	public enum SceneType {
		MAINGAME, SPLASHSCENE
	}

	public SceneManager(GameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
	}

	/**
	 * Method to load the game resources. Needs to be called before creating the
	 * scene.
	 * 
	 * @param sceneType
	 */
	public void loadResources(SceneType sceneType) {
		loadResources(sceneType, -1);

	}

	/**
	 * Method to load the game resources. Needs to be called before creating the
	 * scene.
	 * 
	 * @param sceneType
	 * @param levelID
	 *            only needed if you want to create the main game scene
	 */
	public void loadResources(SceneType sceneType, int levelID) {
		switch (sceneType) {
		case MAINGAME:
			if (levelID == -1)
				throw new UnsupportedOperationException(
						"Cant load resources for maingame without specified levelID!");
			GameScene.loadResources(activity, levelID);
			break;
		case SPLASHSCENE:
			SplashScene.loadSplashSceneResources(activity);
		}

	}

	public void createScene(SceneType scene) {
		switch (scene) {
		case MAINGAME:
			mainGameScene = GameScene.createScene(activity,
					activity.getVertexBufferObjectManager());
			break;
		case SPLASHSCENE:
			createSplashScene();
		}
	}

	// Method allows you to get the currently active scene
	public SceneType getCurrentSceneType() {
		return currentScene;

	}

	// Method allows you to set the currently active scene
	public void setCurrentScene(SceneType scene) {
		currentScene = scene;
		switch (scene) {
		case MAINGAME:
			engine.setScene(mainGameScene);
			Log.d(TAG, "Scene Attached: " + scene.toString());
			break;
		case SPLASHSCENE:
			engine.setScene(splashScene);
		}

	}

	public Scene createSplashScene() {
		// Create the Splash Scene and set background colour to red and add the
		// splash logo.
		splashScene = new SplashScene(camera, activity);

		return splashScene;
	}

}