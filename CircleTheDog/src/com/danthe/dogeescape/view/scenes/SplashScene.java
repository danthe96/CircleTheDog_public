package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * Screen to be displayed while the resources are loading.
 * 
 * @author Daniel
 * 
 */
public class SplashScene extends Scene {

	// private static SplashScene instance = null;

	private static BitmapTextureAtlas splashTextureAtlas;
	private static TextureRegion splashTextureRegion;

	// Method loads all of the splash scene resources
	public static void loadSplashSceneResources(BaseGameActivity activity) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 512, 512, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(splashTextureAtlas, activity, "zores.png", 0,
						0);
		splashTextureAtlas.load();
	}

	public static SplashScene createScene(Camera camera,
			BaseGameActivity activity) {
		return new SplashScene(camera, activity);
	}

	SplashScene(Camera camera, BaseGameActivity activity) {
		setBackground(new Background(0, 0, 0));
		Sprite splash = new Sprite(0, 0, 768, 768, splashTextureRegion,
				activity.getVertexBufferObjectManager()) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.1f);
		splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f,
				(camera.getHeight() - splash.getHeight()) * 0.5f);
		attachChild(splash);

	}
}
