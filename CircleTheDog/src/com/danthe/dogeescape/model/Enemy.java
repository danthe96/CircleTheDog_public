package com.danthe.dogeescape.model;

import java.util.LinkedList;
import java.util.List;

import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.model.level.Level;

public class Enemy {

	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path = new LinkedList<Integer>();

	private ChangeListener changeListener;

	private int position;

	private final List<Tile> tileList;
	private LinkedList<Integer> borderTiles;
	private boolean won = false;
	private boolean lost = false;

	private Level level;

	public Enemy(int position, int tileXLength, int tileYLength,
			List<Tile> tileList, Level level) {
		this.position = position;
		this.tileList = tileList;
		this.level = level;

		borderTiles = new LinkedList<Integer>();
		for (int i = 0; i < tileXLength; i++) {
			borderTiles.add(i);
			borderTiles.add((tileYLength - 1) * (tileXLength) + i);
		}
		for (int i = 1; i < (tileYLength - 1); i++) {
			borderTiles.add(i * tileXLength);
			borderTiles.add(i * tileXLength + (tileXLength - 1));
		}

	}

	private void calculateWay() {
		doDijkstra(position, tileList);
		LinkedList<Integer> lucrativeFields = new LinkedList<Integer>();

		for (int i : borderTiles) {
			if (!tileList.get(i).isBlocked()) {
				lucrativeFields.add(i);
				break;
			}
		}

		int unreachable = 0;

		for (int i : borderTiles) {
			if (distance[i] == Integer.MAX_VALUE)
				unreachable++;
			else if (!tileList.get(i).isBlocked()
					&& distance[i] <= distance[lucrativeFields.get(0)]) {
				if (distance[i] <= distance[lucrativeFields.get(0)])
					lucrativeFields.clear();
				lucrativeFields.add(i);
			}
		}

		if (unreachable >= borderTiles.size()) {
			lost = true;
			changeListener.onStateChanged();
			return;
		}

		// Debug.e(Arrays.toString(previous) + " \n " + pos);
		// Debug.e(Arrays.toString(distance));

		path.clear();
		int v = lucrativeFields.get((int) (Math.random() * lucrativeFields
				.size()));
		while (v != position) {
			path.add(0, v);
			// Debug.e("path size " + path.size());
			// Debug.e(Arrays.toString(previous) + " \n " + pos);
			v = previous[v];
		}

		recalculate = false;
	}

	private void doDijkstra(int index, List<Tile> tileList) {
		distance = new int[tileList.size()];
		previous = new int[tileList.size()];
		distance[index] = 0;
		LinkedList<Integer> q = new LinkedList<Integer>();
		for (int i = 0; i < distance.length; i++) {
			if (i != index) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}
			if (!tileList.get(i).isBlocked()
					&& (i == index || !level.enemyOnTile(tileList.get(i))))
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
				LinkedList<Integer> neighbors = level.getNeighbors(u);
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

	public boolean hasWon() {
		return won;
	}

	public boolean hasLost() {
		return lost;
	}

	public void move() {
		if (!(won || lost)) {
			this.position = path.poll();
			if (borderTiles.contains(position))
				won = true;
			changeListener.onStateChanged();

		}
	}

	public void setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public void recheckPath() {
		if (!recalculate) {
			for (Integer i : path) {
				if (tileList.get(i).isBlocked()
						|| level.enemyOnTile(tileList.get(i))) {
					recalculate = true;
					break;
				}
			}
		}
	}

	public void updateWay() {
		if (recalculate)
			calculateWay();
		recalculate = false;
	}

	public int getPosition() {
		return position;
	}

}
