package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.LevelManager;
import com.danthe.dogeescape.model.level.LevelManager.Story;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

public class StorySelectScene extends Scene implements IOnMenuItemClickListener {
	private static final String TAG = "STORY_SELECT_SCENE";

	private MenuScene menuChildScene;

	private SceneSetter sceneSetter;

	private static ITextureRegion textBoxTextureReg = TextureManager.textBoxTextureReg;
	private static Font comicSansFont = TextureManager.comicSansFont;

	private StorySelectScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter) {
		this.sceneSetter = levelSceneSetter;
		createBackground(vertexBufferObjectManager);
		createMenuChildScene(vertexBufferObjectManager, camera);
	}

	public static StorySelectScene createScene(
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter) {
		StorySelectScene result = new StorySelectScene(
				vertexBufferObjectManager, camera, levelSceneSetter);
		return result;

	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Story selectedStory = Story.values()[pMenuItem.getID()];
		sceneSetter.setLevelSelectScene(selectedStory);
		return true;
	}

	private void createBackground(
			VertexBufferObjectManager vertexBufferObjectManager) {
		Sprite background = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
				GameActivity.CAMERA_HEIGHT, TextureManager.appBackground,
				vertexBufferObjectManager);
		setBackground(new SpriteBackground(0, 0, 0, background));

	}

	private void createMenuChildScene(VertexBufferObjectManager vbo,
			Camera camera) {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);// GameActivity.CAMERA_HEIGHT*MenuTopOffset);

		// generateGrid(vbom);

		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);
		Log.d(TAG, "CREATE SCENE");

		for (Story story : LevelManager.Story.values()) {
			TextMenuItem storyText = new TextMenuItem(1, comicSansFont,
					story.getOutputString(), vbo);
			final IMenuItem backItem = new AnimatedSpriteMenuItem(
					story.ordinal(), 550, 220, textBoxTextureReg, vbo, true,
					false, storyText);
			menuChildScene.addMenuItem(backItem);
		}
		// final IMenuItem continueItem = new AnimatedSpriteMenuItem(0, 550,
		// 220,
		// textBoxTextureReg, vbo, true, false, continueText);
		// menuChildScene.addMenuItem(continueItem);
		//
		// TextMenuItem backText = new TextMenuItem(1, comicSansFont,
		// "test", vbo);
		// final IMenuItem backItem = new AnimatedSpriteMenuItem(1, 550, 220,
		// textBoxTextureReg, vbo, true, false, backText);

		menuChildScene.setMenuAnimator(new DirectMenuAnimator());
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		setChildScene(menuChildScene);
	}

}