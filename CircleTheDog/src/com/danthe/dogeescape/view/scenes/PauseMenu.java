package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SceneManager;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;
import com.danthe.dogeescape.view.TileView;

public class PauseMenu extends MenuScene implements IOnMenuItemClickListener {
	private static final String TAG = "PAUSE_MENU";
	private static PauseMenu instance = null;

	private static ITextureRegion textBoxTextureReg = TextureManager.textBoxTextureReg;
	private static Font comicSansFont = TextureManager.comicSansFont;

	private final SceneSetter sceneSetter;
	private GameScene parent;

	public static PauseMenu createScene(Camera camera, Context context,
			VertexBufferObjectManager vbo) {
		if (instance == null)
			instance = new PauseMenu(camera, context, vbo);

		return instance;
	}

	private PauseMenu(Camera camera, Context context,
			VertexBufferObjectManager vbo) {
		super(camera);
		Log.d(TAG, "CREATE SCENE");

		sceneSetter = SceneManager.getSceneSetter();

		TextMenuItem continueText = new TextMenuItem(0, comicSansFont,
				context.getText(R.string.resume), vbo);
		final IMenuItem continueItem = new AnimatedSpriteMenuItem(0, 550, 220,
				textBoxTextureReg, vbo, true, false, continueText);
		this.addMenuItem(continueItem);

		TextMenuItem backText = new TextMenuItem(1, comicSansFont,
				context.getText(R.string.back), vbo);
		final IMenuItem backItem = new AnimatedSpriteMenuItem(1, 550, 220,
				textBoxTextureReg, vbo, true, false, backText);
		this.addMenuItem(backItem);

		this.setMenuAnimator(new DirectMenuAnimator());
		this.buildAnimations();
		this.setBackgroundEnabled(false);
		this.setOnMenuItemClickListener(this);

	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		parent = GameScene.getInstance();
		switch (pMenuItem.getID()) {
		case 0:
			parent.switchChildScene();
			TileView.blockInput = false;
			return true;
		case 1:
			sceneSetter.setScene(SceneType.LEVELSELECTSCENE);
			return true;
		}

		return false;
	}

}
