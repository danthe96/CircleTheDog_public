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
import org.andengine.util.debug.Debug;

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

	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path;

	private boolean playersTurn = false;
	private int turns = 0;
	private boolean lost = false;
	private boolean won = false;
	private boolean trapped = false;
	private Tile[][] tiles;

	private ITextureRegion gameBackgroundTextureReg;
	private ITiledTextureRegion enemyTextureReg, circleTextureReg;

	private int[] enemyPosition = new int[2];
	private AnimatedSprite enemySprite;

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
				new FillResolutionPolicy(/* CAMERA_WIDTH, CAMERA_HEIGHT */),
				camera);
	}

	@Override
	protected void onCreateResources() {
		try {
			final List<String> levelAssets = Arrays.asList(getResources()
					.getAssets().list("level" + level + ""));

			// Debug.e(Arrays.toString(getResources().getAssets().list(
			// "level" + level + "")));
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

			if (levelAssets.contains("circles.png"))
				this.circleTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, this.getAssets(),
								"level" + level + "/circles.png", 0, 0, 2, 1);
			else
				this.circleTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(circleBTA, this.getAssets(),
								"gfx/circles.png", 0, 0, 2, 1);

			if (levelAssets.contains("enemy2.png"))
				this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, this.getAssets(),
								"level" + level + "/enemy2.png", 0, 0, 5, 1);
			else
				this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(enemyBTA, this.getAssets(),
								"gfx/enemy2.png", 0, 0, 5, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Scene onCreateScene() {
		gameScene = new Scene();

		try {
			String levelDir = "level" + level + "/";

			BufferedReader bfr;

			bfr = new BufferedReader(new InputStreamReader(getAssets().open(
					levelDir + "level.txt")));

			int tileYLength = Integer.parseInt(bfr.readLine());
			int tileXLength = Integer.parseInt(bfr.readLine());
			tiles = new Tile[tileYLength][tileXLength];
			Log.e("", tileYLength + "," + tileXLength);

			Sprite background2Sprite = new Sprite(22, 332, 676, 648,
					this.gameBackgroundTextureReg,
					getVertexBufferObjectManager());
			gameScene.attachChild(background2Sprite);

			int tileWidth = 576 / Math.max(tiles.length, tiles[0].length);
			int alternate = -tileWidth / 4;
			int startingPointX = 40 + Math.abs(alternate);
			int startingPointY = 350;
			if (tiles.length >= tiles[0].length)
				startingPointX += (612 - tiles[0].length * tileWidth) / 2;
			else
				startingPointY += (612 - tiles.length * tileWidth) / 2;

			for (int i = 0; i < tiles.length; i++) {
				String[] tileProperties = bfr.readLine().replace(" ", "")
						.split(",");
				for (int j = 0; j < tiles[0].length; j++) {
					int property = Integer.parseInt(tileProperties[j]);
					switch (property) {
					case 0:
						tiles[i][j] = new Tile(startingPointX + alternate
								+ (tileWidth + Math.abs(alternate) / 4) * j,
								startingPointY
										+ (tileWidth + Math.abs(alternate) / 4)
										* i, tileWidth, tileWidth,
								circleTextureReg,
								getVertexBufferObjectManager(), false, this);
						gameScene.registerTouchArea(tiles[i][j]);
						break;
					case 1:
						tiles[i][j] = new Tile(startingPointX - tileWidth * .35f
								+ alternate
								+ (tileWidth + Math.abs(alternate) / 4) * j,
								startingPointY - tileWidth / 2f
										+ (tileWidth + Math.abs(alternate) / 4)
										* i, 1.7f * tileWidth,
								1.5f * tileWidth, circleTextureReg,
								getVertexBufferObjectManager(), true, this);
						break;
					default:

					}
					gameScene.attachChild(tiles[i][j]);
				}
				alternate = -alternate;
			}

			enemySprite = new AnimatedSprite(
					tiles[tiles.length / 2][tiles[0].length / 2].getX(),
					tiles[tiles.length / 2][tiles[0].length / 2].getY() - 9
							* tileWidth / 8, tileWidth, 2 * tileWidth,
					enemyTextureReg, getVertexBufferObjectManager());
			enemyPosition = new int[] { tiles.length / 2, tiles[0].length / 2 };
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

	public boolean blockTile(Tile tile) {
		if (!playersTurn || tile.isBlocked()
				|| tiles[enemyPosition[0]][enemyPosition[1]] == tile)
			return false;

		int pos = -1;
		int max = tiles.length * tiles[0].length;
		for (int i = 0; i < max; i++) {
			if (tiles[i / tiles[0].length][i % tiles[0].length] == tile) {
				pos = i;
				break;
			}
		}

		if (path.contains(Integer.valueOf(pos)))
			recalculate = true;

		playersTurn = false;
		turns++;
		return true;
	}

	private LinkedList<Integer[]> getNeighbors(int tileRow, int tileCol) {
		LinkedList<Integer[]> neighbors = new LinkedList<Integer[]>();
		// Hall of fame: Grade A bullshit
		// int add = tileRow % 2 == 1 ? 1 : 0
		int add = tileRow % 2;

		neighbors.add(new Integer[] { tileRow - 1, tileCol - 1 + add });
		neighbors.add(new Integer[] { tileRow - 1, tileCol + add });
		neighbors.add(new Integer[] { tileRow, tileCol + 1 });
		neighbors.add(new Integer[] { tileRow + 1, tileCol - 1 + add });
		neighbors.add(new Integer[] { tileRow + 1, tileCol + add });
		neighbors.add(new Integer[] { tileRow, tileCol - 1 });

		for (Iterator<Integer[]> iter = neighbors.iterator(); iter.hasNext();) {
			Integer[] i = iter.next();
			if (i[0] < 0 || i[0] >= tiles.length || i[1] < 0
					|| i[1] >= tiles[0].length || tiles[i[0]][i[1]].isBlocked())
				iter.remove();
		}

		return neighbors;

	}

	private void doDijkstra(int startRow, int startCol) {
		int w = tiles[0].length;
		int h = tiles.length;
		distance = new int[h * w];
		previous = new int[h * w];
		distance[startRow * w + startCol] = 0;
		LinkedList<Integer> q = new LinkedList<Integer>();
		for (int i = 0; i < distance.length; i++) {
			if (!(i / w == startRow && i % w == startCol)) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}
			if (!tiles[i / w][i % w].isBlocked())
				q.add(i);
		}

		while (!q.isEmpty()) {
			// Debug.e("size " + q.size());
			int u = q.get(0);
			for (int i = 0; i < distance.length; i++) {
				if (distance[i] < distance[u] && q.contains((Integer) i))
					u = i;
			}

			/* boolean check = */q.remove((Integer) u);
			// if (!check)
			// Debug.e("Field " + u + ". Blocked?"
			// + tiles[u / 9][u % 9].isBlocked());

			if (distance[u] < Integer.MAX_VALUE) {

				LinkedList<Integer[]> neighbors = getNeighbors(u / w, u % w);
				for (Integer[] i : neighbors) {
					int alt = distance[u] + 1;
					if (alt < distance[i[0] * w + i[1]]) {
						distance[i[0] * w + i[1]] = alt;
						previous[i[0] * w + i[1]] = u;
					}
				}
			}

		}

	}

	private void calculateWay() {
		doDijkstra(enemyPosition[0], enemyPosition[1]);
		int w = tiles[0].length;
		int h = tiles.length;

		int pos = w - 1;
		for (int i = 0; i < w * h; i++)
			if ((i / w <= 0 || i / w >= h - 1 || i % w <= 0 || i % w >= w - 1)
					&& !tiles[i / w][i % w].isBlocked()) {
				pos = i;
				break;
			}

		int unreachable = 0;
		for (int i = 1; i < h; i++) {

			if (!tiles[i][0].isBlocked() && distance[w * i] < distance[pos])
				pos = w * i;
			else if (distance[w * i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tiles[(h - 1) - i][(w - 1)].isBlocked()
					&& distance[((h - 1) - i) * w + (w - 1)] < distance[pos])
				pos = ((h - 1) - i) * w + (w - 1);
			else if (distance[((h - 1) - i) * w + (w - 1)] == Integer.MAX_VALUE)
				unreachable++;
		}

		for (int i = 1; i < w; i++) {

			if (!tiles[h - 1][i].isBlocked()
					&& distance[(h - 1) * w + i] < distance[pos])
				pos = (h - 1) * w + i;
			else if (distance[(h - 1) * w + i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tiles[0][(w - 1) - i].isBlocked()
					&& distance[(w - 1) - i] < distance[pos])
				pos = (w - 1) - i;
			else if (distance[(w - 1) - i] == Integer.MAX_VALUE)
				unreachable++;
		}

		if (unreachable >= 2 * (h - 1) + 2 * (w - 1)) {
			trapped = true;
			return;
		}

		// Debug.e(Arrays.toString(previous) + " \n " + pos);
		// Debug.e(Arrays.toString(distance));

		path.clear();
		int v = pos;
		while (v != enemyPosition[0] * w + enemyPosition[1]) {
			path.add(0, v);
			// Debug.e("path size " + path.size());
			// Debug.e(Arrays.toString(previous) + " \n " + pos);
			v = previous[v];
		}

		recalculate = false;
	}

	@Override
	public void run() {

		lost = false;
		won = false;
		trapped = false;
		turns = 0;
		playersTurn = true;
		recalculate = true;

		path = new LinkedList<Integer>();

		while (!t.isInterrupted() && !lost && !won) {

			if (!playersTurn) {
				Debug.e("enemy turn");

				int row = enemyPosition[0];
				int col = enemyPosition[1];

				LinkedList<Integer[]> neighbors = getNeighbors(row, col);

				if (neighbors.size() < 1) {
					won = true;
					Debug.e("won");
					break;
				}

				Integer[] nextTile = new Integer[2];
				if (recalculate) {
					calculateWay();
				}
				if (trapped) {
					int rand = (int) (Math.random() * neighbors.size());
					nextTile = neighbors.get(rand);
				} else {
					int next = path.poll();
					nextTile[0] = next / tiles[0].length;
					nextTile[1] = next % tiles[0].length;
				}

				enemySprite.setX(tiles[nextTile[0]][nextTile[1]].getX());
				enemySprite.setY(tiles[nextTile[0]][nextTile[1]].getY() - 9
						* tiles[0][0].getWidth() / 8);
				enemyPosition[0] = nextTile[0];
				enemyPosition[1] = nextTile[1];

				if (enemyPosition[0] <= 0
						|| enemyPosition[0] >= tiles.length - 1
						|| enemyPosition[1] <= 0
						|| enemyPosition[1] >= tiles[0].length - 1) {
					enemySprite.animate(new long[] { 100, 250 }, new int[] { 0,
							4 }, 3);
					try {
						Thread.sleep(1050);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);
					lost = true;
					break;
				} else if (trapped) {
					enemySprite.animate(new long[] { 250, 100 }, new int[] { 0,
							3 }, true);
				}

				playersTurn = true;

			} else {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		if (lost || won) {

			if (won) {
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
		}

	}

}
