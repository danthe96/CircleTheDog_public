package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.KeyListener;
import com.danthe.dogeescape.view.GameActivity;
import com.danthe.dogeescape.view.TileView;

/**
 * Class organizing the different Scenes. Ive decided to not do the game scenes
 * in native android but in the andengine aswell. Therefore well need different
 * scenes now. see:
 * http://stuartmct.co.uk/2012/07/16/andengine-scenes-and-scene-management/
 * 
 * Add different Scenes by creating different a new Sceneclass and changing the
 * methods here.
 * 
 * @author Daniel
 * 
 */
public class SceneManager implements IOnMenuItemClickListener, KeyListener {
	private static final String TAG = "SCENE_MANAGER";

	private SceneType currentScene;
	private GameActivity activity;
	private Engine engine;
	private Camera camera;

	private Scene mainGameScene;
	private MenuScene pauseScene;

	private Scene splashScene;

	public enum SceneType {
		MAINGAME, SPLASHSCENE, PAUSEMENUSCENE
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
			PauseMenu.loadPauseSceneResources(activity);
			break;
		case SPLASHSCENE:
			SplashScene.loadSplashSceneResources(activity);
		default:
		}

	}

	public Scene createScene(SceneType scene) {
		switch (scene) {
		case MAINGAME:
			mainGameScene = GameScene.createScene(activity,
					activity.getVertexBufferObjectManager(),
					activity.getApplicationContext());
			return mainGameScene;
		case SPLASHSCENE:
			splashScene = SplashScene.createScene(camera, activity);
			return splashScene;
		case PAUSEMENUSCENE:
			pauseScene = PauseMenu.createScene(camera,
					activity.getApplicationContext(),
					activity.getVertexBufferObjectManager());
			return pauseScene;
		}
		return null;
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
		default:
		}

	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case 0:
			mainGameScene.clearChildScene();
			TileView.blockInput = false;
			return true;
		case 1:
			System.exit(0);
			return true;
		}

		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!mainGameScene.hasChildScene()) {
				createScene(SceneType.PAUSEMENUSCENE);
				mainGameScene.setChildScene(pauseScene);
				pauseScene.setOnMenuItemClickListener(this);
				TileView.blockInput = true;
			} else {
				mainGameScene.reset();
				TileView.blockInput = false;
			}
			return true;
		}
		return false;
	}

}