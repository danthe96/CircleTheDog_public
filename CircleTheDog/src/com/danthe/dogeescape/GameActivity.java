package com.danthe.dogeescape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
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
import android.util.Log;

public class GameActivity extends SimpleBaseGameActivity implements Runnable {

	Scene gameScene;

	private Thread t;
	private LinkedList<Integer> highscores;
	private int level = 0;

	private ArrayList<Tile> tileList;
	private int tileYLength, tileXLength;
	private int graphicalTileWidth;

	private int turns = 0;
	public static boolean playersTurn = false;
	private boolean lost = false;
	private boolean won = false;

	private int enemyPosition;
	private AnimatedSprite enemySprite;

	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path;

	private ITextureRegion gameBackgroundTextureReg;
	private ITiledTextureRegion enemyTextureReg, tileTextureReg;

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

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
					.getAssets().list("level" + level + ""));

			// 1 - Set up bitmap textures
			ITexture gameBackgroundTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							if (levelAssets.contains("game_background.png"))
								return getAssets().open(
										"level" + level
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
								"level" + level + "/tiles.png", 0, 0, 2, 1);
			else
				this.tileTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, this.getAssets(),
								"gfx/tiles.png", 0, 0, 2, 1);

			if (levelAssets.contains("enemy2.png"))
				this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, this.getAssets(),
								"level" + level + "/enemy2.png", 0, 0, 5, 1);
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

			String levelDir = "level" + level + "/";
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					getAssets().open(levelDir + "level.txt")));

			String[] dimension = bfr.readLine().replace(" ", "").split(",");
			tileYLength = Integer.parseInt(dimension[0]);
			tileXLength = Integer.parseInt(dimension[1]);
			tileList = new ArrayList<Tile>(tileXLength * tileYLength);
			Log.e("", tileYLength + "," + tileXLength);

			graphicalTileWidth = 576 / Math.max(tileYLength, tileXLength);
			int alternate = -graphicalTileWidth / 4;
			int startingPointX = 40 + Math.abs(alternate);
			int startingPointY = 350;
			if (tileYLength >= tileXLength)
				startingPointX += (612 - tileXLength * graphicalTileWidth) / 2;
			else
				startingPointY += (612 - tileYLength * graphicalTileWidth) / 2;

			ArrayList<TileType> tileTypes = LevelLoader.readLevel(bfr,
					tileYLength, tileXLength);
			for (int i = 0; i < tileXLength * tileYLength; i++) {
				Tile tile = new Tile(startingPointX + alternate
						+ (graphicalTileWidth + Math.abs(alternate) / 4)
						* (i % tileXLength), startingPointY
						+ (graphicalTileWidth + Math.abs(alternate) / 4)
						* (i / tileXLength), graphicalTileWidth,
						graphicalTileWidth, tileTextureReg,
						getVertexBufferObjectManager(), tileTypes.get(i));
				tileList.add(tile);
				gameScene.attachChild(tile);
				gameScene.registerTouchArea(tile);

				if (i % tileXLength == tileXLength - 1)
					alternate = -alternate;
			}

			String[] enemyPos = bfr.readLine().replace(" ", "").split(",");
			enemyPosition = Integer.parseInt(enemyPos[0]) * tileXLength
					+ Integer.parseInt(enemyPos[1]);
			enemySprite = new AnimatedSprite(
					tileList.get(enemyPosition).getX(), tileList.get(
							enemyPosition).getY()
							- 9 * graphicalTileWidth / 8, graphicalTileWidth,
					2 * graphicalTileWidth, enemyTextureReg,
					getVertexBufferObjectManager());

			gameScene.attachChild(enemySprite);
			enemySprite.setZIndex(background2Sprite.getZIndex() + 1);
			enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);

			gameScene.setBackgroundEnabled(true);

			if (t != null && !won && !lost)
				while (t.isAlive()) {
					t.interrupt();
				}

			t = new Thread(this);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gameScene;
	}

	@Override
	public void run() {

		// initialization
		lost = false;
		won = false;
		turns = 0;

		recalculate = true;
		path = new LinkedList<Integer>();

		// Main loop
		while (!t.isInterrupted() && !(lost || won)) {
			turns++;
			playersTurn = true;
			while (playersTurn) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			updateTiles();
			if (recalculate)
				calculateWay();
			checkVictory();
			moveEnemy();
		}

	}

	private void updateTiles() {

		for (int i = 0; i < tileList.size(); i++) {
			Tile tile = tileList.get(i);

			switch (tile.type) {
			case EMPTY:
				//
				break;
			case STAKE:
				gameScene.unregisterTouchArea(tile);
				if (path.contains(Integer.valueOf(i)))
					recalculate = true;
				break;
			case ROCK:
				//
				break;
			case ICE:
				tile.countdown--;
				if (tile.countdown <= 0) {
					tile.type = TileType.EMPTY;
					tile.blocked = true;
					tile.setCurrentTileIndex(0);
				} else
					tile.setCurrentTileIndex(tile.getCurrentTileIndex() + 1);
				break;
			case LAVA:
				LinkedList<Integer> neighbors = getNeighbors(i);
				int pos = neighbors
						.get((int) (Math.random() * neighbors.size()));

				Tile nextTile = tileList.get(pos);
				nextTile.type = TileType.LAVA;
				nextTile.setCurrentTileIndex(6);
				nextTile.blocked = true;
				break;
			case SWAMP:
				//
				break;
			case TURTLE:
				LinkedList<Integer> neighbors2 = getNeighbors(i);
				int pos2 = neighbors2.get((int) (Math.random() * neighbors2
						.size()));

				Tile nextTile2 = tileList.get(pos2);
				nextTile2.type = TileType.TURTLE;
				nextTile2.setCurrentTileIndex(8);
				nextTile2.blocked = true;

				tile.type = TileType.EMPTY;
				tile.setCurrentTileIndex(0);
				tile.blocked = false;
				break;
			}

		}

	}

	private void calculateWay() {
		doDijkstra(enemyPosition);
		int w = tileXLength;
		int h = tileYLength;

		int pos = w - 1;
		for (int i = 0; i < w * h; i++) {
			if ((i / w <= 0 || i / w >= h - 1 || i % w <= 0 || i % w >= w - 1)
					&& !tileList.get(i).blocked) {
				pos = i;
				break;
			}
		}

		int unreachable = 0;
		for (int i = 1; i < h; i++) {

			if (!tileList.get(w * i).blocked && distance[w * i] < distance[pos])
				pos = w * i;
			else if (distance[w * i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tileList.get(((h - 1) - i) * w + (w - 1)).blocked
					&& distance[((h - 1) - i) * w + (w - 1)] < distance[pos])
				pos = ((h - 1) - i) * w + (w - 1);
			else if (distance[((h - 1) - i) * w + (w - 1)] == Integer.MAX_VALUE)
				unreachable++;
		}

		for (int i = 1; i < w; i++) {

			if (!tileList.get((h - 1) * w + i).blocked
					&& distance[(h - 1) * w + i] < distance[pos])
				pos = (h - 1) * w + i;
			else if (distance[(h - 1) * w + i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tileList.get((w - 1) - i).blocked
					&& distance[(w - 1) - i] < distance[pos])
				pos = (w - 1) - i;
			else if (distance[(w - 1) - i] == Integer.MAX_VALUE)
				unreachable++;
		}

		if (unreachable >= 2 * (h - 1) + 2 * (w - 1)) {
			won = true;
			return;
		}

		// Debug.e(Arrays.toString(previous) + " \n " + pos);
		// Debug.e(Arrays.toString(distance));

		path.clear();
		int v = pos;
		while (v != enemyPosition) {
			path.add(0, v);
			// Debug.e("path size " + path.size());
			// Debug.e(Arrays.toString(previous) + " \n " + pos);
			v = previous[v];
		}

		recalculate = false;
	}

	private void checkVictory() {
		if (lost || won) {
			if (won) {

				enemySprite.animate(new long[] { 100, 250 },
						new int[] { 0, 4 }, 3);
				try {
					Thread.sleep(1050);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);

				for (int i = 0; i < 5; i++) {
					if (highscores.get(i) == -1 || turns < highscores.get(i)) {
						highscores.add(i, turns);
						highscores.remove(5);
						break;
					}
				}
				SharedPreferences prefs = this.getSharedPreferences(
						"dogeScores", Context.MODE_PRIVATE);
				Editor edit = prefs.edit();
				for (int i = 0; i < 5; i++) {
					edit.putInt("key" + i, highscores.get(i));
					edit.commit();
				}
			}

			// TODO:
			// Call ending activity

			t.interrupt();
		}
	}

	private void moveEnemy() {
		if (!won) {
			int nextTile = path.poll();

			enemyPosition = nextTile;
			enemySprite.setX(tileList.get(enemyPosition).getX());
			enemySprite.setY(tileList.get(enemyPosition).getY() - 9
					* graphicalTileWidth / 8);

			if (enemyPosition / tileXLength <= 0
					|| enemyPosition / tileXLength >= tileYLength - 1
					|| enemyPosition % tileXLength <= 0
					|| enemyPosition % tileXLength >= tileXLength - 1) {
				enemySprite.animate(new long[] { 100, 250 },
						new int[] { 0, 4 }, 3);
				try {
					Thread.sleep(1050);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);
				lost = true;
			}
		}
	}

	private LinkedList<Integer> getNeighbors(int index) {
		LinkedList<Integer> neighbors = new LinkedList<Integer>();

		int tileRow = index / tileXLength;
		int tileCol = index % tileXLength;
		int add = tileRow % 2;

		if (tileRow > 0 && tileCol + add - 1 >= 0)
			neighbors.add(index - tileXLength - 1 + add);
		if (tileRow > 0 && tileCol + add < tileXLength)
			neighbors.add(index - tileXLength + add);
		if (tileCol + 1 < tileXLength)
			neighbors.add(index + 1);
		if (tileRow + 1 < tileYLength && tileCol + add < tileXLength)
			neighbors.add(index + tileXLength + add);
		if (tileRow + 1 < tileYLength && tileCol + add - 1 >= 0)
			neighbors.add(index + tileXLength - 1 + add);
		if (tileCol > 0)
			neighbors.add(index - 1);

		for (Iterator<Integer> iter = neighbors.iterator(); iter.hasNext();) {
			Integer i = iter.next();
			if (tileList.get(i).blocked)
				iter.remove();
		}

		return neighbors;

	}

	private void doDijkstra(int index) {
		distance = new int[tileList.size()];
		previous = new int[tileList.size()];
		distance[index] = 0;
		LinkedList<Integer> q = new LinkedList<Integer>();
		for (int i = 0; i < distance.length; i++) {
			if (i != index) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}
			if (!tileList.get(i).blocked)
				q.add(i);
		}

		while (!q.isEmpty()) {
			// Debug.e("size " + q.size());
			int u = q.get(0);
			for (int i = 0; i < distance.length; i++) {
				if (distance[i] < distance[u] && q.contains((Integer) i))
					u = i;
			}

			q.remove((Integer) u);

			if (distance[u] < Integer.MAX_VALUE) {
				LinkedList<Integer> neighbors = getNeighbors(u);
				for (Integer i : neighbors) {
					int alt = distance[u] + 1;
					if (alt < distance[i]) {
						distance[i] = alt;
						previous[i] = u;
					}
				}
			}

		}

	}

}
