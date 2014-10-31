package com.danthe.dogeescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

public class DogeActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener, Runnable, IOnMenuItemClickListener {

	private Camera camera;

	private Scene mainScene;
	Scene gameScene;
	private MenuScene menuScene;
	private MenuScene highscoreMenuScene, pauseMenuScene, endMenuScene;
	private MenuScene[] howToMenus = new MenuScene[4];
	private int howToIndex = 0;
	private Scene endScene;

	private Thread t;
	private LinkedList<Integer> highscores;

	private boolean recalculate = true;
	private int[] distance;
	private int[] previous;
	private LinkedList<Integer> path;

	private boolean playersTurn = false;
	private int turns = 0;
	private boolean lost = false;
	private boolean won = false;
	private boolean trapped = false;
	private Tile[][] tiles = new Tile[9][9];

	private ITextureRegion backgroundTextureReg, gameBackgroundTextureReg,
			textBoxTextureReg, whiteTextBoxTextureReg, endBackgroundTextureReg
			/* ,shareTextureReg, dogecoinTextureReg */;
	private ITextureRegion[] howToTextureRegs = new ITextureRegion[4];
	private ITiledTextureRegion enemyTextureReg, circleTextureReg;

	private Font comicSansFont, defaultFont;

	private int[] enemyPosition = new int[2];
	private AnimatedSprite enemySprite;

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		highscores = new LinkedList<Integer>();
		SharedPreferences prefs = this.getSharedPreferences("dogeScores",
				Context.MODE_PRIVATE);

		for (int i = 0; i < 5; i++)
			highscores.add(prefs.getInt("key" + i, -1));

