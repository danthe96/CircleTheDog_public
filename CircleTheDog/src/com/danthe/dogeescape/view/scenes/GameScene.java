package com.danthe.dogeescape.view.scenes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;

import com.danthe.dogeescape.AssetManagerProvider;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.Level;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.view.EnemySprite;
import com.danthe.dogeescape.view.GameActivity;
import com.danthe.dogeescape.view.TileView;

/**
 * Content of former GameActivity class. Note that all Textures are now static.
 * @author Daniel
 *
 */
public class GameScene extends Scene{
	private static final String TAG = "GAME_SCENE";
	
	private static ITextureRegion gameBackgroundTextureReg, textBoxTextureReg, appBackgroundTextureReg;
	private static ITiledTextureRegion enemyTextureReg, tileTextureReg;
	

	private Level currentLevel;
	
	private int graphicalTileWidth;

	private List<TileView> tileViews = new LinkedList<TileView>();
	private LinkedList<EnemySprite> enemySprites;
	
	
	static GameScene createScene(AssetManagerProvider assetManagerProvider, VertexBufferObjectManager vertexBufferObjectManager) {
		GameScene gameScene = new GameScene(assetManagerProvider, vertexBufferObjectManager);

		return gameScene;
	}
	private GameScene(AssetManagerProvider assetManagerProvider, VertexBufferObjectManager vertexBufferObjectManager) {
		Log.d(TAG, "CREATE SCENE");
		Sprite background = new Sprite(0,0, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT, appBackgroundTextureReg,vertexBufferObjectManager);
		setBackground(new SpriteBackground(0,0,0,background));
		
		Sprite background2Sprite = new Sprite(22, 332, 676, 648,
				this.gameBackgroundTextureReg, vertexBufferObjectManager);
		attachChild(background2Sprite);

		try {
			currentLevel = new Level(0, assetManagerProvider);
		} catch (IOException e) {
			e.printStackTrace();
		}

		graphicalTileWidth = 576 / Math.max(currentLevel.getTileYLength(),
				currentLevel.getTileXLength());
		int alternate = -graphicalTileWidth / 4;
		int startingPointX = 40 + Math.abs(alternate);
		int startingPointY = 350;
		if (currentLevel.getTileYLength() >= currentLevel.getTileXLength())
			startingPointX += (612 - currentLevel.getTileXLength()
					* graphicalTileWidth) / 2;
		else
			startingPointY += (612 - currentLevel.getTileYLength()
					* graphicalTileWidth) / 2;

		int i = 0;
		for (Tile t : currentLevel.getTileList()) {
			TileView tile = new TileView(startingPointX + alternate
					+ (graphicalTileWidth + Math.abs(alternate) / 4)
					* (i % currentLevel.getTileXLength()), startingPointY
					+ (graphicalTileWidth + Math.abs(alternate) / 4)
					* (i / currentLevel.getTileXLength()), graphicalTileWidth,
					graphicalTileWidth, tileTextureReg,
					vertexBufferObjectManager, t);
			tile.setZIndex(2 * (i / currentLevel.getTileXLength()));
			tileViews.add(tile);
			this.attachChild(tile);
			this.registerTouchArea(tile);

			if (i % currentLevel.getTileXLength() == currentLevel
					.getTileXLength() - 1)
				alternate = -alternate;
			i++;
		}

		enemySprites = new LinkedList<EnemySprite>();
		for (Enemy p : currentLevel.getEnemyList()) {
			enemySprites.add(new EnemySprite(tileViews.get(p.getPosition())
					.getX(), tileViews.get(p.getPosition()).getY() - 9
					* graphicalTileWidth / 8, graphicalTileWidth,
					2 * graphicalTileWidth, enemyTextureReg,
					vertexBufferObjectManager, p, tileViews, this));
			this.attachChild(enemySprites.getLast());
			enemySprites.getLast().setZIndex(
					tileViews.get(p.getPosition()).getZIndex() + 1);
			enemySprites.getLast().animate(new long[] { 200, 250 }, 0, 1, true);
			p.setChangeListener(enemySprites.getLast());

		}

		this.setBackgroundEnabled(true);

//		Font comicSansFont = FontFactory.create(mEngine.getFontManager(),
//				mEngine.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
//				Typeface.createFromAsset(this.getAssets(),
//						"ttf/LDFComicSans.ttf"), 46f, true,
//				Color.WHITE_ARGB_PACKED_INT);
//		this.getFontManager().loadFont(comicSansFont);
//		comicSansFont.getTexture().load();
//
//		this.pauseMenuScene = new PauseMenu(camera, this.getBaseContext(),
//				this.getVertexBufferObjectManager(), textBoxTextureReg,
//				comicSansFont);
//		pauseMenuScene.setOnMenuItemClickListener(this);

	}
	
	static void loadResources(final BaseGameActivity activity, final int levelID) {
		try {
			final List<String> levelAssets = Arrays.asList(activity.getResources()
					.getAssets().list("level" + levelID + ""));

			// 1 - Set up bitmap textures
			ITexture gameBackgroundTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							if (levelAssets.contains("game_background.png"))
								return activity.getAssets().open(
										"level" + levelID
												+ "/game_background.png");
							else
								return activity.getAssets().open(
										"gfx/game_background.png");
						}
					});
			ITexture textBoxTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/textbox.png");
						}
					});
			ITexture appBackgroundTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/background2.png");
						}
					});

			BitmapTextureAtlas enemyBTA = new BitmapTextureAtlas(
					activity.getTextureManager(), 1280, 512,
					TextureOptions.BILINEAR);
			BitmapTextureAtlas circleBTA = new BitmapTextureAtlas(
					activity.getTextureManager(), 512, 128, TextureOptions.BILINEAR);

			// 2 - Load bitmap textures into VRAM
			gameBackgroundTexture.load();
			textBoxTexture.load();
			appBackgroundTexture.load();

			circleBTA.load();
			enemyBTA.load();

			// 3 - Set up texture regions
			gameBackgroundTextureReg = TextureRegionFactory
					.extractFromTexture(gameBackgroundTexture);
			textBoxTextureReg = TextureRegionFactory
					.extractFromTexture(textBoxTexture);
			appBackgroundTextureReg = TextureRegionFactory.extractFromTexture(appBackgroundTexture);
			
			if (levelAssets.contains("tiles.png"))
				tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, activity.getAssets(),
								"level" + levelID + "/tiles.png", 0, 0, 4, 1);
			else
				tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, activity.getAssets(),
								"tiles.png", 0, 0, 4, 1);

			if (levelAssets.contains("enemy2.png"))
				enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, activity.getAssets(),
								"level" + levelID + "/enemy2.png", 0, 0, 5, 1);
			else
				enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, activity.getAssets(),
								"enemy2.png", 0, 0, 5, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
