package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.LevelManager;
import com.danthe.dogeescape.model.level.LevelManager.Story;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

public class StorySelectScene extends Scene implements IOnMenuItemClickListener {
	private static final String TAG = "STORY_SELECT_SCENE";

	private MenuScene menuChildScene;
	// private Scene background;

	private SceneSetter sceneSetter;

	private static final float HINT_X = 0.5f;
	private static final float HINT_Y = 0.9f;

	private StorySelectScene(Context context,
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter) {
		this.sceneSetter = levelSceneSetter;
		// createBackground(vertexBufferObjectManager);
		createMenuChildScene(context, vertexBufferObjectManager, camera);
		setBackgroundEnabled(false);
	}

	public static StorySelectScene createScene(Context context,
			VertexBufferObjectManager vertexBufferObjectManager, Camera camera,
			SceneSetter levelSceneSetter) {
		StorySelectScene result = new StorySelectScene(context,
				vertexBufferObjectManager, camera, levelSceneSetter);
		return result;

	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Story selectedStory = Story.values()[pMenuItem.getID()];
		sceneSetter.setLevelSelectScene(selectedStory);
		TextureManager.click.play();
		return true;
	}

	// private void createBackground( VertexBufferObjectManager
	// vertexBufferObjectManager) {
	// background = new Scene();
	//
	// Sprite backgroundSprite = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
	// GameActivity.CAMERA_HEIGHT, TextureManager.appBackground,
	// vertexBufferObjectManager);
	// background.setBackground(new SpriteBackground(0, 0, 0,
	// backgroundSprite));
	// setChildScene(background);
	//
	// }

	private void createMenuChildScene(Context context,
			VertexBufferObjectManager vbo, Camera camera) {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);// GameActivity.CAMERA_HEIGHT*MenuTopOffset);

		// generateGrid(vbom);

		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);
		Log.d(TAG, "CREATE SCENE");

		int[] storyStrings = { R.string.story_0, R.string.story_1 };

		for (Story story : LevelManager.Story.values()) {
			TextMenuItem storyText = new TextMenuItem(1,
					TextureManager.comicSansFont,
					context.getText(storyStrings[story.ordinal()]), vbo);
			final IMenuItem backItem = new AnimatedSpriteMenuItem(
					story.ordinal(), 825, 330,
					TextureManager.storyTextures[story.ordinal()], vbo, true,
					false, storyText);
			menuChildScene.addMenuItem(backItem);
		}

		menuChildScene.setMenuAnimator(new DirectMenuAnimator());
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		setChildScene(menuChildScene);

		Text hint = new Text(0, 0, TextureManager.comicSansFont,
				context.getText(R.string.story_select), new TextOptions(
						HorizontalAlign.CENTER), vbo);
		hint.setPosition(
				HINT_X * GameActivity.CAMERA_WIDTH - HINT_X * hint.getWidth(),
				HINT_Y * GameActivity.CAMERA_HEIGHT);
		attachChild(hint);
	}

}