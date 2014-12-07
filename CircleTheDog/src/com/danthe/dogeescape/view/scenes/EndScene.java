package com.danthe.dogeescape.view.scenes;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SupportiveMessageManager;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.model.level.Level;

public class EndScene extends Scene {
	private static final int TEXTBOX_WIDTH = 867;
	private static final float TEXTBOX_HEIGHT = 577.5f;
	private static final int TEXTBOX_Y = 276;
	private static final int TEXTBOX_X = 96;
	private static final int MENUSCENE_Y = 924;
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;

	private final float WINDOW_X;
	private static final int WINDOW_Y = 525;
	private static final int WINDOW_WIDTH = 1068;
	private static final int WINDOW_HEIGHT = 1212;

	public static EndScene createScene(Context context, Camera cam,
			VertexBufferObjectManager vertexBufferObjectManager, int levelID) {
		instance = new EndScene(context, cam, vertexBufferObjectManager,
				levelID);

		return instance;
	}

	public static EndScene getInstance() {
		return instance;
	}

	private EndScene(Context context, Camera cam,
			VertexBufferObjectManager vbo, final int levelID) {
		Log.d(TAG, "CREATE SCENE");
		setY(-2000);
		TextureRegion endScreenTextureReg = TextureManager.endScreenTextureReg;
		WINDOW_X = (GameActivity.CAMERA_WIDTH - WINDOW_WIDTH) / 2;

		this.setBackgroundEnabled(false);
		Sprite backgroundSprite = new Sprite(WINDOW_X, WINDOW_Y, WINDOW_WIDTH,
				WINDOW_HEIGHT, endScreenTextureReg, vbo);
		attachChild(backgroundSprite);

		Text levelName = new Text(0, backgroundSprite.getY() + 82.5f,
				TextureManager.defaultBigFont, context.getString(
						R.string.level, levelID + 1), vbo);
		levelName.setColor(Color.BLACK);
		levelName
				.setX((backgroundSprite.getWidth() - levelName.getWidth()) / 2);
		attachChild(levelName);

		if (Level.won) {

			final int SPACE_IN_BETWEEN = 60;

			Text doge_victory = new Text(0, 0, TextureManager.defaultFont,
					context.getText(R.string.victory), vbo);
			doge_victory.setColor(Color.BLACK);
			doge_victory.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - doge_victory.getWidth()) / 2);
			attachChild(doge_victory);

			Text victory_info = new Text(0, 0, TextureManager.defaultFont,
					context.getString(R.string.victory_info, Level.turns), vbo);
			victory_info.setColor(Color.BLACK);
			victory_info.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - victory_info.getWidth()) / 2);
			attachChild(victory_info);

			float centeredY = backgroundSprite.getY()
					+ TEXTBOX_Y
					+ (TEXTBOX_HEIGHT - doge_victory.getHeight()
							- victory_info.getHeight() - SPACE_IN_BETWEEN) / 2;
			doge_victory.setY(centeredY);
			victory_info.setY(centeredY + doge_victory.getHeight()
					+ SPACE_IN_BETWEEN);

		} else if (Level.lost) {

			Text supportiveText = new Text(0, 0, TextureManager.comicSansFont,
					SupportiveMessageManager.getInstance()
							.getSupportiveMessage(), new TextOptions(
							HorizontalAlign.CENTER), vbo);
			supportiveText.setColor(Color.BLACK);
			supportiveText.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - supportiveText.getWidth()) / 2);
			attachChild(supportiveText);

			Text doge_defeat = new Text(0, 0, TextureManager.defaultFont,
					context.getString(R.string.defeat_info, Level.turns), vbo);
			doge_defeat.setColor(Color.BLACK);
			doge_defeat.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - doge_defeat.getWidth()) / 2);
			attachChild(doge_defeat);

			supportiveText.setY(backgroundSprite.getY() + TEXTBOX_Y);
			doge_defeat.setY(backgroundSprite.getY() + TEXTBOX_Y
					+ TEXTBOX_HEIGHT - doge_defeat.getHeight() - 30);

		}

		MenuButtonMenuScene menuScene = new MenuButtonMenuScene(cam, vbo,
				WINDOW_WIDTH, levelID);
		menuScene.setPosition((WINDOW_WIDTH - menuScene.getWidth()) / 2,
				WINDOW_Y + MENUSCENE_Y);
		setChildScene(menuScene);
		triggerAnimation(menuScene);

	}

	private void triggerAnimation(MenuButtonMenuScene menuScene) {
		IEntityModifier modifier = new MoveModifier(0.2f, 0, 0,-2000f,0, EaseStrongOut.getInstance());
		registerEntityModifier(modifier);
		//menuScene.registerEntityModifier(modifier.deepCopy());
		
		//menuScene.getsetBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		IEntityModifier menuSceneModifier = new MoveModifier(0.2f, menuScene.getX(), menuScene.getX(),menuScene.getY()-2000f,menuScene.getY(), EaseStrongOut.getInstance());
		menuScene.registerEntityModifier(menuSceneModifier);
		
	}
}
