package com.danthe.dogeescape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader {

	public static ArrayList<TileType> readLevel(BufferedReader bfr, int height,
			int width) {
		ArrayList<TileType> list = new ArrayList<TileType>(width * height);

		try {
			for (int i = 0; i < height; i++) {
				String[] row;
				row = bfr.readLine().replace(" ", "").split(",");
				for (String s : row) {
					switch (Integer.parseInt(s)) {
					case 0:
						list.add(TileType.EMPTY);
						break;
					case 1:
						list.add(TileType.STAKE);
						break;
					case 2:
						list.add(TileType.ROCK);
						break;
					case 3:
						list.add(TileType.ICE);
						break;
					case 4:
						list.add(TileType.LAVA);
						break;
					case 5:
						list.add(TileType.SWAMP);
						break;
					case 6:
						list.add(TileType.TURTLE);
						break;
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}
