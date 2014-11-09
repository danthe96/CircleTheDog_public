package com.danthe.dogeescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.Level;
import com.danthe.dogeescape.model.Tile;

public class GameActivity extends SimpleBaseGameActivity implements
		AssetManagerProvider {

	Scene gameScene;
	private Level currentLevel;

	// TODO Move
	private LinkedList<Integer> highscores;

	private int graphicalTileWidth;

	private ITextureRegion gameBackgroundTextureReg;
	private ITiledTextureRegion enemyTextureReg, tileTextureReg;

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	private List<TileView> tileViews = new LinkedList<TileView>();
	private LinkedList<EnemySprite> enemySprites;

	private int levelID = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can be used as soon as there is another activity to start the game
		// this.level = getIntent().getExtras().getInt("level", 0);

		highscores = new LinkedList<Integer>();
		SharedPreferences prefs = this.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);
		for (int i = 0; i < 5; i++)
			highscores.add(prefs.getInt("key" + i, -1));

	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(), camera);
	}

	@Override
	protected void onCreateResources() {
		try {
			final List<String> levelAssets = Arrays.asList(getResources()
					.getAssets().list("level" + levelID + ""));

			// 1 - Set up bitmap textures
			ITexture gameBackgroundTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							if (levelAssets.contains("game_background.png"))
								return getAssets().open(
										"level" + levelID
												+ "/game_background.png");
							else
								return getAssets().open(
										"gfx/game_background.png");
						}
					});

			BitmapTextureAtlas enemyBTA = new BitmapTextureAtlas(
					this.getTextureManager(), 1280, 512,
					TextureOptions.BILINEAR);
			BitmapTextureAtlas circleBTA = new BitmapTextureAtlas(
					this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);

			// 2 - Load bitmap textures into VRAM
			gameBackgroundTexture.load();

			circleBTA.load();
			enemyBTA.load();

			// 3 - Set up texture regions
			this.gameBackgroundTextureReg = TextureRegionFactory
					.extractFromTexture(gameBackgroundTexture);

			if (levelAssets.contains("tiles.png"))
				this.tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, this.getAssets(),
								"level" + levelID + "/tiles.png", 0, 0, 2, 1);
			else
				this.tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, this.getAssets(),
								"gfx/tiles.png", 0, 0, 2, 1);

			if (levelAssets.contains("enemy2.png"))
				this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, this.getAssets(),
								"level" + levelID + "/enemy2.png", 0, 0, 5, 1);
			else
				this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, this.getAssets(),
								"gfx/enemy2.png", 0, 0, 5, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Scene onCreateScene() {
		gameScene = new Scene();

		Sprite background2Sprite = new Sprite(22, 332, 676, 648,
				this.gameBackgroundTextureReg, getVertexBufferObjectManager());
		gameScene.attachChild(background2Sprite);

		try {
			currentLevel = new Level(0, this, this);
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
					getVertexBufferObjectManager(), t);
			tile.setZIndex(2 * (i / currentLevel.getTileXLength()));
			tileViews.add(tile);
			gameScene.attachChild(tile);
			gameScene.registerTouchArea(tile);

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
					getVertexBufferObjectManager(), p, tileViews, this));
			gameScene.attachChild(enemySprites.getLast());
			enemySprites.getLast().setZIndex(
					tileViews.get(p.getPosition()).getZIndex() + 1);
			enemySprites.getLast().animate(new long[] { 200, 250 }, 0, 1, true);
			p.setChangeListener(enemySprites.getLast());

		}

		gameScene.setBackgroundEnabled(true);

		return gameScene;
	}

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

	public void resortElements() {
		gameScene.sortChildren();
	}

}
