package com.danthe.dogeescape.view.scenes;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.animator.DirectMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

import android.content.Context;
import android.graphics.Typeface;

import com.danthe.dogeescape.R;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

public class PauseMenu extends MenuScene {

	private static PauseMenu instance = null;

	private static ITextureRegion textBoxTextureReg;
	private static Font comicSansFont;

	public static void loadPauseSceneResources(final BaseGameActivity activity) {

		ITexture textBoxTexture;
		try {
			textBoxTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/textbox.png");
						}
					});
			textBoxTexture.load();
			textBoxTextureReg = TextureRegionFactory
					.extractFromTexture(textBoxTexture);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		comicSansFont = FontFactory.create(activity.getFontManager(), activity
				.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				Typeface.createFromAsset(activity.getAssets(),
						"ttf/LDFComicSans.ttf"), 46f, true,
				Color.WHITE_ARGB_PACKED_INT);
		activity.getFontManager().loadFont(comicSansFont);
		comicSansFont.getTexture().load();

	}

	public static PauseMenu createScene(Camera camera, Context context,
			VertexBufferObjectManager vbo) {
		if (instance == null)
			instance = new PauseMenu(camera, context, vbo);

		return instance;
	}

	public PauseMenu(Camera camera, Context context,
			VertexBufferObjectManager vbo) {

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
