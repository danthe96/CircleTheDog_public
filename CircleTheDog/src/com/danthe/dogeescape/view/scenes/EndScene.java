package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SupportiveMessageManager;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.model.level.Level;

public class EndScene extends Scene {
	private static final int TEXTBOX_WIDTH = 578;
	private static final int TEXTBOX_HEIGHT = 390;
	private static final int TEXTBOX_Y = 184;
	private static final int TEXTBOX_X = 64;
	private static final int MENUSCENE_Y = 600;
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;

	private final float WINDOW_X;
	private static final int WINDOW_Y = 350;
	private static final int WINDOW_WIDTH = 712;

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

		TextureRegion endScreenTextureReg = TextureManager.endScreenTextureReg;
		WINDOW_X = (GameActivity.CAMERA_WIDTH - endScreenTextureReg.getWidth()) / 2;

		this.setBackgroundEnabled(false);
		Sprite backgroundSprite = new Sprite(WINDOW_X, WINDOW_Y,
				endScreenTextureReg, vbo);
		attachChild(backgroundSprite);

		Text levelName = new Text(0, backgroundSprite.getY() + 55,
				TextureManager.defaultBigFont, context.getString(
						R.string.level, levelID + 1), vbo);
		levelName.setColor(Color.BLACK);
		levelName
				.setX((backgroundSprite.getWidth() - levelName.getWidth()) / 2);
		attachChild(levelName);

		if (Level.won) {

			final int SPACE_IN_BETWEEN = 40;

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
					+ TEXTBOX_HEIGHT - doge_defeat.getHeight());

		}

		MenuButtonMenuScene menuScene = new MenuButtonMenuScene(cam, vbo,
				WINDOW_WIDTH, levelID);
		menuScene.setPosition((WINDOW_WIDTH - menuScene.getWidth()) / 2,
				WINDOW_Y + MENUSCENE_Y);
		setChildScene(menuScene);

	}
}
