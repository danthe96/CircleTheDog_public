package com.danthe.dogeescape.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.danthe.dogeescape.AssetManagerProvider;
import com.danthe.dogeescape.HumanActivityListener;

/**
 * @author Daniel
 * @author danthe
 * 
 */
public class Level implements Runnable, HumanActivityListener {

	public enum Status {
		SOLVED, PLAYABLE, LOCKED
	}

	/**
	 * ID of the level to be loaded
	 */
	private final int levelID;
	private final String levelDir;

	/**
	 * needed to load the level specifications
	 */
	// private final AssetManagerProvider assetManagerProvider;

	/**
	 * List of all the tiles in the Level
	 */
	private final ArrayList<Tile> tileList;
	/**
	 * Level Dimensions
	 */
	public final int tileYLength, tileXLength;

	private LinkedList<Integer> highscores;
	private Context context;

	private int turns = 0;
	public static boolean playersTurn = false;
	private boolean lost = false;
	private boolean won = false;

	private Thread t;

	// private GameActivity parent;

	private final List<Enemy> enemies;

	public Level(int levelID, AssetManagerProvider assetManagerProvider,
			Context context) throws IOException {
		this.levelID = levelID;
		this.context = context;

		initHighscores();
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

		int a = 0;
		for (TileType t : tileTypes) {
			a++;
			tileList.add(new Tile(a % tileXLength, a / tileXLength, t, this));
		}

		String[] enemyPos = bfr.readLine().replace(" ", "").split(",");
		enemies = new LinkedList<Enemy>();
		initPlayers(enemyPos);

		if (t != null && !isGameOver())
			while (t.isAlive()) {
				t.interrupt();
			}

		t = new Thread(this);
		t.start();
	}

	private void initPlayers(String[] playerPos) {

		int enemyPosition;
		for (int i = 0; i < playerPos.length - 1; i += 2) {
			enemyPosition = Integer.parseInt(playerPos[i]) * tileXLength
					+ Integer.parseInt(playerPos[i + 1]);
			enemies.add(new Enemy(enemyPosition, tileXLength, tileYLength,
					tileList, this));
		}

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
				// TODO Danthy erklär mir was das hier tut.
				// Das deregistriert den Bereich im Touchlistener, weil wir
				// Felder mit Hindernis nicht auf Berührung überprüfen müssen
				// Kann auch weg, wenn es sein muss.
				// gameScene.unregisterTouchArea(tile);
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

	public boolean isGameOver() {
		return won || lost;
	}

	@Override
	public void run() {

		// initialization
		lost = false;
		won = false;
		turns = 0;

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

			for (Enemy e : enemies) {
				e.recheckPath();
				e.updateWay();
			}
			checkVictory();
			for (Enemy e : enemies)
				e.move();
			updateTiles();
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
		won = true;
		lost = true;
		for (Enemy p : enemies) {
			if (!p.hasLost())
				won = false;
			if (!p.hasWon())
				lost = false;
		}

		if (lost || won) {
			if (won) {
				saveHighscores(turns);
			}
			// TODO:
			// Call ending activity
			t.interrupt();
		}
	}

	@Override
	public void onHumanActivity() {
		playersTurn = false;
	}

	private void initHighscores() {
		highscores = new LinkedList<Integer>();
		SharedPreferences prefs = context.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);
		for (int i = 0; i < 5; i++)
			highscores.add(prefs.getInt("key" + i, -1));
	}

	private void saveHighscores(int turns) {
		for (int i = 0; i < 5; i++) {
			if (highscores.get(i) == -1 || turns < highscores.get(i)) {
				highscores.add(i, turns);
				highscores.remove(5);
				break;
			}
		}
		SharedPreferences prefs = context.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		for (int i = 0; i < 5; i++) {
			edit.putInt("key" + i, highscores.get(i));
			edit.commit();
		}
	}

}
