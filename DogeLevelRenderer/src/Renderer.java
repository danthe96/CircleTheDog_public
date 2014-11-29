import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Renderer extends JFrame implements ActionListener,
		DocumentListener {

	enum Type {
		EMPTY, STAKE, ROCK, BUSH, ICE, LAVA, SWAMP, TURTLE;

		float[] spawnChance = { 0, 0.02f, 0.05f, 0.05f, 0f, 0f, 0f, 0f, };

		float getSpawnChance() {
			return spawnChance[ordinal()];
		}
	}

	JTextArea textArea = new JTextArea("Wow such Text Area.");
	JButton render = new JButton("Very Render. Wow");
	JButton random = new JButton("Such Random. Wow");

	private static int plot_size = 64;
	private static String lineSep;
	BufferedImage tiles;
	BufferedImage enemy;

	public Renderer() {
		setSize(1500, 1000);
		setVisible(true);

		setLayout(new BorderLayout());
		lineSep = System.getProperty("line.separator");



//
		render.addActionListener(this);
		render.setActionCommand("render");
		getContentPane().add(render, BorderLayout.SOUTH);

		random.addActionListener(this);
		random.setActionCommand("random");
		getContentPane().add(random, BorderLayout.NORTH);
		System.out.println("random button added");
		
		textArea.setPreferredSize(new Dimension(200, 400));
		textArea.setText(getRandomLevel());
		textArea.getDocument().addDocumentListener(this);
//
		getContentPane().add(textArea, BorderLayout.WEST);
		try {
			tiles = ImageIO.read(new File("tiles.png"));
			enemy = ImageIO.read(new File("enemy2.png"));
			System.out.println("images loaded");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Renderer();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("render")) {

		} else
			textArea.setText(getRandomLevel());

		render();

	}

	private void render() {
		try {
			System.out.println("RENDER!");

			getGraphics().fillRect(220, 100, 800, 800);

			int x_start = 250;
			int y_start = 200;

			String level = textArea.getText().replaceAll(" ", "");
			String topline = level.substring(0, level.indexOf(lineSep));
			String[] sizes = topline.split(",");

			//get Level Dimensions
			int width = Integer.parseInt(sizes[0]);
			int height = Integer.parseInt(sizes[1]);

			//draw the plots
			String[] lines = level.split(lineSep+"|;");
//			for (String s: lines) {
//
//				System.out.println(s);
//				System.out.println("---");
//			}
			String[] row;
			int x_offset = 0;
			int lineID = 1;
			for (lineID = lineID; lineID <= height; lineID++) {
				row = lines[lineID].split(",");
				if (lineID % 2 == 0)
					x_offset = plot_size / 2;
				else
					x_offset = 0;

				for (int x = 0; x < width; x++) {
					drawPlot(x_offset + x_start + x * plot_size, y_start
							+ (lineID - 1) * plot_size, getType(row[x]));
				}
				
				

			}
//			draw the enemies
			String[] coords;
			int x,y;
			for (lineID = lineID; lineID< lines.length; lineID++) {
				System.out.println(lines[lineID]);
				coords = lines[lineID].split(",");
				x = Integer.parseInt(coords[1]);
				y = Integer.parseInt(coords[0]);
				
				if (y%2 == 0) {
					x_offset = 0;
				}
				else {
					x_offset = plot_size/2;
				}
				
				drawEnemy(x_start + x* plot_size+x_offset, y_start+y*plot_size);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void drawEnemy(int i, int j) {
		int playerHeigth = (int) (plot_size * 1.5);
		getGraphics().drawImage(enemy, i, j-playerHeigth/2, i+plot_size, j+playerHeigth/2, 0,0, enemy.getWidth()/5, enemy.getHeight(), null);
		
	}

	private void drawPlot(int i, int j, Type type) {
		Graphics g = getGraphics();
		g.drawImage(tiles, i, j, i + plot_size, j + plot_size,
				128 * type.ordinal(), 0, 128 * (type.ordinal() + 1),
				tiles.getHeight(), null);
		// g.drawImage(tiles, i, j, i+plot_size, j+plot_size, 0, 0,
		// tiles.getWidth(), tiles.getHeight(), null);

		// g.fillRect(i, j, plot_size, plot_size);
	}

	private Type getType(String string) {
		return Type.values()[Integer.parseInt(string)];
	}

	public String getRandomLevel() {

		// Adjust this parameter to make levels more/less difficult by spawning
		// more or less blocks
		float diff = 1f;

		int num_x = 10;
		int num_y = 9;

		String result = num_x + "," + num_y + lineSep;
		Type type;
		for (int y = 0; y < num_y; y++)
			for (int x = 0; x < num_x; x++) {
				type = getRandomType(diff);
				result += type.ordinal();
				if (x == num_x - 1)
					result += lineSep;
				else
					result += ",";
			}
		result += "5,4";
		return result;
	}

	private Type getRandomType(float diff) {
		float rand = (float) Math.random();

		for (Type t : Type.values()) {
			if (rand < t.getSpawnChance() * diff)
				return t;
			else
				rand -= t.getSpawnChance() * diff;
		}

		return Type.EMPTY;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
			
		render();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		render();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		render();

	}
}
