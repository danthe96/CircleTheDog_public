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
import com.danthe.dogeescape.interfaces.IResourceProvider;
import com.danthe.dogeescape.interfaces.KeyListener;
import com.danthe.dogeescape.model.level.LevelManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class GameActivity extends BaseGameActivity implements
		AssetManagerProvider, IResourceProvider {

	private static final String TAG = "GAME_ACTIVITY";
	private static final String AD_UNIT_ID = "**REMOVED**";

	private static final SceneType DEFAULT_SCENE = SceneType.STORYSELECTSCENE;

	private SceneManager sceneManager;
	private LinkedList<KeyListener> keyListeners = new LinkedList<KeyListener>();

	private Camera camera;

	public static int CAMERA_WIDTH = 1080;
	public static int CAMERA_HEIGHT = 1920;

	private InterstitialAd ad;
	private boolean adShown = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can be used as soon as there is another activity to start the game
		// this.level = getIntent().getExtras().getInt("level", 0);

		// AdMob
		ad = new InterstitialAd(this.getApplicationContext());
		ad.setAdUnitId(AD_UNIT_ID);

		AdRequest adRequest = new AdRequest.Builder().build();
		ad.loadAd(adRequest);

		// Google Analytics
		Tracker.init(this);
		SupportiveMessageManager.init(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Google Analytics
		Tracker.getInstance().triggerOnStart();
	}

	@Override
	public void onStop() {
		super.onStop();

		// Google Analytics
		Tracker.getInstance().triggerOnStop();
	}

	@Override
	protected synchronized void onResume() {
		mEngine.getSoundManager().onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mEngine.getSoundManager().onPause();
		super.onPause();
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
				camera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
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
						sceneManager.initAndSetMotherScene();
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

	public boolean displayAd() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (ad.isLoaded()) {
					ad.show();
					adShown = true;
					synchronized (this) {
						this.notify();
					}
				} else {
					adShown = false;
					synchronized (this) {
						this.notify();
					}
					AdRequest adRequest = new AdRequest.Builder().build();
					ad.loadAd(adRequest);
				}
			}
		};
		try {
			synchronized (r) {
				this.runOnUiThread(r);
				r.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (adShown)
			return true;
		else
			return false;
	}
}
