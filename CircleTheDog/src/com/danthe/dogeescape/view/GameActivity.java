package com.danthe.dogeescape.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.danthe.dogeescape.AssetManagerProvider;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.Level;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.view.scenes.SceneManager;
import com.danthe.dogeescape.view.scenes.SceneManager.SceneType;

public class GameActivity extends BaseGameActivity  implements IOnMenuItemClickListener, AssetManagerProvider {
	private static final String TAG = "GAME_ACTIVITY";
	private static final SceneType DEFAULT_SCENE = SceneType.MAINGAME;

	private SceneManager sceneManager;

	private Camera camera;

	// TODO Move
	private LinkedList<Integer> highscores;

	public static int CAMERA_WIDTH = 720;
	public static int CAMERA_HEIGHT = 1280;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can be used as soon as there is another activity to start the game
		// this.level = getIntent().getExtras().getInt("level", 0);

		
		
		initHighscores();

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
		sceneManager.loadResources(SceneType.SPLASHSCENE);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}



	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		Log.d(TAG, "CREATE SCENE");
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());
	}

	


	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

		Log.d(TAG, "POPULATE SCENE");
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
		{
			public void onTimePassed(final TimerHandler pTimerHandler) 
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				sceneManager.loadResources(DEFAULT_SCENE,0);
				sceneManager.createScene(DEFAULT_SCENE);
				sceneManager.setCurrentScene(DEFAULT_SCENE);
			}
		}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();
		
	}





	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
//		switch (pMenuItem.getID()) {
//		case 0:
//			gameScene.clearChildScene();
//			TileView.blockInput = false;
//			return true;
//		case 1:
//			System.exit(0);
//			return true;
//		}

		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& event.getAction() == KeyEvent.ACTION_DOWN) {
//			if (!gameScene.hasChildScene()) {
//				gameScene.setChildScene(pauseMenuScene);
//				TileView.blockInput = true;
//			} else {
//				gameScene.reset();
//				TileView.blockInput = false;
//			}
//		} else {
//			return super.onKeyDown(keyCode, event);
//		}
		return false;
	}
	
	@Deprecated
	private void initHighscores() {
		highscores = new LinkedList<Integer>();
		SharedPreferences prefs = this.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);
		for (int i = 0; i < 5; i++)
			highscores.add(prefs.getInt("key" + i, -1));
	}	

	@Deprecated
	public void saveHighscores(int turns) {
		for (int i = 0; i < 5; i++) {
			if (highscores.get(i) == -1 || turns < highscores.get(i)) {
				highscores.add(i, turns);
				highscores.remove(5);
				break;
			}
		}
		SharedPreferences prefs = this.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		for (int i = 0; i < 5; i++) {
			edit.putInt("key" + i, highscores.get(i));
			edit.commit();
		}
	}







}
