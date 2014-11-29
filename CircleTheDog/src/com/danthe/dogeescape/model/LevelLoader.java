package com.danthe.dogeescape.model;

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
					list.add(TileType.values()[Integer.parseInt(s)]);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}
