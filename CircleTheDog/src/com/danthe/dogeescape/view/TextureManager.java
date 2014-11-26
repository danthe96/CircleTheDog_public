package com.danthe.dogeescape.view;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.util.Log;

/**
 * Idea: This class should hold all textures.
 * 
 * Right now it only holds the textures of the LevelSelectScene.
 * 
 * @author Daniel
 * 
 */
public class TextureManager {
	private static final String TAG = "TEXTURE_MANAGER";

	public static BitmapTextureAtlas levelSelectBitmapTextureAtlas;
	public static TextureRegion levelSelectOpen, levelSelectSolved,
			levelSelectLocked;
	
	
	public static TextureRegion appBackground;
	private static ITexture appBackgroundTexture;

	public static void init(BaseGameActivity activity) {
		Log.d(TAG, "INIT");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		initLevelSelectResources(activity);
		initMainGraphics(activity);
	}



	public static void load() {
		Log.d(TAG, "LOAD");
		loadLevelSelectResources();
		loadMainTextures();

	}




	private static void loadLevelSelectResources() {
		Log.d(TAG, "LOAD levelSelect");

		levelSelectBitmapTextureAtlas.load();

	}

	private static void initLevelSelectResources(BaseGameActivity activity) {
		Log.d(TAG, "INIT levelSelect");
		levelSelectBitmapTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 768, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		levelSelectOpen = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectOpen.png", 0,0);
		levelSelectSolved = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectSolved.png",0,256);
		levelSelectLocked = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectLocked.png",0,512);

	}
	
	private static void initMainGraphics(final BaseGameActivity activity) {

		Log.d(TAG, "INIT maintextures");
		try {
			appBackgroundTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open(
									"gfx/background2.png");
						}
					});
		} catch (IOException e) {

			Log.d(TAG, "INITIALIZING OF MAIN TEXTURES FAILED");
			e.printStackTrace();
		}
	}

	private static void loadMainTextures() {

		appBackgroundTexture.load();
		appBackground = TextureRegionFactory
				.extractFromTexture(appBackgroundTexture);
		
	}
}
