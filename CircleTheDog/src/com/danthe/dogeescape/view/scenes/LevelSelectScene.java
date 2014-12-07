package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.Level.Status;
import com.danthe.dogeescape.model.level.LevelManager;
import com.danthe.dogeescape.model.level.LevelManager.Story;

public class LevelSelectScene extends Scene implements IOnMenuItemClickListener {
	private static final String TAG = "LEVEL_SELECT_SCENE";

	private MenuScene menuChildScene;
	private final Story story;
	private final Activity activity;

	private SceneSetter levelSceneSetter;
	private static final int MenuItemPixel = 256;
	private static final int ElementsPerRow = 4;

	private static final float MenuTopOffset = 0.3f;
	private static final float MenuSideOffset = 0.125f;

	private static final float DistanceBetweenElements = 0.03f;
	private static final int STORY_SELECT_ID = -1;
	private static final int TUTORIAL_ID = -2;

	private static float ButtonSize() {
		return (1f - 2 * MenuSideOffset - (ElementsPerRow - 1)
				* DistanceBetweenElements)
				* (float) GameActivity.CAMERA_WIDTH
				/ (float) MenuItemPixel
				/ (float) ElementsPerRow;

	}

	private LevelSelectScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter, Story story, Activity activity) {
		this.levelSceneSetter = levelSceneSetter;
		this.story = story;
		this.activity = activity;
		//createBackground(vertexBufferObjectManager);
		createMenuChildScene(vertexBufferObjectManager, camera);
		setBackgroundEnabled(false);
		
	}
	
	public boolean checkForTutorial(Story story){
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		if (prefs.getBoolean("firstTime"+story.toString(), true)) {
			Log.d(TAG, "First time, showing tutorial");
			Editor editor = prefs.edit();
			editor.putBoolean("firstTime"+story.toString(), false);
			editor.commit();

			levelSceneSetter.setScene(SceneType.TUTORIALSCENE);
			return true;
		}
		return false;
	}

	public static LevelSelectScene createScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter, Story story, Activity activity) {
		LevelSelectScene result = new LevelSelectScene(
				vertexBufferObjectManager, camera, levelSceneSetter, story,
				activity);
		return result;

	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case STORY_SELECT_ID:
			levelSceneSetter.setScene(SceneType.STORYSELECTSCENE);
			return true;
		case TUTORIAL_ID:
			levelSceneSetter.setScene(SceneType.TUTORIALSCENE);
			return true;
		default:
			int levelID = pMenuItem.getID();
			if (LevelManager.getInstance().isOpenToPlay(levelID)) {
				levelSceneSetter.setLevelScene(pMenuItem.getID());
				return true;
			} else {
				// TODO Play some sound here.
			}
		}
		return false;
	}

	private void createBackground(
			VertexBufferObjectManager vertexBufferObjectManager) {
		Sprite background = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
				GameActivity.CAMERA_HEIGHT, TextureManager.appBackground,
				vertexBufferObjectManager);
		setBackground(new SpriteBackground(0, 0, 0, background));

	}

	private void createMenuChildScene(VertexBufferObjectManager vbom,
			Camera camera) {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);// GameActivity.CAMERA_HEIGHT*MenuTopOffset);

		generateGrid(vbom);

		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	/**
	 * Generates the ButtonGrid.
	 * 
	 * @param vbom
	 */
	private void generateGrid(VertexBufferObjectManager vbom) {
		float ExpandedButtonSize = ButtonSize() + DistanceBetweenElements
				* GameActivity.CAMERA_WIDTH / MenuItemPixel;
		int x = -1;
		int y = 0;

		float startposX = (ElementsPerRow - 1) / 2f;
		float startposY = 0;

		IMenuItem menuItem;
		for (int levelID : story.getLevelIDs()) {
			x++;
			if (x == ElementsPerRow) {
				x = 0;
				y++;
			}
			menuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(levelID,
					getMenuTexture(levelID), vbom), ExpandedButtonSize,
					ButtonSize());
			moveToTopCenter(menuItem);

			move(menuItem, x - startposX, y - startposY);

			menuChildScene.addMenuItem(menuItem);
		}

		// Add back to Story Button
		IMenuItem backToMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(STORY_SELECT_ID,
						TextureManager.backToMenuTextureReg, vbom),
				ExpandedButtonSize, ButtonSize());
		moveToTopCenter(backToMenuItem);
		move(backToMenuItem, 0, y + 2);
		menuChildScene.addMenuItem(backToMenuItem);

		// Add tutorial button
		IMenuItem tutorialMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(TUTORIAL_ID,
						TextureManager.tutorialButtonTextureReg, vbom),
				ExpandedButtonSize, ButtonSize());
		moveToTopCenter(tutorialMenuItem);
		move(tutorialMenuItem, 0, y + 2);
		menuChildScene.addMenuItem(tutorialMenuItem);

		float center = (GameActivity.CAMERA_WIDTH - backToMenuItem.getWidth()
				- tutorialMenuItem.getWidth() - GameActivity.CAMERA_WIDTH
				* DistanceBetweenElements) / 2;
		backToMenuItem.setX(center);
		tutorialMenuItem.setX(center + backToMenuItem.getWidth()
				+ GameActivity.CAMERA_WIDTH * DistanceBetweenElements);

	}

	/**
	 * Moves a Button around in the menugrid
	 * 
	 * @param menuItem
	 * @param x
	 * @param y
	 */
	private void move(IMenuItem menuItem, float x, float y) {
		float gridwidth = (ButtonSize()) * MenuItemPixel
				+ DistanceBetweenElements * GameActivity.CAMERA_WIDTH;
		Log.d(TAG, "GRIDWIDTH: " + gridwidth);
		Log.d(TAG, "X: " + x);
		Log.d(TAG, "Y: " + y);
		menuItem.setPosition(menuItem.getX() + x * gridwidth, menuItem.getY()
				+ y * gridwidth);

	}

	/**
	 * Assigns the right Texture to the levelstatus.
	 * 
	 * @param levelID
	 * @return
	 */
	private ITextureRegion getMenuTexture(int levelID) {
		Status status = LevelManager.getInstance().getStatus(levelID);
		switch (status) {
		case LOCKED:
			return TextureManager.levelSelectLocked;
		case PLAYABLE:
			return TextureManager.levelSelectOpen;
		case SOLVED1STAR:
			return TextureManager.levelSelectSolved1;
		case SOLVED2STAR:
			return TextureManager.levelSelectSolved2;
		case SOLVED3STAR:
			return TextureManager.levelSelectSolved3;
		}
		return null;
	}

	/**
	 * This code centers the item. Note that the usage of menuItem.getWidth is
	 * correct, although this is 256 pixels and NOT the actual size of the
	 * button.
	 * 
	 * @param menuItem
	 */
	public void moveToTopCenter(IMenuItem menuItem) {

		menuItem.setPosition(
				(GameActivity.CAMERA_WIDTH - menuItem.getWidth()) * 0.5f,
				(GameActivity.CAMERA_HEIGHT - menuItem.getHeight())
						* MenuTopOffset);

	}
}