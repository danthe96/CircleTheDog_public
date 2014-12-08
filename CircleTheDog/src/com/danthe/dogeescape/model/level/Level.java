package com.danthe.dogeescape.model.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.andengine.util.debug.Debug;

import android.util.Log;

import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.Tracker;
import com.danthe.dogeescape.Tracker.LevelSuccess;
import com.danthe.dogeescape.interfaces.AssetManagerProvider;
import com.danthe.dogeescape.interfaces.HumanActivityListener;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.model.Tile.TileType;

/**
 * @author Daniel
 * @author danthe
 * 
 */
public class Level implements Runnable, HumanActivityListener {

	public enum Status {
		LOCKED, PLAYABLE, SOLVED1STAR, SOLVED2STAR, SOLVED3STAR
	}

	/**
	 * ID of the level to be loaded
	 */
	public final int levelID;
	private final String levelDir;

	/**
	 * needed to load the level specifications
	 */
	// private final AssetManagerProvider assetManagerProvider;

	/**
	 * List of all the tiles in the Level
	 */
	private ArrayList<Tile> tileList;
	/**
	 * Level Dimensions
	 */
	public final int tileYLength, tileXLength;

	public int turns = 0;
	public boolean playersTurn = false;
	public static boolean lost = false;
	public static boolean won = false;

	private static Thread t;

	private SceneSetter sceneSetter;

	private final List<Enemy> enemies;

	public Level(int levelID, AssetManagerProvider assetManagerProvider,
			SceneSetter sceneSetter) throws IOException {
		this.levelID = levelID;
		this.sceneSetter = sceneSetter;

		// this.assetManagerProvider = assetManagerProvider;
		levelDir = "level" + this.levelID + "/";
		// this.parent = parent;

		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				assetManagerProvider.getAssets().open(levelDir + "level.txt")));

		String[] dimension = bfr.readLine().replace(" ", "").split(",");
		tileYLength = Integer.parseInt(dimension[0]);
		tileXLength = Integer.parseInt(dimension[1]);
		tileList = new ArrayList<Tile>(tileXLength * tileYLength);
		Log.e("", tileYLength + "," + tileXLength);

		ArrayList<TileType> tileTypes = LevelLoader.readLevel(bfr, tileYLength,
				tileXLength);

		int i = 0;
		for (TileType t : tileTypes) {
			i++;
			tileList.add(new Tile(i % tileXLength, i / tileXLength, t, this));
		}

		enemies = new LinkedList<Enemy>();
		String str;
		while ((str = bfr.readLine()) != null) {
			String[] pos = str.split(",");
			int enemyPosition = Integer.parseInt(pos[0]) * tileXLength
					+ Integer.parseInt(pos[1]);
			enemies.add(new Enemy(enemyPosition, tileXLength, tileYLength,
					tileList, this));
		}

		if (t != null) {
			while (t.isAlive()) {
				t.interrupt();
			}
		}

		t = new Thread(this);
		t.start();
	}

	private void updateTiles() {
		// TODO move this code into tiles
		for (int i = 0; i < tileList.size(); i++) {
			Tile tile = tileList.get(i);

			switch (tile.getTileType()) {
			case EMPTY:
				//
				break;
			case STAKE:

				break;
			case ROCK:
				//
				break;
			case BUSH:
				break;
			case ICE:
				tile.setCountdown(tile.getCountdown() - 1);
				if (tile.getCountdown() <= 0) {
					tile.setTileType(TileType.EMPTY);
				}
				break;
			case LAVA:
				LinkedList<Integer> neighbors = getNeighbors(i);
				int pos = neighbors
						.get((int) (Math.random() * neighbors.size()));

				Tile nextTile = tileList.get(pos);
				nextTile.setTileType(TileType.LAVA);
				break;
			case SWAMP:
				//
				break;
			case TURTLE:
				LinkedList<Integer> neighbors2 = getNeighbors(i);
				int pos2 = neighbors2.get((int) (Math.random() * neighbors2
						.size()));

				Tile nextTile2 = tileList.get(pos2);
				nextTile2.setTileType(TileType.TURTLE);

				tile.setTileType(TileType.EMPTY);
				break;
			}
		}
	}

	public List<Enemy> getEnemyList() {
		return enemies;
	}

	public int getTileYLength() {
		return tileYLength;
	}

	public int getTileXLength() {
		return tileXLength;
	}

	public ArrayList<Tile> getTileList() {
		return tileList;
	}

	@Override
	public void run() {

		// initialization
		lost = false;
		won = false;
		turns = 1;
		playersTurn = true;

		// Main loop
		main: while (!t.isInterrupted() && !(lost || won)) {
			while (playersTurn) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					continue main;
				}
			}

			for (Enemy e : enemies) {
				if (!t.isInterrupted()) {
					Debug.d(e + " checking in");
					e.recheckPath();
					e.updateWay();
					checkVictory();
					if (e.hasLost() || e.hasWon())
						continue;
					e.move();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {
						continue main;
					}
				}
			}
			checkVictory();
			updateTiles();
			turns++;
			playersTurn = true;
		}

	}

	public LinkedList<Integer> getNeighbors(int index) {
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
			if (tileList.get(i).isBlocked())
				iter.remove();
		}

		return neighbors;

	}

	private void checkVictory() {
		if (!(won || lost)) {
			won = true;
			lost = false;
			for (Enemy e : enemies) {
				if (!e.hasLost())
					won = false;
				if (e.hasWon())
					lost = true;
			}

			if (lost || won) {
				if (won) {
					LevelManager.getInstance().setLevelSolved(levelID, turns);
					Tracker.getInstance().triggerLevel(levelID,
							LevelSuccess.WIN, turns);
				} else if (lost) {
					Tracker.getInstance().triggerLevel(levelID,
							LevelSuccess.FAIL, turns);
				}
				try {
					Thread.sleep(750);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sceneSetter.setScene(SceneType.ENDSCENE);
				t.interrupt();
			}
		}
	}

	@Override
	public void onHumanActivity() {
		playersTurn = false;
	}

	public boolean enemyOnTile(Tile tile) {
		for (Enemy e : enemies) {
			if (e.getPosition() == tileList.indexOf(tile))
				return true;
		}
		return false;
	}

	public boolean enemyOnTile(int position) {
		for (Enemy e : enemies) {
			if (e.getPosition() == position)
				return true;
		}
		return false;
	}

}
