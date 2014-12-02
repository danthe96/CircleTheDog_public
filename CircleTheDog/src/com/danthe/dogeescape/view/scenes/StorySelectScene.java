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
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

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

	private static Font comicSansFont = TextureManager.comicSansFont;

	private static final float HINT_X = 0.5f;
	private static final float HINT_Y = 0.9f;

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
					story.ordinal(), 550, 220,
					TextureManager.storyTextures[story.ordinal()], vbo, true,
					false, storyText);
			menuChildScene.addMenuItem(backItem);
		}

		menuChildScene.setMenuAnimator(new DirectMenuAnimator());
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		setChildScene(menuChildScene);

		Text hint = new Text(0, 0, comicSansFont, "Select a story to start!",
				new TextOptions(HorizontalAlign.CENTER), vbo);
		hint.setPosition(
				HINT_X * GameActivity.CAMERA_WIDTH - HINT_X * hint.getWidth(),
				HINT_Y * GameActivity.CAMERA_HEIGHT);
		attachChild(hint);
	}

}