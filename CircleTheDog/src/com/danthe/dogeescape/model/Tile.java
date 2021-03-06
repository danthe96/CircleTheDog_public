package com.danthe.dogeescape.model;

import java.util.LinkedList;
import java.util.List;

import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.interfaces.HumanActivityListener;

public class Tile {

	public enum TileType {
		EMPTY, STAKE, ROCK, BUSH, ICE, LAVA, SWAMP, TURTLE;

		public float getAlpha() {
			if (this != EMPTY)
				return 1;
			return 94 / 255f;
		}
	}

	final int x;
	final int y;

	private TileType tileType;
	private boolean blocked;

	private int countdown;

	/**
	 * A tileview must register here in order to be notified of changes of the
	 * tiles. This allows more sophisticated procedures
	 */
	private List<ChangeListener> changeListeners;
	private HumanActivityListener humanActivityListener;

	public Tile(int x, int y, TileType tileType,
			HumanActivityListener humanActivityListener) {
		this.x = x;
		this.y = y;

		this.humanActivityListener = humanActivityListener;

		changeListeners = new LinkedList<ChangeListener>();

		setTileType(tileType);
	}

	public void setTileTypeOnHumanOrder(TileType tileType) {
		humanActivityListener.onHumanActivity();
		setTileType(tileType);
		TextureManager.place_stake.play();
	}

	public void setTileType(TileType tileType) {

		switch (tileType) {
		case EMPTY:
			blocked = false;
			break;
		case STAKE:
			blocked = true;
			break;
		case ROCK:
			blocked = true;
			break;
		case BUSH:
			blocked = true;
			break;
		case ICE:
			blocked = true;
			countdown = 3;
			break;
		case LAVA:
			blocked = true;
			break;
		case SWAMP:
			blocked = false;
			break;
		case TURTLE:
			blocked = true;
			break;
		}
		this.tileType = tileType;
		alertListeners();
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
		alertListeners();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TileType getTileType() {
		return tileType;
	}

	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	/**
	 * Alert the Listeners when a major change of the tile occured (e.g. Tile
	 * Type changed)
	 */
	public void alertListeners() {
		for (ChangeListener c : changeListeners) {
			c.onStateChanged();
		}
	}
}
