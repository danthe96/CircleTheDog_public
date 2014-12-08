package com.danthe.dogeescape.view.scenes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
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

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.AssetManagerProvider;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.model.Tile.TileType;
import com.danthe.dogeescape.model.level.Level;
import com.danthe.dogeescape.view.EnemySprite;
import com.danthe.dogeescape.view.TileView;

/**
 * Content of former GameActivity class. Note that all Textures are now static.
 * 
 * @author Daniel
 * 
 */
public class GameScene extends Scene {

	private static final String TAG = "GAME_SCENE";

	// private MenuScene pauseMenu;
	private MenuButtonMenuScene menuButtons;

	private static final float MENU_POSITION_Y = 0.85f;

	private static final int GAMEOVERLAY_X = 0;
	private static final int GAMEOVERLAY_Y = 489;
	private static final int GAMEOVERLAY_WIDTH = 1080;
	private static final int GAMEOVERLAY_HEIGHT = 1080;

	private static GameScene instance = null;

	private static ITextureRegion gameFieldTextureReg;
	private static ITiledTextureRegion enemyTextureReg, tileTextureReg;

	private Level currentLevel;

	private static float graphicalTileWidth;

	private List<TileView> tileViews = new LinkedList<TileView>();
	private LinkedList<EnemySprite> enemySprites;

	public static GameScene createScene(
			AssetManagerProvider assetManagerProvider,
			VertexBufferObjectManager vertexBufferObjectManager,
			Context context, int levelID, SceneSetter sceneSetter, Camera camera) {
		instance = new GameScene(assetManagerProvider,
				vertexBufferObjectManager, context, levelID, sceneSetter,
				camera);
		return instance;
	}

	public static GameScene getInstance() {
		return instance;
	}

	private GameScene(AssetManagerProvider assetManagerProvider,
			VertexBufferObjectManager vertexBufferObjectManager,
			Context context, int levelID, SceneSetter sceneSetter, Camera cam) {
		Log.d(TAG, "CREATE SCENE");

		gameFieldTextureReg = TextureManager.gameFieldTextureReg;
		enemyTextureReg = TextureManager.enemyTextureReg;
		tileTextureReg = TextureManager.tileTextureReg;

		Sprite backgroundSprite = new Sprite(GAMEOVERLAY_X, GAMEOVERLAY_Y,
				GAMEOVERLAY_WIDTH, GAMEOVERLAY_HEIGHT, gameFieldTextureReg,
				vertexBufferObjectManager);
		attachChild(backgroundSprite);

		try {
			currentLevel = new Level(levelID, assetManagerProvider, sceneSetter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		final int BORDER = 32;
		float max = Math.max(currentLevel.getTileYLength(),
				currentLevel.getTileXLength());
		graphicalTileWidth = (GAMEOVERLAY_WIDTH - 2 * BORDER) / (max);
		float alternate = -graphicalTileWidth / 4;
		if (graphicalTileWidth * currentLevel.getTileXLength() - 2 * alternate
				+ 2 * BORDER > GAMEOVERLAY_WIDTH - 2 * BORDER) {
			float ratio = max * graphicalTileWidth
					/ (max * graphicalTileWidth - 2 * alternate);
			graphicalTileWidth *= ratio;
			alternate *= ratio;
		}

		float startingPointX = GAMEOVERLAY_X + BORDER - alternate;
		float startingPointY = GAMEOVERLAY_Y + BORDER;
		startingPointX += (GAMEOVERLAY_WIDTH - 2 * BORDER
				- currentLevel.getTileXLength() * graphicalTileWidth + 2 * alternate) / 2f;
		startingPointY += (GAMEOVERLAY_HEIGHT - 2 * BORDER - currentLevel
				.getTileYLength() * graphicalTileWidth) / 2f;

		// leave room between tiles
		graphicalTileWidth = 15 * graphicalTileWidth / 16;

		int i = 0;
		for (Tile t : currentLevel.getTileList()) {
			TileView tile = new TileView(startingPointX + alternate
					+ (graphicalTileWidth + Math.abs(alternate) / 4)
					* (i % currentLevel.getTileXLength()), startingPointY
					+ (graphicalTileWidth + Math.abs(alternate) / 4)
					* (i / currentLevel.getTileXLength()), graphicalTileWidth,
					graphicalTileWidth, tileTextureReg,
					vertexBufferObjectManager, currentLevel, t);
			if (t.getTileType() == TileType.EMPTY
					|| t.getTileType() == TileType.STAKE
					|| t.getTileType() == TileType.ICE)
				tile.setZIndex(2 * i);
			else
				tile.setZIndex(2 * i + 3);
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
			enemySprites.getLast().setZIndex(2 * p.getPosition() + 4);

			p.setChangeListener(enemySprites.getLast());

		}

		this.sortChildren();

		// initMenuButtons
		initMenuButtons(vertexBufferObjectManager, cam);

		// pauseMenu = PauseMenu.createScene(cam, context,
		// vertexBufferObjectManager);

		this.setBackgroundEnabled(false);

	}

	private void initMenuButtons(
			VertexBufferObjectManager vertexBufferObjectManager, Camera cam) {
		menuButtons = new MenuButtonMenuScene(cam, vertexBufferObjectManager,
				GameActivity.CAMERA_WIDTH, currentLevel, true);
		menuButtons.setPosition(
				(GameActivity.CAMERA_WIDTH - menuButtons.getWidth()) / 2,
				GameActivity.CAMERA_HEIGHT * MENU_POSITION_Y);
		setChildScene(menuButtons);
	}

	// currently not in use, modular loading seems to be too slow.
	public static void loadResources(final BaseGameActivity activity,
			final int levelID) {
		try {
			final List<String> levelAssets = Arrays.asList(activity
					.getResources().getAssets().list("level" + levelID + ""));

			// 1 - Set up bitmap textures
			if (levelAssets.contains("game_background.png")) {
				ITexture gameFieldTexture = new BitmapTexture(
						activity.getTextureManager(), new IInputStreamOpener() {
							@Override
							public InputStream open() throws IOException {

								return activity.getAssets().open(
										"level" + levelID
												+ "/game_background.png");
							}
						});
				gameFieldTexture.load();
				gameFieldTextureReg = TextureRegionFactory
						.extractFromTexture(gameFieldTexture);
			}

			if (levelAssets.contains("tiles.png")) {
				BitmapTextureAtlas circleBTA = new BitmapTextureAtlas(
						activity.getTextureManager(), 512, 128,
						TextureOptions.BILINEAR);
				circleBTA.load();
				tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, activity.getAssets(),
								"level" + levelID + "/tiles.png", 0, 0, 4, 1);
			}

			if (levelAssets.contains("enemy2.png")) {
				BitmapTextureAtlas enemyBTA = new BitmapTextureAtlas(
						activity.getTextureManager(), 1280, 512,
						TextureOptions.BILINEAR);
				enemyBTA.load();
				enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, activity.getAssets(),
								"level" + levelID + "/enemy2.png", 0, 0, 5, 1);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static float getGraphicalTileWidth() {
		return graphicalTileWidth;
	}

	// public void switchChildScene() {
	// if (this.getChildScene() == pauseMenu) {
	// this.setChildScene(menuButtons);
	// } else {
	// this.setChildScene(pauseMenu);
	// }
	//
	// }

	public Level getCurrentLevel() {
		return currentLevel;
	}

}
