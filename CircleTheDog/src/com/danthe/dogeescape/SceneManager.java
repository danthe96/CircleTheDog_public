package com.danthe.dogeescape;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.interfaces.KeyListener;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.LevelManager.Story;
import com.danthe.dogeescape.view.scenes.EndScene;
import com.danthe.dogeescape.view.scenes.GameScene;
import com.danthe.dogeescape.view.scenes.HowToScene;
import com.danthe.dogeescape.view.scenes.LevelSelectScene;
import com.danthe.dogeescape.view.scenes.MotherScene;
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

	private MotherScene motherScene;

	private SceneType currentScene;
	private GameActivity activity;
	private Engine engine;
	private Camera camera;

	private GameScene mainGameScene;
	private Scene endScene;
	private LevelSelectScene levelSelectScene;
	private Scene storySelectScene;
	private Scene tutorialScene;

	private Scene splashScene;

	private int currentLevelID = 0;
	private Story currentStory = DEFAULT_STORY;

	private Sprite filterOverlaySprite;

	public enum SceneType {
		MAINGAME, SPLASHSCENE, LEVELSELECTSCENE, ENDSCENE, STORYSELECTSCENE, TUTORIALSCENE
	}

	public SceneManager(GameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;

		sceneSetter = this;

		TextureManager.init(activity, engine.getSoundManager());
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
					currentStory, activity);
			filterOverlaySprite = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
					GameActivity.CAMERA_HEIGHT,
					TextureManager.backgroundFilterTextureReg,
					activity.getVertexBufferObjectManager());
			return levelSelectScene;
		case ENDSCENE:
			endScene = EndScene.createScene(activity, camera,
					activity.getVertexBufferObjectManager(),
					mainGameScene.getCurrentLevel());
			return endScene;
		case STORYSELECTSCENE:
			storySelectScene = StorySelectScene.createScene(
					activity.getApplicationContext(),
					activity.getVertexBufferObjectManager(), camera, this);
			return storySelectScene;
		case TUTORIALSCENE:
			int[] range = currentStory.getTutorialRange();
			tutorialScene = new HowToScene(camera,
					activity.getVertexBufferObjectManager(),
					activity.getApplicationContext(), range[0], range[1]);
			filterOverlaySprite = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
					GameActivity.CAMERA_HEIGHT,
					TextureManager.backgroundFilterTextureReg,
					activity.getVertexBufferObjectManager());
			return tutorialScene;
		default:
			throw new RuntimeException("Tried to create unknown scene: "
					+ scene);
		}
	}

	// Method allows you to get the currently active scene
	public SceneType getCurrentSceneType() {
		return currentScene;
	}

	public void initAndSetMotherScene() {

		motherScene = new MotherScene(activity.getVertexBufferObjectManager());
		engine.setScene(motherScene);

	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			TextureManager.click.play();
			if (currentScene == SceneType.MAINGAME
					|| currentScene == SceneType.ENDSCENE) {
				GameScene.stopGameSounds();
				this.setScene(SceneType.LEVELSELECTSCENE);
			} else if (currentScene != SceneType.STORYSELECTSCENE) {
				this.setScene(SceneType.STORYSELECTSCENE);
			} else {
				return false;
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
		if (filterOverlaySprite != null)
			filterOverlaySprite.detachSelf();

		Log.d(TAG, "Scene Attached: " + scene.toString());
		switch (scene) {
		case MAINGAME:
			motherScene.swapScene(mainGameScene);
			break;
		case SPLASHSCENE:
			engine.setScene(splashScene);
			break;
		case LEVELSELECTSCENE:
			motherScene.swapScene(levelSelectScene);
			if (levelSelectScene.checkForTutorial(currentStory))
				return;
			break;
		case ENDSCENE:
			if (motherScene.getChildScene() == mainGameScene) {
				mainGameScene.attachChild(filterOverlaySprite);
				mainGameScene.setChildScene(endScene);
			}
			break;
		case STORYSELECTSCENE:
			motherScene.swapScene(storySelectScene);

			break;
		case TUTORIALSCENE:
			if (motherScene.getChildScene() == levelSelectScene) {
				levelSelectScene.attachChild(filterOverlaySprite);
				levelSelectScene.setChildScene(tutorialScene);
			}
			break;
		default:
			throw new RuntimeException("Tried to set unknown scene " + scene);
		}

		currentScene = scene;
	}

}