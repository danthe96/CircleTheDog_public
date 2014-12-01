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

import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.LevelManager;
import com.danthe.dogeescape.model.level.Level.Status;
import com.danthe.dogeescape.model.level.LevelManager.Story;

public class LevelSelectScene extends Scene implements IOnMenuItemClickListener {
	private static final String TAG = "LEVEL_SELECT_SCENE";

	private MenuScene menuChildScene;
	private final Story story;

	private SceneSetter levelSceneSetter;
	private static final int MenuItemPixel = 256;
	private static final int ElementsPerRow = 4;

	private static final float MenuTopOffset = 0.3f;
	private static final float MenuSideOffset = 0.125f;

	private static final float DistanceBetweenElements = 0.03f;
	private static final int STORY_SELECT_ID = -666;

	private static float ButtonSize() {
		return (1f - 2 * MenuSideOffset - (ElementsPerRow - 1)
				* DistanceBetweenElements)
				* (float) GameActivity.CAMERA_WIDTH
				/ (float) MenuItemPixel
				/ (float) ElementsPerRow;

	}

	private LevelSelectScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter, Story story) {
		this.levelSceneSetter = levelSceneSetter;
		this.story = story;
		createBackground(vertexBufferObjectManager);
		createMenuChildScene(vertexBufferObjectManager, camera);
	}

	public static LevelSelectScene createScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter, Story story) {
		LevelSelectScene result = new LevelSelectScene(
				vertexBufferObjectManager, camera, levelSceneSetter, story);
		return result;

	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		if (pMenuItem.getID() == STORY_SELECT_ID) {
			levelSceneSetter.setScene(SceneType.STORYSELECTSCENE);
			return true;
		}
		
		
		int levelID = pMenuItem.getID();
		if (LevelManager.getInstance().isOpenToPlay(levelID)) {
			levelSceneSetter.setLevelScene(pMenuItem.getID());
		} else {
			// TODO Play some sound here.

		}
		return true;
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

		//Add back to Story Button
		menuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(STORY_SELECT_ID,
				TextureManager.backToMenuTextureReg, vbom), ExpandedButtonSize,
				ButtonSize());

		moveToTopCenter(menuItem);
		move(menuItem, 0, y+2);

		menuChildScene.addMenuItem(menuItem);
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