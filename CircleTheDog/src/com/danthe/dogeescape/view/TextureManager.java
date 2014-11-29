package com.danthe.dogeescape.view;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;

/**
 * Idea: This class should hold all textures.
 * 
 * Right now it only holds the textures of the LevelSelectScene.
 * 
 * @author Daniel&danthe
 * 
 */
public class TextureManager {
	private static final String TAG = "TEXTURE_MANAGER";

	// Main
	public static TextureRegion appBackground;
	private static ITexture appBackgroundTexture;

	// LevelSelectScene
	public static BitmapTextureAtlas levelSelectBitmapTextureAtlas;
	public static TextureRegion levelSelectOpen, levelSelectSolved,
			levelSelectLocked;

	// GameScene
	private static ITexture gameFieldTexture;
	public static ITextureRegion gameFieldTextureReg;
	private static BitmapTextureAtlas circleBTA, enemyBTA;
	public static ITiledTextureRegion enemyTextureReg, tileTextureReg;

	// PauseMenu
	private static ITexture textBoxTexture;
	public static ITextureRegion textBoxTextureReg;
	public static Font comicSansFont;

	public static void init(BaseGameActivity activity) {
		Log.d(TAG, "INIT");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		initLevelSelectResources(activity);
		initMainTextures(activity);
		initMenuTextures(activity);
		initGameTexture(activity);
	}

	public static void load() {
		Log.d(TAG, "LOAD");
		loadLevelSelectResources();
		loadMainTextures();
		loadMenuTextures();
		loadGameTextures();
	}

	private static void initLevelSelectResources(BaseGameActivity activity) {
		Log.d(TAG, "INIT levelSelect");
		levelSelectBitmapTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 768,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		levelSelectOpen = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectOpen.png", 0, 0);
		levelSelectSolved = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectSolved.png", 0, 256);
		levelSelectLocked = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBitmapTextureAtlas, activity,
						"levelSelectLocked.png", 0, 512);
	}

	private static void loadLevelSelectResources() {
		Log.d(TAG, "LOAD levelSelect");

		levelSelectBitmapTextureAtlas.load();
	}

	private static void initMainTextures(final BaseGameActivity activity) {

		Log.d(TAG, "INIT main textures");
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
		Log.d(TAG, "LOAD main textures");

		appBackgroundTexture.load();
		appBackground = TextureRegionFactory
				.extractFromTexture(appBackgroundTexture);

	}

	private static void initMenuTextures(final BaseGameActivity activity) {
		Log.d(TAG, "INIT main textures");

		try {
			textBoxTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/textbox.png");
						}
					});

		} catch (IOException e) {
			e.printStackTrace();
		}

		comicSansFont = FontFactory.create(activity.getFontManager(), activity
				.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				Typeface.createFromAsset(activity.getAssets(),
						"ttf/LDFComicSans.ttf"), 46f, true,
				Color.WHITE_ARGB_PACKED_INT);
		activity.getFontManager().loadFont(comicSansFont);

	}

	private static void loadMenuTextures() {
		Log.d(TAG, "LOAD menu textures");

		textBoxTexture.load();
		textBoxTextureReg = TextureRegionFactory
				.extractFromTexture(textBoxTexture);

		comicSansFont.getTexture().load();
	}

	private static void initGameTexture(final BaseGameActivity activity) {
		Log.d(TAG, "INIT game textures");

		try {
			gameFieldTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open(
									"gfx/game_background.png");
						}
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		circleBTA = new BitmapTextureAtlas(activity.getTextureManager(), 512,
				128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		tileTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(circleBTA, activity.getAssets(),
						"tiles.png", 0, 0, 4, 1);

		enemyBTA = new BitmapTextureAtlas(activity.getTextureManager(), 1280,
				512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(enemyBTA, activity.getAssets(),
						"enemy2.png", 0, 0, 5, 1);

	}

	private static void loadGameTextures() {
		Log.d(TAG, "LOAD game textures");

		gameFieldTexture.load();
		gameFieldTextureReg = TextureRegionFactory
				.extractFromTexture(gameFieldTexture);

		circleBTA.load();
		enemyBTA.load();
	}

}
