package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.R.string;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

import android.content.Context;

public class PauseMenu extends MenuScene {

	public PauseMenu(Camera camera, Context context,
			VertexBufferObjectManager vbo, ITextureRegion textBoxTextureReg,
			Font comicSansFont) {

		super(camera);

		TextMenuItem continueText = new TextMenuItem(0, comicSansFont,
				context.getText(R.string.resume), vbo);
		final IMenuItem continueItem = new AnimatedSpriteMenuItem(0, 550, 220,
				textBoxTextureReg, vbo, true, false, continueText);
		this.addMenuItem(continueItem);

		TextMenuItem backText = new TextMenuItem(1, comicSansFont,
				context.getText(R.string.quit), vbo);
		final IMenuItem backItem = new AnimatedSpriteMenuItem(1, 550, 220,
				textBoxTextureReg, vbo, true, false, backText);
		this.addMenuItem(backItem);

		this.setMenuAnimator(new DirectMenuAnimator());
		this.buildAnimations();
		this.setBackgroundEnabled(false);

	}

}
