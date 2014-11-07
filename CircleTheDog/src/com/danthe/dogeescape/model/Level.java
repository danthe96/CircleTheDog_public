package com.danthe.dogeescape.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.danthe.dogeescape.AssetManagerProvider;
import com.danthe.dogeescape.HumanActivityListener;
import com.danthe.dogeescape.TileView;

/**
 * Yes, Danthy, games usually include a level class ;-)
 * @author Daniel
 *
 */
public class Level implements Runnable, HumanActivityListener{

	/**
	 * ID of the level to be loaded
	 */
	private final int levelID;
	private final String levelDir;
	
	/**
	 * needed to load the level specifications
	 */
	private final AssetManagerProvider assetManagerProvider;
	
	/**
	 * List of all the tiles in the Level
	 */
	private final ArrayList<Tile> tileList;
	/**
	 * Level Dimensions
	 */
	private final int tileYLength, tileXLength;


	private int turns = 0;
	public static boolean playersTurn = false;
	private boolean lost = false;
	private boolean won = false;
	

	private Thread t;
	
	private final List<Player> players;
	
	
	public Level (int levelID, AssetManagerProvider assetManagerProvider) throws IOException{
		this.levelID = levelID;
		this.assetManagerProvider = assetManagerProvider;
		levelDir = "level" + levelID + "/";
		
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				assetManagerProvider.getAssets().open(levelDir + "level.txt")));

		String[] dimension = bfr.readLine().replace(" ", "").split(",");
		tileYLength = Integer.parseInt(dimension[0]);
		tileXLength = Integer.parseInt(dimension[1]);
		tileList = new ArrayList<Tile>(tileXLength * tileYLength);
		Log.e("", tileYLength + "," + tileXLength);

		ArrayList<TileType> tileTypes = LevelLoader.readLevel(bfr,
				tileYLength, tileXLength);

		int a=0;
		for (TileType t: tileTypes) {
			a++;
			tileList.add(new Tile(a%tileXLength, a/tileXLength, t, this));
		}
		
		
		String[] enemyPos = bfr.readLine().replace(" ", "").split(",");
		players = new LinkedList<Player>();
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
		for (int i=0; i<playerPos.length-1; i+=2) {
			enemyPosition = Integer.parseInt(playerPos[i]) * tileXLength
					+ Integer.parseInt(playerPos[i+ 1]); 
			players.add(new Player(enemyPosition, tileXLength, tileYLength, tileList));
		}
		
	}
	private void updateTiles() {
		//TODO move this code into tiles
		for (int i = 0; i < tileList.size(); i++) {
			Tile tile = tileList.get(i);

			switch (tile.getTileType()) {
			case EMPTY:
				//
				break;
			case STAKE:
				//TODO Danthy erklär mir was das hier tut.
				//gameScene.unregisterTouchArea(tile);
//				if (path.contains(Integer.valueOf(i)))
//					recalculate = true;
				break;
			case ROCK:
				//
				break;
			case ICE:
				tile.setCountdown(tile.getCountdown()-1);
				if (tile.getCountdown() <= 0) {
					tile.setTileType(TileType.EMPTY);
				}
//					else
//					tile.setCurrentTileIndex(tile.getCurrentTileIndex() + 1);
				break;
			case LAVA:
//				LinkedList<Integer> neighbors = getNeighbors(i);
//				int pos = neighbors
//						.get((int) (Math.random() * neighbors.size()));
//
//				TileView nextTile = tileList.get(pos);
//				nextTile.type = TileType.LAVA;
//				nextTile.setCurrentTileIndex(6);
//				nextTile.blocked = true;
				break;
			case SWAMP:
				//
				break;
			case TURTLE:
//				LinkedList<Integer> neighbors2 = getNeighbors(i);
//				int pos2 = neighbors2.get((int) (Math.random() * neighbors2
//						.size()));
//
//				TileView nextTile2 = tileList.get(pos2);
//				nextTile2.type = TileType.TURTLE;
//				nextTile2.setCurrentTileIndex(8);
//				nextTile2.blocked = true;
//
//				tile.type = TileType.EMPTY;
//				tile.setCurrentTileIndex(0);
//				tile.blocked = false;
				break;
			}
			}
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
			updateTiles();
			for (Player p: players) {
				p.updateWay();
			}
			checkVictory();
			//TODO move enemy
			//moveEnemy();
		}

	}
	private void checkVictory() {
		if (lost || won) {
			if (won) {
//TODO move
//				enemySprite.animate(new long[] { 100, 250 },
//						new int[] { 0, 4 }, 3);
//				try {
//					Thread.sleep(1050);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);
//
//				for (int i = 0; i < 5; i++) {
//					if (highscores.get(i) == -1 || turns < highscores.get(i)) {
//						highscores.add(i, turns);
//						highscores.remove(5);
//						break;
//					}
//				}
//				SharedPreferences prefs = this.getSharedPreferences(
//						"dogeScores", Context.MODE_PRIVATE);
//				Editor edit = prefs.edit();
//				for (int i = 0; i < 5; i++) {
//					edit.putInt("key" + i, highscores.get(i));
//					edit.commit();
//				}
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


}
		
