package com.danthe.dogeescape;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;

import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.interfaces.KeyListener;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.LevelManager.Story;
import com.danthe.dogeescape.view.TileView;
import com.danthe.dogeescape.view.scenes.EndScene;
import com.danthe.dogeescape.view.scenes.GameScene;
import com.danthe.dogeescape.view.scenes.LevelSelectScene;
import com.danthe.dogeescape.view.scenes.SplashScene;
import com.danthe.dogeescape.view.scenes.StorySelectScene;

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
public class SceneManager implements KeyListener, SceneSetter {
	private static final String TAG = "SCENE_MANAGER";

	private static final Story DEFAULT_STORY = Story.THE_GARDEN;

	private static SceneSetter sceneSetter;

	private SceneType currentScene;
	private GameActivity activity;
	private Engine engine;
	private Camera camera;

	private GameScene mainGameScene;
	private Scene endScene;
	private Scene levelSelectScene;
	private Scene storySelectScene;

	private Scene splashScene;

	private int currentLevelID = 0;
	private Story currentStory = DEFAULT_STORY;

	public enum SceneType {
		MAINGAME, SPLASHSCENE, LEVELSELECTSCENE, ENDSCENE, STORYSELECTSCENE
	}

	public SceneManager(GameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;

		sceneSetter = this;

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
					activity.getApplicationContext(), currentLevelID, this,
					camera);
			return mainGameScene;
		case SPLASHSCENE:
			splashScene = SplashScene.createScene(camera, activity);
			return splashScene;
		case LEVELSELECTSCENE:
			levelSelectScene = LevelSelectScene.createScene(
					activity.getVertexBufferObjectManager(), camera, this,
					currentStory);
			return levelSelectScene;
		case ENDSCENE:
			endScene = EndScene.createScene(activity.getApplicationContext(),
					camera, activity.getVertexBufferObjectManager(),
					currentLevelID);
			return endScene;
		case STORYSELECTSCENE:
			storySelectScene = StorySelectScene.createScene(
					activity.getVertexBufferObjectManager(), camera, this);
			return storySelectScene;
		default:
			throw new RuntimeException("Tried to create unknown scene: "
					+ scene);
		}
	}

	// Method allows you to get the currently active scene
	public SceneType getCurrentSceneType() {
		return currentScene;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& currentScene != SceneType.STORYSELECTSCENE) {
			if (currentScene == SceneType.MAINGAME) {
					mainGameScene.switchChildScene();
					TileView.blockInput = !TileView.blockInput;
			} else {
				this.setScene(SceneType.STORYSELECTSCENE);
			}
			return true;
		}
		return false;
	}

	public static SceneSetter getSceneSetter() {
		return sceneSetter;
	}

	@Override
	public void setLevelScene(int LevelID) {
		currentLevelID = LevelID;
		// loadResources(SceneType.MAINGAME, currentLevelID);
		setScene(SceneType.MAINGAME);
	}

	@Override
	public void setLevelSelectScene(Story selectedStory) {
		currentStory = selectedStory;
		setScene(SceneType.LEVELSELECTSCENE);

	}

	@Override
	public void setScene(SceneType scene) {
		createScene(scene);

		Log.d(TAG, "Scene Attached: " + scene.toString());
		switch (scene) {
		case MAINGAME:
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
		case STORYSELECTSCENE:
			engine.setScene(storySelectScene);
			break;
		default:
			throw new RuntimeException("Tried to set unknown scene " + scene);
		}

		currentScene = scene;
	}

}