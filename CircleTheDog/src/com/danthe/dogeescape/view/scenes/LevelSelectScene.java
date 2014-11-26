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

import com.danthe.dogeescape.model.Level.Status;
import com.danthe.dogeescape.model.LevelManager;
import com.danthe.dogeescape.view.GameActivity;
import com.danthe.dogeescape.view.TextureManager;

public class LevelSelectScene extends Scene implements IOnMenuItemClickListener
{
	private static final String TAG ="LEVEL_SELECT_SCENE";

	
	private MenuScene menuChildScene;
	

	private LevelSceneSetter levelSceneSetter;
	private static final int MenuItemPixel = 256;
	private static final int ElementsPerRow = 4;
	
	private static final float MenuTopOffset = 0.3f;
	private static final float MenuSideOffset = 0.1f;
	
	private static final float DistanceBetweenElements = 0.03f;
	private static float ButtonSize() {
		return (1f - 2*MenuSideOffset - (ElementsPerRow-1)*DistanceBetweenElements)* (float) GameActivity.CAMERA_WIDTH / (float) MenuItemPixel / (float) ElementsPerRow;
		
	}
	
	private LevelSelectScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera, LevelSceneSetter levelSceneSetter) {
		this.levelSceneSetter = levelSceneSetter;
		createBackground(vertexBufferObjectManager);
		createMenuChildScene(vertexBufferObjectManager, camera);
	}



	public static LevelSelectScene createScene(VertexBufferObjectManager vertexBufferObjectManager, Camera camera,LevelSceneSetter levelSceneSetter)
	{
		LevelSelectScene result = new LevelSelectScene(vertexBufferObjectManager, camera,levelSceneSetter);
		return result;

	}

	
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		levelSceneSetter.setLevelScene(pMenuItem.getID());
		return true;
	}
	
	private void createBackground(VertexBufferObjectManager vertexBufferObjectManager)
	{
		Sprite background = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
				GameActivity.CAMERA_HEIGHT, TextureManager.appBackground,
				vertexBufferObjectManager);
		setBackground(new SpriteBackground(0, 0, 0, background));

	}
	
	private void createMenuChildScene(VertexBufferObjectManager vbom, Camera camera)
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0,0);//GameActivity.CAMERA_HEIGHT*MenuTopOffset);

		generateGrid(vbom);

		menuChildScene.setBackgroundEnabled(false);



		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}

	/**
	 * Generates the ButtonGrid. 
	 * @param vbom
	 */
	private void generateGrid(VertexBufferObjectManager vbom) {
		float ExpandedButtonSize = ButtonSize()+DistanceBetweenElements*GameActivity.CAMERA_WIDTH/MenuItemPixel;
		int x = -1;
		int y = 0;
		
		float startposX = (ElementsPerRow-1)/2f;
		float startposY = 0;
		
		IMenuItem menuItem;
		for (int levelID=0; levelID< LevelManager.getInstance().getNumLevels(); levelID++) {
			x++;
			if (x == ElementsPerRow) {
				x=0;
				y++;
			}
			menuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(levelID, getMenuTexture(levelID), vbom), ExpandedButtonSize, ButtonSize());
			moveToTopCenter(menuItem);
			
			move(menuItem, x-startposX, y-startposY);
			

			menuChildScene.addMenuItem(menuItem);
		}
	}
	
	/**
	 * Moves a Button around in the menugrid
	 * @param menuItem
	 * @param x
	 * @param y
	 */
	private void move(IMenuItem menuItem, float x, float y) {
		float gridwidth = (ButtonSize())*MenuItemPixel+DistanceBetweenElements*GameActivity.CAMERA_WIDTH;
		Log.d(TAG, "GRIDWIDTH: "+gridwidth);
		Log.d(TAG, "X: "+x);
		Log.d(TAG, "Y: "+y);
		menuItem.setPosition(menuItem.getX()+x*gridwidth, menuItem.getY()+y*gridwidth);
		
	}

	/**
	 * Assigns the right Texture to the levelstatus.
	 * @param levelID
	 * @return
	 */
	private ITextureRegion getMenuTexture(int levelID) {
		Status status = LevelManager.getInstance().getStatus(levelID);
		switch(status) {
		case SOLVED: return TextureManager.levelSelectSolved;
		case LOCKED: return TextureManager.levelSelectLocked;
		case PLAYABLE: return TextureManager.levelSelectOpen;
		}
		return null;
	}

	/**
	 * This code centers the item. Note that the usage of menuItem.getWidth is correct, although this is 256 pixels and NOT the actual size of the button.
	 * @param menuItem
	 */
	public void moveToTopCenter(IMenuItem menuItem) {
		
		menuItem.setPosition((GameActivity.CAMERA_WIDTH-menuItem.getWidth())*0.5f, (GameActivity.CAMERA_HEIGHT-menuItem.getHeight())*MenuTopOffset);
		
	}
}