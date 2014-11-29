package com.danthe.dogeescape;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.interfaces.KeyListener;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.view.TileView;
import com.danthe.dogeescape.view.scenes.EndScene;
import com.danthe.dogeescape.view.scenes.GameScene;
import com.danthe.dogeescape.view.scenes.LevelSelectScene;
import com.danthe.dogeescape.view.scenes.PauseMenu;
import com.danthe.dogeescape.view.scenes.SplashScene;

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
public class SceneManager implements IOnMenuItemClickListener, KeyListener,
		SceneSetter {
	private static final String TAG = "SCENE_MANAGER";

	private SceneType currentScene;
	private GameActivity activity;
	private Engine engine;
	private Camera camera;

	private Scene mainGameScene;
	private MenuScene pauseScene;
	private Scene endScene;
	private Scene levelSelectScene;

	private Scene splashScene;

	private int currentLevelID = 0;

	public enum SceneType {
		MAINGAME, SPLASHSCENE, PAUSEMENUSCENE, LEVELSELECTSCENE, ENDSCENE
	}

	public SceneManager(GameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;

		TextureManager.init(activity);
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

	public void loadTextureManagerResources() {
		TextureManager.load();
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
		default:
		}

	}

	public Scene createScene(SceneType scene) {
		switch (scene) {
		case MAINGAME:
			mainGameScene = GameScene.createScene(activity,
					activity.getVertexBufferObjectManager(),
					activity.getApplicationContext(), currentLevelID, this);
			return mainGameScene;
		case SPLASHSCENE:
			splashScene = SplashScene.createScene(camera, activity);
			return splashScene;
		case PAUSEMENUSCENE:
			pauseScene = PauseMenu.createScene(camera,
					activity.getApplicationContext(),
					activity.getVertexBufferObjectManager());
			return pauseScene;
		case LEVELSELECTSCENE:
			levelSelectScene = LevelSelectScene.createScene(
					activity.getVertexBufferObjectManager(), camera, this);
			return levelSelectScene;
		case ENDSCENE:
			endScene = EndScene.createScene(activity.getApplicationContext(),
					activity.getVertexBufferObjectManager(), this,
					currentLevelID);
			return endScene;
		}
		return null;
	}

	// Method allows you to get the currently active scene
	public SceneType getCurrentSceneType() {
		return currentScene;
	}

	@Override
	// Danthy wieso hast du diese Design-Entscheidung getroffen? Wieso soll der
	// SceneManager Men�eingaben verwalten?
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case 0:
			mainGameScene.clearChildScene();
			TileView.blockInput = false;
			return true;
		case 1:
			setScene(SceneType.LEVELSELECTSCENE);
			return true;
		}

		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& currentScene == SceneType.MAINGAME) {
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

	@Override
	public void setLevelScene(int LevelID) {
		currentLevelID = LevelID;
		// loadResources(SceneType.MAINGAME, currentLevelID);
		setScene(SceneType.MAINGAME);
	}

	@Override
	public void setScene(SceneType scene) {
		createScene(scene);

		Log.d(TAG, "Scene Attached: " + scene.toString());
		switch (scene) {
		case MAINGAME:
			createScene(SceneType.PAUSEMENUSCENE);
			engine.setScene(mainGameScene);
			break;
		case SPLASHSCENE:
			engine.setScene(splashScene);
			break;
		case LEVELSELECTSCENE:
			engine.setScene(levelSelectScene);
			break;
		case ENDSCENE:
			if (currentScene == SceneType.MAINGAME)
				mainGameScene.setChildScene(endScene);
			break;
		default:
		}

		currentScene = scene;
	}

}