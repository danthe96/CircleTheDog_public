package com.danthe.dogeescape;

import java.util.LinkedList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.interfaces.AssetManagerProvider;
import com.danthe.dogeescape.interfaces.KeyListener;
import com.danthe.dogeescape.model.level.LevelManager;
import com.google.analytics.tracking.android.EasyTracker;

public class GameActivity extends BaseGameActivity implements
		AssetManagerProvider {
	private static final String TAG = "GAME_ACTIVITY";
	private static final SceneType DEFAULT_SCENE = SceneType.STORYSELECTSCENE;

	private SceneManager sceneManager;
	private LinkedList<KeyListener> keyListeners = new LinkedList<KeyListener>();

	private Camera camera;

	public static int CAMERA_WIDTH = 720;
	public static int CAMERA_HEIGHT = 1280;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can be used as soon as there is another activity to start the game
		// this.level = getIntent().getExtras().getInt("level", 0);

		// Google Analytics

	}

	@Override
	public void onStart() {
		super.onStop();

		// Google Analytics
		EasyTracker.getInstance(getApplicationContext()).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();

		// Google Analytics
		EasyTracker.getInstance(getApplicationContext()).activityStop(this);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {

		sceneManager = new SceneManager(this, mEngine, camera);
		keyListeners.add(sceneManager);
		sceneManager.loadResources(SceneType.SPLASHSCENE);

		LevelManager.init(this);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		Log.d(TAG, "CREATE SCENE");
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager
				.createScene(SceneType.SPLASHSCENE));
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

		Log.d(TAG, "POPULATE SCENE");
		mEngine.registerUpdateHandler(new TimerHandler(1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						sceneManager.loadTextureManagerResources();
						sceneManager.loadResources(DEFAULT_SCENE, 0);
						sceneManager.createScene(DEFAULT_SCENE);
						sceneManager.setScene(DEFAULT_SCENE);
					}
				}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		for (KeyListener k : keyListeners) {
			if (k.onKeyDown(keyCode, event))
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
