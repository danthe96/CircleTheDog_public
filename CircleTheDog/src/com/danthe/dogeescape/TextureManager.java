package com.danthe.dogeescape;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
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
	private static Texture appBackgroundTexture;

	// LevelSelectScene
	private static BitmapTextureAtlas levelSelectBTA;
	public static TextureRegion levelSelectOpen, levelSelectSolved,
			levelSelectLocked;

	// GameScene
	private static Texture gameFieldTexture;
	public static TextureRegion gameFieldTextureReg;
	private static BitmapTextureAtlas tilesBTA, enemyBTA;
	public static TiledTextureRegion enemyTextureReg, tileTextureReg;

	// PauseMenu
	private static Texture textBoxTexture;
	public static TextureRegion textBoxTextureReg;
	public static Font defaultFont, defaultBigFont, comicSansFont;

	// EndScene
	private static BitmapTextureAtlas endButtonsBTA;
	private static Texture endScreenTexture;
	public static TextureRegion endScreenTextureReg, backToMenuTextureReg,
			retryTextureReg, nextTextureReg;

	public static void init(BaseGameActivity activity) {
		Log.d(TAG, "INIT");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		initLevelSelectResources(activity);
		initMainTextures(activity);
		initMenuTextures(activity);
		initGameTexture(activity);
		initEndTextures(activity);
	}

	public static void load() {
		Log.d(TAG, "LOAD");
		loadLevelSelectResources();
		loadMainTextures();
		loadMenuTextures();
		loadGameTextures();
		loadEndTextures();
	}

	private static void initLevelSelectResources(BaseGameActivity activity) {
		Log.d(TAG, "INIT levelSelect");
		levelSelectBTA = new BitmapTextureAtlas(activity.getTextureManager(),
				256, 768, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		levelSelectOpen = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBTA, activity,
						"levelSelectOpen.png", 0, 0);
		levelSelectSolved = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBTA, activity,
						"levelSelectSolved.png", 0, 256);
		levelSelectLocked = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(levelSelectBTA, activity,
						"levelSelectLocked.png", 0, 512);
	}

	private static void loadLevelSelectResources() {
		Log.d(TAG, "LOAD levelSelect");

		levelSelectBTA.load();
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

		defaultFont = FontFactory.create(activity.getFontManager(),
				activity.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR, Typeface.DEFAULT_BOLD, 42f, true,
				Color.WHITE_ARGB_PACKED_INT);
		activity.getFontManager().loadFont(defaultFont);
		
		defaultBigFont = FontFactory.create(activity.getFontManager(),
				activity.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR, Typeface.DEFAULT_BOLD, 64f, true,
				Color.WHITE_ARGB_PACKED_INT);
		activity.getFontManager().loadFont(defaultBigFont);

	}

	private static void loadMenuTextures() {
		Log.d(TAG, "LOAD menu textures");

		textBoxTexture.load();
		textBoxTextureReg = TextureRegionFactory
				.extractFromTexture(textBoxTexture);

		comicSansFont.getTexture().load();
		defaultFont.getTexture().load();
		defaultBigFont.getTexture().load();
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

		tilesBTA = new BitmapTextureAtlas(activity.getTextureManager(), 896,
				128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		tileTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(tilesBTA, activity.getAssets(),
						"tiles.png", 0, 0, 7, 1);

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

		tilesBTA.load();
		enemyBTA.load();
	}

	private static void initEndTextures(final BaseGameActivity activity) {
		Log.d(TAG, "INIT end textures");

		try {
			endScreenTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open(
									"gfx/end_screen.png");
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		endButtonsBTA = new BitmapTextureAtlas(activity.getTextureManager(),
				256, 768, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		backToMenuTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(endButtonsBTA, activity, "menu.png", 0, 0);
		nextTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(endButtonsBTA, activity, "next.png", 0, 256);
		retryTextureReg = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(endButtonsBTA, activity, "retry.png", 0, 512);

	}

	private static void loadEndTextures() {
		Log.d(TAG, "LOAD end textures");

		endButtonsBTA.load();

		endScreenTexture.load();
		endScreenTextureReg = TextureRegionFactory
				.extractFromTexture(endScreenTexture);
	}

}