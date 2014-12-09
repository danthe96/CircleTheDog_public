package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.Context;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SceneManager;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

public class HowToScene extends Scene implements IOnMenuItemClickListener {

	private int panel_nr;
	private final TextureRegion tutorialBackgroundTextureReg;
	private MenuScene[] tutorialMenuScenes;// = new
											// MenuScene[TextureManager.TUTORIAL_PANEL_COUNT];

	private SceneSetter sceneSetter;
	private int lastPanelNumber;

	public static final int SCENE_WIDTH = 825, SCENE_HEIGHT = 1425,
			SCENE_X = (GameActivity.CAMERA_WIDTH - SCENE_WIDTH) / 2,
			SCENE_Y = (GameActivity.CAMERA_HEIGHT - SCENE_HEIGHT) / 2;
	private static final float CONTENT_X = 37.5f, CONTENT_Y = 37.5f;

	public HowToScene(Camera cam, VertexBufferObjectManager vbo,
			Context context, int panelStart, int panelEnd) {
		super();
		tutorialMenuScenes = new MenuScene[panelEnd - panelStart];
		tutorialBackgroundTextureReg = TextureManager.tutorialBackgroundTextureReg;
		sceneSetter = SceneManager.getSceneSetter();
		panel_nr = 0;
		lastPanelNumber = panelEnd - panelStart;

		this.setBackgroundEnabled(false);

		Sprite filterSprite = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
				GameActivity.CAMERA_HEIGHT,
				TextureManager.backgroundFilterTextureReg, vbo);
		attachChild(filterSprite);

		Sprite backgroundSprite = new Sprite(SCENE_X, SCENE_Y, SCENE_WIDTH,
				SCENE_HEIGHT, tutorialBackgroundTextureReg, vbo);
		attachChild(backgroundSprite);

		for (int i = panelStart; i < panelEnd; i++) {
			tutorialMenuScenes[i - panelStart] = new HowToMenuScene(cam, vbo,
					context, i);
			tutorialMenuScenes[i - panelStart].setOnMenuItemClickListener(this);
		}

		this.setChildScene(tutorialMenuScenes[panel_nr]);
		tutorialMenuScenes[panel_nr].setPosition(SCENE_X + CONTENT_X, SCENE_Y
				+ CONTENT_Y);

	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case HowToMenuScene.CONTINUE_ID:
			panel_nr++;
			if (panel_nr < lastPanelNumber) {
				this.setChildScene(tutorialMenuScenes[panel_nr]);
				tutorialMenuScenes[panel_nr].setPosition(SCENE_X + CONTENT_X,
						SCENE_Y + CONTENT_Y);
			} else
				sceneSetter.setScene(SceneType.LEVELSELECTSCENE);
			break;
		}
		return false;
	}

	class HowToMenuScene extends MenuScene {

		private static final int CONTINUE_ID = 0;

		private final TextureRegion[] tutorialPictures;
		private final int[] TEXT_RESOURCES = { R.string.tutorial_1,
				R.string.tutorial_2, R.string.tutorial_3, R.string.tutorial_4,
				R.string.tutorial_5, R.string.tutorial_6 };

		private final float MENUSCENE_WIDTH = HowToScene.SCENE_WIDTH - 2
				* HowToScene.CONTENT_X,
				MENUSCENE_HEIGHT = HowToScene.SCENE_HEIGHT - 2
						* HowToScene.CONTENT_Y;

		HowToMenuScene(Camera cam, VertexBufferObjectManager vbo,
				Context context, int panel_nr) {
			super(cam);
			tutorialPictures = TextureManager.tutorialPictures;

			Sprite howToPicItem = new Sprite(0, 0, MENUSCENE_WIDTH, 750,
					tutorialPictures[panel_nr], vbo);
			attachChild(howToPicItem);

			Text manualText = new Text(0, howToPicItem.getY()
					+ howToPicItem.getHeight() + 30,
					TextureManager.defaultFont,
					context.getText(TEXT_RESOURCES[panel_nr]), new TextOptions(
							HorizontalAlign.CENTER), vbo);
			manualText.setColor(Color.BLACK);
			attachChild(manualText);
			manualText.setX((MENUSCENE_WIDTH - manualText.getWidth()) / 2);

			TextMenuItem continueText = new TextMenuItem(CONTINUE_ID,
					TextureManager.comicSansFont,
					context.getText(R.string.next), vbo);
			IMenuItem continueItem = new ScaleMenuItemDecorator(
					new AnimatedSpriteMenuItem(0, MENUSCENE_WIDTH, 225,
							TextureManager.textBoxTextureReg, vbo, true, false,
							continueText), 1.05f, 1f);
			this.addMenuItem(continueItem);
			continueItem.setY(MENUSCENE_HEIGHT - continueItem.getHeight());

			setBackgroundEnabled(false);
		}

	}

}
