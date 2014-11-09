package com.danthe.dogeescape.model;

import java.util.LinkedList;
import java.util.List;

import com.danthe.dogeescape.ChangeListener;

public class Enemy {

	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path = new LinkedList<Integer>();

	private ChangeListener changeListener;

	private int position;

	private int tileXLength;
	private int tileYLength;
	private final List<Tile> tileList;
	private boolean won = false;
	private boolean lost = false;

	private Level level;

	public Enemy(int position, int tileXLength, int tileYLength,
			List<Tile> tileList, Level level) {
		this.position = position;
		this.tileList = tileList;
		this.tileXLength = tileXLength;
		this.tileYLength = tileYLength;
		this.level = level;
	}

	private void calculateWay() {
		doDijkstra(position, tileList);
		int w = tileXLength;
		int h = tileYLength;

		int pos = w - 1;
		for (int i = 0; i < w * h; i++) {
			if ((i / w <= 0 || i / w >= h - 1 || i % w <= 0 || i % w >= w - 1)
					&& !tileList.get(i).isBlocked()) {
				pos = i;
				break;
			}
		}

		int unreachable = 0;
		for (int i = 1; i < h; i++) {

			if (!tileList.get(w * i).isBlocked()
					&& distance[w * i] < distance[pos])
				pos = w * i;
			else if (distance[w * i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tileList.get(((h - 1) - i) * w + (w - 1)).isBlocked()
					&& distance[((h - 1) - i) * w + (w - 1)] < distance[pos])
				pos = ((h - 1) - i) * w + (w - 1);
			else if (distance[((h - 1) - i) * w + (w - 1)] == Integer.MAX_VALUE)
				unreachable++;
		}

		for (int i = 1; i < w; i++) {

			if (!tileList.get((h - 1) * w + i).isBlocked()
					&& distance[(h - 1) * w + i] < distance[pos])
				pos = (h - 1) * w + i;
			else if (distance[(h - 1) * w + i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tileList.get((w - 1) - i).isBlocked()
					&& distance[(w - 1) - i] < distance[pos])
				pos = (w - 1) - i;
			else if (distance[(w - 1) - i] == Integer.MAX_VALUE)
				unreachable++;
		}

		if (unreachable >= 2 * (h - 1) + 2 * (w - 1)) {
			lost = true;
			changeListener.onStateChanged();
			return;
		}

		// Debug.e(Arrays.toString(previous) + " \n " + pos);
		// Debug.e(Arrays.toString(distance));

		path.clear();
		int v = pos;
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
			if (!tileList.get(i).isBlocked())
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
			if (position / tileXLength <= 0
					|| position / tileXLength >= tileYLength - 1
					|| position % tileXLength <= 0
					|| position % tileXLength >= tileXLength - 1)
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
				if (tileList.get(i).isBlocked()) {
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