		// TODO Remove Debug
		// RateAppManager.clear(this);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(/* CAMERA_WIDTH, CAMERA_HEIGHT */),
				camera);
	}

	@Override
	protected void onCreateResources() {

		this.comicSansFont = FontFactory.create(mEngine.getFontManager(),
				mEngine.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				Typeface.createFromAsset(this.getAssets(),
						"ttf/LDFComicSans.ttf"), 46f, true,
				Color.WHITE_ARGB_PACKED_INT);
		this.getFontManager().loadFont(this.comicSansFont);
		comicSansFont.getTexture().load();

		this.defaultFont = FontFactory.create(mEngine.getFontManager(),
				mEngine.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				Typeface.DEFAULT_BOLD, 42f, true, Color.WHITE_ARGB_PACKED_INT);
		this.getFontManager().loadFont(this.defaultFont);
		defaultFont.getTexture().load();

		try {

			// 1 - Set up bitmap textures
			ITexture backgroundTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/background.png");
						}
					});
			ITexture gameBackgroundTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/game_background.png");
						}
					});
			ITexture endBackgroundTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/end_background.png");
						}
					});

			ITexture textBoxTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/textbox.png");
						}
					});
			ITexture whiteTextBoxTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/textbox_white.png");
						}
					});
			ITexture shareTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/share_facebook.png");
						}
					});
			// ITexture dogecoinTexture = new BitmapTexture(
			// this.getTextureManager(), new IInputStreamOpener() {
			// @Override
			// public InputStream open() throws IOException {
			// return getAssets().open("gfx/dogecoin.png");
			// }
			// });

			BitmapTextureAtlas enemyBTA = new BitmapTextureAtlas(
					this.getTextureManager(), 1280, 512,
					TextureOptions.BILINEAR);
			BitmapTextureAtlas circleBTA = new BitmapTextureAtlas(
					this.getTextureManager(), 128, 64, TextureOptions.BILINEAR);

			// 2 - Load bitmap textures into VRAM
			backgroundTexture.load();
			gameBackgroundTexture.load();
			endBackgroundTexture.load();

			circleBTA.load();
			enemyBTA.load();

			whiteTextBoxTexture.load();
			textBoxTexture.load();
			shareTexture.load();
			// dogecoinTexture.load();

			// 3 - Set up texture regions
			this.backgroundTextureReg = TextureRegionFactory
					.extractFromTexture(backgroundTexture);
			this.gameBackgroundTextureReg = TextureRegionFactory
					.extractFromTexture(gameBackgroundTexture);
			this.endBackgroundTextureReg = TextureRegionFactory
					.extractFromTexture(endBackgroundTexture);
			this.circleTextureReg = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(circleBTA, this.getAssets(),
							"gfx/circles.png", 0, 0, 2, 1);
			this.enemyTextureReg = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(enemyBTA, this.getAssets(),
							"gfx/enemy2.png", 0, 0, 5, 1);
			this.textBoxTextureReg = TextureRegionFactory
					.extractFromTexture(textBoxTexture);
			this.whiteTextBoxTextureReg = TextureRegionFactory
					.extractFromTexture(whiteTextBoxTexture);
			// this.shareTextureReg = TextureRegionFactory
			// .extractFromTexture(shareTexture);
			// this.dogecoinTextureReg = TextureRegionFactory
			// .extractFromTexture(dogecoinTexture);

			for (int i = 0; i < howToTextureRegs.length; i++) {
				final int i2 = i;
				ITexture howToTexture = new BitmapTexture(
						this.getTextureManager(), new IInputStreamOpener() {
							@Override
							public InputStream open() throws IOException {
								return getAssets().open(
										"gfx/tut" + (i2 + 1) + ".png");
							}
						});
				howToTexture.load();
				howToTextureRegs[i] = TextureRegionFactory
						.extractFromTexture(howToTexture);
			}

		} catch (IOException e) {
			Debug.e(e);
		}

	}

	@Override
	protected Scene onCreateScene() {
		mainScene = new Scene();
		createMenuScene();

		Sprite backgroundSprite = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				this.backgroundTextureReg, getVertexBufferObjectManager());
		mainScene.attachChild(backgroundSprite);

		// Sprite dogecoinSprite = new Sprite(590, 1150, 128, 128,
		// this.dogecoinTextureReg, this.getVertexBufferObjectManager()) {
		// @Override
		// public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
		// float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		// dogeActivity.startActivity(new Intent(Intent.ACTION_VIEW,
		// Uri.parse(dogeActivity
		// .getString(R.string.dogecoin_url))));
		// return true;
		// }
		// return false;
		// }
		// };
		// mainScene.registerTouchArea(dogecoinSprite);
		// mainScene.attachChild(dogecoinSprite);

		mainScene.setChildScene(menuScene);

		return mainScene;

	}

	private void createEndScene() {
		boolean ratescreen = false;
		endScene = new Scene();
		endScene.setPosition(60, 240);

		Sprite backgroundSprite = new Sprite(0, 0, 600, 804,
				this.endBackgroundTextureReg,
				this.getVertexBufferObjectManager());
		endScene.attachChild(backgroundSprite);

		endMenuScene = new MenuScene(camera);

		if (won) {
			Debug.e("GAME WON!");
			if (RateAppManager.bRateNow(this)) {
				Debug.e("RATE SCREEN");
				ratescreen = true;
				Text rate_top = new Text(72, 64, comicSansFont,
						this.getText(R.string.rate_top),
						this.getVertexBufferObjectManager());
				rate_top.setColor(Color.BLACK);
				endScene.attachChild(rate_top);

				Text rate_info = new Text(64, rate_top.getY() + 90,
						defaultFont, this.getText(R.string.rate_info),
						this.getVertexBufferObjectManager());
				rate_info.setColor(Color.BLACK);
				endScene.attachChild(rate_info);

				TextMenuItem yesText = new TextMenuItem(0, comicSansFont,
						this.getText(R.string.rate_yes),
						this.getVertexBufferObjectManager());
				final IMenuItem yesItem = new AnimatedSpriteMenuItem(666, 480,
						96, textBoxTextureReg,
						this.getVertexBufferObjectManager(), true, false,
						yesText);
				endMenuScene.addMenuItem(yesItem);

				TextMenuItem remindText = new TextMenuItem(0, comicSansFont,
						this.getText(R.string.rate_later),
						this.getVertexBufferObjectManager());
				final IMenuItem remindItem = new AnimatedSpriteMenuItem(667,
						480, 96, textBoxTextureReg,
						this.getVertexBufferObjectManager(), true, false,
						remindText);
				endMenuScene.addMenuItem(remindItem);

				TextMenuItem neverText = new TextMenuItem(5, comicSansFont,
						this.getText(R.string.no),
						this.getVertexBufferObjectManager());
				final IMenuItem neverItem = new AnimatedSpriteMenuItem(668,
						480, 96, textBoxTextureReg,
						this.getVertexBufferObjectManager(), true, false,
						neverText);
				endMenuScene.addMenuItem(neverItem);

			} else {
				Text doge_victory = new Text(72, 96, comicSansFont,
						this.getText(R.string.victory),
						this.getVertexBufferObjectManager());
				doge_victory.setColor(Color.BLACK);
				doge_victory.setX(backgroundSprite.getWidth() / 2
						- doge_victory.getWidth() / 2);
				endScene.attachChild(doge_victory);

				// Text doge = new Text(460, doge_victory.getY()
				// + doge_victory.getHeight() + 10, defaultFont,
				// this.getText(R.string.doge),
				// this.getVertexBufferObjectManager());
				// doge.setColor(Color.BLACK);
				// endScene.attachChild(doge);

				Text victory_info = new Text(64, 212, defaultFont,
						this.getString(R.string.victory_info, turns),
						this.getVertexBufferObjectManager());
				victory_info.setColor(Color.BLACK);
				victory_info.setX(backgroundSprite.getWidth() / 2
						- victory_info.getWidth() / 2);
				endScene.attachChild(victory_info);
			}

		} else if (lost) {

			Text doge_defeat = new Text(72, 96, comicSansFont,
					this.getText(R.string.defeat),
					this.getVertexBufferObjectManager());
			doge_defeat.setColor(Color.BLACK);
			doge_defeat.setX(backgroundSprite.getWidth() / 2
					- doge_defeat.getWidth() / 2);
			endScene.attachChild(doge_defeat);

			// Text doge = new Text(460, doge_defeat.getY()
			// + doge_defeat.getHeight() + 10, defaultFont,
			// this.getText(R.string.doge),
			// this.getVertexBufferObjectManager());
			// doge.setColor(Color.BLACK);
			// endScene.attachChild(doge);

			Text defeat_info = new Text(72, 212, defaultFont, this.getString(
					R.string.defeat_info, turns),
					this.getVertexBufferObjectManager());
			defeat_info.setColor(Color.BLACK);
			defeat_info.setX(backgroundSprite.getWidth() / 2
					- defeat_info.getWidth() / 2);
			endScene.attachChild(defeat_info);

		}

		// SpriteMenuItem shareItem = new SpriteMenuItem(7, 480, 96,
		// this.shareTextureReg, this.getVertexBufferObjectManager());
		// endMenuScene.addMenuItem(shareItem);
		if (!ratescreen) {
			TextMenuItem shareText = new TextMenuItem(0, comicSansFont,
					this.getText(R.string.share_app),
					this.getVertexBufferObjectManager());
			final IMenuItem shareItem = new AnimatedSpriteMenuItem(7, 480, 96,
					textBoxTextureReg, this.getVertexBufferObjectManager(),
					true, false, shareText);
			endMenuScene.addMenuItem(shareItem);

			TextMenuItem playText = new TextMenuItem(0, comicSansFont,
					this.getText(R.string.play_again),
					this.getVertexBufferObjectManager());
			final IMenuItem playItem = new AnimatedSpriteMenuItem(0, 480, 96,
					textBoxTextureReg, this.getVertexBufferObjectManager(),
					true, false, playText);
			endMenuScene.addMenuItem(playItem);

			TextMenuItem backText = new TextMenuItem(5, comicSansFont,
					this.getText(R.string.back),
					this.getVertexBufferObjectManager());
			final IMenuItem backItem = new AnimatedSpriteMenuItem(5, 480, 96,
					textBoxTextureReg, this.getVertexBufferObjectManager(),
					true, false, backText);
			endMenuScene.addMenuItem(backItem);
		}
		endMenuScene.setPosition(0, 208);

		endMenuScene.setMenuAnimator(new DirectMenuAnimator());
		endMenuScene.buildAnimations();
		endMenuScene.setBackgroundEnabled(false);
		endMenuScene.setOnMenuItemClickListener(this);

		endScene.setBackgroundEnabled(false);
		endScene.setChildScene(endMenuScene);

	}

	private void createMenuScene() {

		// 1 - main menu
		menuScene = new MenuScene(camera);

		TextMenuItem playText = new TextMenuItem(0, comicSansFont,
				this.getText(R.string.play),
				this.getVertexBufferObjectManager());
		final IMenuItem playItem = new AnimatedSpriteMenuItem(0, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, playText);
		menuScene.addMenuItem(playItem);

		TextMenuItem howToText = new TextMenuItem(1, comicSansFont,
				this.getText(R.string.howto),
				this.getVertexBufferObjectManager());
		final IMenuItem howToItem = new AnimatedSpriteMenuItem(1, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, howToText);
		menuScene.addMenuItem(howToItem);

		TextMenuItem highscoreText = new TextMenuItem(2, comicSansFont,
				this.getText(R.string.scores),
				this.getVertexBufferObjectManager());
		final IMenuItem highscoreItem = new AnimatedSpriteMenuItem(2, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, highscoreText);
		menuScene.addMenuItem(highscoreItem);

		menuScene.setMenuAnimator(new DirectMenuAnimator());
		menuScene.buildAnimations();
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnMenuItemClickListener(this);

		// submenu about how to play
		String[] strings = { this.getString(R.string.tutorial_1),
				this.getString(R.string.tutorial_2),
				this.getString(R.string.tutorial_3),
				this.getString(R.string.tutorial_4) };
		for (int i = 0; i < howToMenus.length; i++) {
			howToMenus[i] = createHowToMenuScene(howToTextureRegs[i],
					strings[i], i == howToMenus.length - 1 ? true : false);
		}

		// highscore submenu
		highscoreMenuScene = new MenuScene(camera);

		StringBuffer scores = new StringBuffer(this.getText(R.string.highscore));
		scores.append("\n\n");
		for (int i = 0; i < 5; i++) {
			if (highscores.get(i) != -1)
				scores.append((i + 1) + ".   " + highscores.get(i) + "\n");
		}

		TextMenuItem currentHighscoreText = new TextMenuItem(4, comicSansFont,
				scores.toString(), this.getVertexBufferObjectManager());
		currentHighscoreText.setColor(Color.BLACK);
		final IMenuItem currentHighscoreItem = new AnimatedSpriteMenuItem(4,
				550, 500, whiteTextBoxTextureReg,
				this.getVertexBufferObjectManager(), false, true,
				currentHighscoreText);

		highscoreMenuScene.addMenuItem(currentHighscoreItem);

		TextMenuItem backText = new TextMenuItem(5, comicSansFont,
				this.getText(R.string.back),
				this.getVertexBufferObjectManager());
		final IMenuItem backItem = new AnimatedSpriteMenuItem(5, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, backText);
		highscoreMenuScene.addMenuItem(backItem);

		highscoreMenuScene.setMenuAnimator(new DirectMenuAnimator());
		highscoreMenuScene.buildAnimations();
		highscoreMenuScene.setBackgroundEnabled(false);
		highscoreMenuScene.setOnMenuItemClickListener(this);

	}

	private MenuScene createHowToMenuScene(ITextureRegion picture, String text,
			boolean last) {
		MenuScene howTo = new MenuScene(camera);

		final IMenuItem howToPicItem = new SpriteMenuItem(3, 550, 500, picture,
				this.getVertexBufferObjectManager());
		howTo.addMenuItem(howToPicItem);

		TextMenuItem manualText = new TextMenuItem(3, defaultFont, text,
				this.getVertexBufferObjectManager());
		manualText.setColor(Color.BLACK);
		final IMenuItem manualItem = new AnimatedSpriteMenuItem(0, 550, 220,
				whiteTextBoxTextureReg, this.getVertexBufferObjectManager(),
				false, false, manualText);
		howTo.addMenuItem(manualItem);

		if (last) {
			TextMenuItem playText = new TextMenuItem(0, comicSansFont,
					this.getText(R.string.play),
					this.getVertexBufferObjectManager());
			final IMenuItem playItem = new AnimatedSpriteMenuItem(0, 550, 220,
					textBoxTextureReg, this.getVertexBufferObjectManager(),
					true, false, playText);
			howTo.addMenuItem(playItem);
		} else {
			TextMenuItem nextText = new TextMenuItem(0, comicSansFont,
					this.getText(R.string.next),
					this.getVertexBufferObjectManager());
			final IMenuItem nextItem = new AnimatedSpriteMenuItem(8, 550, 220,
					textBoxTextureReg, this.getVertexBufferObjectManager(),
					true, false, nextText);
			howTo.addMenuItem(nextItem);
		}

		howTo.setPosition(0, 120);

		howTo.setMenuAnimator(new DirectMenuAnimator());
		howTo.buildAnimations();
		howTo.setBackgroundEnabled(false);
		howTo.setOnMenuItemClickListener(this);

		return howTo;
	}

	private void createGameScene() {

		gameScene = new Scene();

		Sprite background2Sprite = new Sprite(22, 332, 676, 648,
				this.gameBackgroundTextureReg, getVertexBufferObjectManager());
		gameScene.attachChild(background2Sprite);

		double sixth = 1 / 6f;
		int alternate = -16;
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				boolean blocked = Math.random() < sixth ? true : false;
				if (!blocked || (i == 4 && j == 4)) {
					tiles[i][j] = new Tile((56 + alternate) + 68 * j,
							350 + 68 * i, circleTextureReg,
							getVertexBufferObjectManager(), false, this);
					gameScene.registerTouchArea(tiles[i][j]);
				} else {
					tiles[i][j] = new Tile((56 + alternate) + 68 * j,
							350 + 68 * i, circleTextureReg,
							getVertexBufferObjectManager(), true, this);
				}
				gameScene.attachChild(tiles[i][j]);

			}
			alternate = -alternate;
		}

		enemySprite = new AnimatedSprite(tiles[4][4].getX(),
				tiles[4][4].getY() - 64 - 10, 64, 128, enemyTextureReg,
				getVertexBufferObjectManager());
		enemyPosition = new int[] { 4, 4 };
		gameScene.attachChild(enemySprite);
		enemySprite.setZIndex(background2Sprite.getZIndex() + 1);
		enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);

		gameScene.setBackgroundEnabled(false);

		// initialize pauseMenu

		pauseMenuScene = new MenuScene(camera);

		TextMenuItem continueText = new TextMenuItem(6, comicSansFont,
				this.getText(R.string.resume),
				this.getVertexBufferObjectManager());
		final IMenuItem continueItem = new AnimatedSpriteMenuItem(6, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, continueText);
		pauseMenuScene.addMenuItem(continueItem);

		TextMenuItem backText = new TextMenuItem(5, comicSansFont,
				this.getText(R.string.back),
				this.getVertexBufferObjectManager());
		final IMenuItem backItem = new AnimatedSpriteMenuItem(5, 550, 220,
				textBoxTextureReg, this.getVertexBufferObjectManager(), true,
				false, backText);
		pauseMenuScene.addMenuItem(backItem);

		pauseMenuScene.setMenuAnimator(new DirectMenuAnimator());
		pauseMenuScene.buildAnimations();
		pauseMenuScene.setBackgroundEnabled(false);
		pauseMenuScene.setOnMenuItemClickListener(this);

		if (t != null && !won && !lost)
			while (t.isAlive()) {
				t.interrupt();
			}

		t = new Thread(this);
		t.start();

	}

	public boolean blockTile(Tile tile) {
		if (!playersTurn || tile.isBlocked()
				|| tiles[enemyPosition[0]][enemyPosition[1]] == tile)
			return false;

		int pos = -1;
		for (int i = 0; i < 81; i++) {
			if (tiles[i / 9][i % 9] == tile) {
				pos = i;
				break;
			}
		}

		if (path.contains(Integer.valueOf(pos)))
			recalculate = true;

		playersTurn = false;
		turns++;
		return true;
	}

	private LinkedList<Integer[]> getNeighbors(int tileRow, int tileCol) {
		LinkedList<Integer[]> neighbors = new LinkedList<Integer[]>();
		int add = tileRow % 2 == 1 ? 1 : 0;

		neighbors.add(new Integer[] { tileRow - 1, tileCol - 1 + add });
		neighbors.add(new Integer[] { tileRow - 1, tileCol + add });
		neighbors.add(new Integer[] { tileRow, tileCol + 1 });
		neighbors.add(new Integer[] { tileRow + 1, tileCol - 1 + add });
		neighbors.add(new Integer[] { tileRow + 1, tileCol + add });
		neighbors.add(new Integer[] { tileRow, tileCol - 1 });

		for (Iterator<Integer[]> iter = neighbors.iterator(); iter.hasNext();) {
			Integer[] i = iter.next();
			if (i[0] < 0 || i[0] >= 9 || i[1] < 0 || i[1] >= 9
					|| tiles[i[0]][i[1]].isBlocked())
				iter.remove();
		}

		return neighbors;

	}

	private void doDijkstra(int startRow, int startCol) {
		distance = new int[81];
		previous = new int[81];
		distance[startRow * 9 + startCol] = 0;
		LinkedList<Integer> q = new LinkedList<Integer>();
		for (int i = 0; i < 81; i++) {
			if (!(i / 9 == startRow && i % 9 == startCol)) {
				distance[i] = Integer.MAX_VALUE;
				previous[i] = -1;
			}
			if (!tiles[i / 9][i % 9].isBlocked())
				q.add(i);
		}

		while (!q.isEmpty()) {
			// Debug.e("size " + q.size());
			int u = q.get(0);
			for (int i = 0; i < distance.length; i++) {
				if (distance[i] < distance[u] && q.contains((Integer) i))
					u = i;
			}

			/* boolean check = */q.remove((Integer) u);
			// if (!check)
			// Debug.e("Field " + u + ". Blocked?"
			// + tiles[u / 9][u % 9].isBlocked());

			if (distance[u] < Integer.MAX_VALUE) {

				LinkedList<Integer[]> neighbors = getNeighbors(u / 9, u % 9);
				for (Integer[] i : neighbors) {
					int alt = distance[u] + 1;
					if (alt < distance[i[0] * 9 + i[1]]) {
						distance[i[0] * 9 + i[1]] = alt;
						previous[i[0] * 9 + i[1]] = u;
					}
				}
			}

		}

	}

	private void calculateWay() {
		doDijkstra(enemyPosition[0], enemyPosition[1]);

		int pos = 9;
		for (int i = 0; i < 81; i++)
			if ((i / 9 <= 0 || i / 9 >= 8 || i % 9 <= 0 || i % 9 >= 8)
					&& !tiles[i / 9][i % 9].isBlocked()) {
				pos = i;
				break;
			}

		int unreachable = 0;
		for (int i = 1; i < 9; i++) {

			if (!tiles[i][0].isBlocked() && distance[9 * i] < distance[pos])
				pos = 9 * i;
			else if (distance[9 * i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tiles[8][i].isBlocked() && distance[8 * 9 + i] < distance[pos])
				pos = 8 * 9 + i;
			else if (distance[8 * 9 + i] == Integer.MAX_VALUE)
				unreachable++;

			if (!tiles[8 - i][8].isBlocked()
					&& distance[(8 - i) * 9 + 8] < distance[pos])
				pos = (8 - i) * 9 + 8;
			else if (distance[(8 - i) * 9 + 8] == Integer.MAX_VALUE)
				unreachable++;

			if (!tiles[0][8 - i].isBlocked() && distance[8 - i] < distance[pos])
				pos = 8 - i;
			else if (distance[8 - i] == Integer.MAX_VALUE)
				unreachable++;

		}

		if (unreachable >= 32) {
			trapped = true;
			return;
		}

		// Debug.e(Arrays.toString(previous) + " \n " + pos);
		// Debug.e(Arrays.toString(distance));

		path.clear();
		int v = pos;
		while (v != enemyPosition[0] * 9 + enemyPosition[1]) {
			path.add(0, v);
			// Debug.e("path size " + path.size());
			// Debug.e(Arrays.toString(previous) + " \n " + pos);
			v = previous[v];
		}

		recalculate = false;
	}

	@Override
	public void run() {

		lost = false;
		won = false;
		trapped = false;
		turns = 0;
		playersTurn = true;
		recalculate = true;

		path = new LinkedList<Integer>();

		while (!t.isInterrupted() && !lost && !won) {

			if (!playersTurn) {
				Debug.e("enemy turn");

				int row = enemyPosition[0];
				int col = enemyPosition[1];

				LinkedList<Integer[]> neighbors = getNeighbors(row, col);

				if (neighbors.size() < 1) {
					won = true;
					Debug.e("won");
					break;
				}

				Integer[] nextTile = new Integer[2];
				if (recalculate) {
					calculateWay();
				}
				if (trapped) {
					int rand = (int) (Math.random() * neighbors.size());
					nextTile = neighbors.get(rand);
				} else {
					int next = path.poll();
					nextTile[0] = next / 9;
					nextTile[1] = next % 9;
				}

				enemySprite.setX(tiles[nextTile[0]][nextTile[1]].getX());
				enemySprite
						.setY(tiles[nextTile[0]][nextTile[1]].getY() - 64 - 10);
				enemyPosition[0] = nextTile[0];
				enemyPosition[1] = nextTile[1];

				if (enemyPosition[0] <= 0 || enemyPosition[0] >= 8
						|| enemyPosition[1] <= 0 || enemyPosition[1] >= 8) {
					enemySprite.animate(new long[] { 100, 250 }, new int[] { 0,
							4 }, 3);
					try {
						Thread.sleep(1050);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					enemySprite.animate(new long[] { 200, 250 }, 0, 1, true);
					lost = true;
					break;
				} else if (trapped) {
					enemySprite.animate(new long[] { 250, 100 }, new int[] { 0,
							3 }, true);
				}

				playersTurn = true;

			} else {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		if (lost || won) {

			if (won) {
				for (int i = 0; i < 5; i++) {
					if (highscores.get(i) == -1 || turns < highscores.get(i)) {
						highscores.add(i, turns);
						highscores.remove(5);
						break;
					}
				}
				SharedPreferences prefs = this.getSharedPreferences(
						"dogeScores", Context.MODE_PRIVATE);
				Editor edit = prefs.edit();
				for (int i = 0; i < 5; i++) {
					edit.putInt("key" + i, highscores.get(i));
					edit.commit();
				}
			}

			createEndScene();
			gameScene.setChildScene(endScene);
		}

	}

	private void share(String url) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = (String) this.getText(R.string.app_url);
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				(String) this.getText(R.string.share_text));
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent,
				(String) this.getText(R.string.share_via)));
		// Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("text/plain");
		// intent.putExtra(Intent.EXTRA_TEXT, url);
		//
		// boolean found = false;
		// List<ResolveInfo> matches =
		// getPackageManager().queryIntentActivities(
		// intent, 0);
		// for (ResolveInfo info : matches) {
		// if (info.activityInfo.packageName.toLowerCase().startsWith(
		// "com.facebook.katana")) {
		// intent.setPackage(info.activityInfo.packageName);
		// found = true;
		// break;
		// }
		// }
		//
		// // As fallback, launch sharer.php in a browser
		// if (!found) {
		// String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
		// + url;
		// intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
		// }
		//
		// startActivity(intent);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Debug.e("" + pMenuItem.getID());
		switch (pMenuItem.getID()) {
		case 0:
			createGameScene();
			mainScene.clearChildScene();
			mainScene.setChildScene(gameScene);
			return true;
		case 1:
			howToIndex = 0;
			menuScene.setChildSceneModal(howToMenus[howToIndex]);
			return true;
		case 2:
			menuScene.setChildSceneModal(highscoreMenuScene);
			return true;
		case 5:
			if (t != null && t.isAlive())
				t.interrupt();
			if (gameScene != null) {
				gameScene.reset();
			}
			menuScene.reset();
			createMenuScene();
			mainScene.reset();
			mainScene.clearChildScene();
			mainScene.setChildScene(menuScene);
			return true;
		case 6:
			gameScene.clearChildScene();
			return true;
		case 7:
			share(getString(R.string.app_url));
			return true;
		case 8:
			howToIndex++;
			menuScene.setChildSceneModal(howToMenus[howToIndex]);
			return true;
		case 666:
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=com.danthe.dogeescape")));
		case 668:
			RateAppManager.neverPromptAgain(this);
		case 667:
			endScene.reset();
			endScene.clearChildScene();
			createEndScene();
			gameScene.setChildScene(endScene);
			return true;

		}
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (gameScene != null && !gameScene.hasChildScene()
					&& mainScene.getChildScene() == gameScene) {
				gameScene.setChildScene(pauseMenuScene);
				return true;
			} else if (mainScene.getChildScene() != menuScene
					|| menuScene.hasChildScene()) {
				if (t != null && t.isAlive())
					t.interrupt();
				if (gameScene != null) {
					gameScene.reset();
				}
				menuScene.reset();
				mainScene.reset();
				mainScene.clearChildScene();
				mainScene.setChildScene(menuScene);
				return true;
			} else
				return super.onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
