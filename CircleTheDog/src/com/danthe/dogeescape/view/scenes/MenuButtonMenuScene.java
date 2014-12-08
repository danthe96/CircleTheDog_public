package com.danthe.dogeescape.view.scenes;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.util.Log;

import com.danthe.dogeescape.SceneManager;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.Tracker;
import com.danthe.dogeescape.Tracker.LevelSuccess;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.Level;
import com.danthe.dogeescape.model.level.LevelManager;

/**
 * This "scene" only contains the two menu buttons at the very bottom of the
 * game scene.
 * 
 * It could probably use a lot of redundant code from the EndScene, but I have
 * no clue what you're doing there.
 * 
 * @author Daniel
 * 
 */
public class MenuButtonMenuScene extends MenuScene implements
		IOnMenuItemClickListener {
	private static final String TAG = "GAME_SCENE_MENU_BUTTONS";

	public static final int BACK_ID = 1;
	public static final int RETRY_ID = 2;
	public static final int NEXT_ID = 3;

	public static final float BUTTON_SIZE = 1f;
	public static final float EXP_BUTTON_SIZE = 1.1f * BUTTON_SIZE;

	public static final float BUTTON_SIZE_QUOTIENT = 0.18f;
	public final float DISTANCE_BETWEEN_BUTTONS;

	private final SceneSetter sceneSetter;
	private final Level level;

	private final float sceneWidth;

	// tracker is only used when retry button is pressed during gameplay
	private boolean inGame;

	public MenuButtonMenuScene(Camera cam, VertexBufferObjectManager vbom,
			int parent_width, Level level, boolean inGame) {
		super(cam);
		Log.d(TAG, "Init");

		this.inGame = inGame;
		this.level = level;
		sceneSetter = SceneManager.getSceneSetter();

		setBackgroundEnabled(false);
		float button_size = BUTTON_SIZE_QUOTIENT * parent_width;
		DISTANCE_BETWEEN_BUTTONS = button_size / 4;

		IMenuItem back = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK_ID,
				button_size, button_size, TextureManager.backToMenuTextureReg,
				vbom), EXP_BUTTON_SIZE, BUTTON_SIZE);
		back.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		addMenuItem(back);

		IMenuItem retry = new ScaleMenuItemDecorator(new SpriteMenuItem(
				RETRY_ID, button_size, button_size,
				TextureManager.retryTextureReg, vbom), EXP_BUTTON_SIZE,
				BUTTON_SIZE);
		retry.setPosition(back.getX() + button_size + DISTANCE_BETWEEN_BUTTONS,
				0);
		retry.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		addMenuItem(retry);

		if (LevelManager.getInstance().isOpenToPlay(level.levelID + 1)
				&& (level.levelID + 1) % LevelManager.numLevelsPerStory != 0) {
			IMenuItem next = new ScaleMenuItemDecorator(new SpriteMenuItem(
					NEXT_ID, button_size, button_size,
					TextureManager.nextTextureReg, vbom), EXP_BUTTON_SIZE,
					BUTTON_SIZE);
			next.setPosition(retry.getX() + button_size
					+ DISTANCE_BETWEEN_BUTTONS, 0);
			addMenuItem(next);
			next.setBlendFunction(GL10.GL_SRC_ALPHA,
					GL10.GL_ONE_MINUS_SRC_ALPHA);
		}

		sceneWidth = getMenuItemCount() * button_size
				+ (getMenuItemCount() - 1) * DISTANCE_BETWEEN_BUTTONS;

		this.setOnMenuItemClickListener(this);
	}

	public float getWidth() {
		return sceneWidth;
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Debug.d(TAG, "MenuItemClicked");
		switch (pMenuItem.getID()) {
		case BACK_ID:
			sceneSetter.setScene(SceneType.LEVELSELECTSCENE);
			return true;
		case RETRY_ID:
			sceneSetter.setLevelScene(level.levelID);
			if (inGame)
				Tracker.getInstance().triggerLevel(level.levelID,
						LevelSuccess.RETRY, level.turns);

			return true;
		case NEXT_ID:
			sceneSetter.setLevelScene(level.levelID + 1);
			return true;
		}
		return false;
	}

}
