package com.danthe.dogeescape.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Player {
	/**
	 * ?
	 */
	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path = new LinkedList<Integer>();
	
	private int position;
	
	private final int tileXLength;
	private final int tileYLength;
	private final List<Tile> tileList;
	private boolean won = false;

	public Player(int position, int tileXLength, int tileYLength, List<Tile> tileList) {
		this.position = position;
		this.tileList = tileList;
		this.tileXLength = tileXLength;
		this.tileYLength = tileYLength;
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

			if (!tileList.get(w * i).isBlocked() && distance[w * i] < distance[pos])
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
			won = true;
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
			if (tileList.get(i).isBlocked())
				iter.remove();
		}

		return neighbors;

	}
	public boolean hasWon() {
		return won;
	}

	public void updateWay() {
		if (recalculate) calculateWay();
		recalculate = false;
		
	}
}
