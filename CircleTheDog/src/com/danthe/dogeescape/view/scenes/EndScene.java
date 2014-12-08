package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SupportiveMessageManager;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.model.level.Level;

public class EndScene extends Scene {
	private static final float TEXTBOX_WIDTH = 876.5f;
	private static final float TEXTBOX_HEIGHT = 577.5f;
	private static final int TEXTBOX_Y = 276;
	private static final int TEXTBOX_X = 98;
	private static final int MENUSCENE_Y = 924;
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;

	private final float WINDOW_X;
	private static final int WINDOW_Y = 525;
	private static final int WINDOW_WIDTH = 1068;
	private static final int WINDOW_HEIGHT = 1212;

	public static EndScene createScene(Activity activity, Camera cam,
			VertexBufferObjectManager vertexBufferObjectManager, Level level) {
		instance = new EndScene(activity, cam, vertexBufferObjectManager, level);

		return instance;
	}

	public static EndScene getInstance() {
		return instance;
	}

	private EndScene(Activity activity, Camera cam,
			VertexBufferObjectManager vbo, final Level level) {
		Log.d(TAG, "CREATE SCENE");

		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		int old_highscore = prefs.getInt(
				activity.getString(R.string.shared_pref_level_highscore_string)
						+ level.levelID + "_" + 0, Integer.MAX_VALUE);

		setY(-2000);
		TextureRegion endScreenTextureReg = TextureManager.endScreenTextureReg;
		WINDOW_X = (GameActivity.CAMERA_WIDTH - WINDOW_WIDTH) / 2;

		this.setBackgroundEnabled(false);
		Sprite backgroundSprite = new Sprite(WINDOW_X, WINDOW_Y, WINDOW_WIDTH,
				WINDOW_HEIGHT, endScreenTextureReg, vbo);
		attachChild(backgroundSprite);

		Text levelName = new Text(0, backgroundSprite.getY() + 82.5f,
				TextureManager.defaultBigFont, activity.getString(
						R.string.level, level.levelID + 1), vbo);
		levelName.setColor(Color.BLACK);
		levelName
				.setX((backgroundSprite.getWidth() - levelName.getWidth()) / 2);
		attachChild(levelName);

		if (Level.won) {

			final int SPACE_IN_BETWEEN = 60;

			Text doge_victory = new Text(0, 0, TextureManager.defaultFont,
					activity.getText(R.string.victory), vbo);
			doge_victory.setColor(Color.BLACK);
			doge_victory.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - doge_victory.getWidth()) / 2);
			attachChild(doge_victory);

			StringBuffer strbuf = new StringBuffer(activity.getString(
					R.string.victory_info, level.turns));
			if (old_highscore <= level.turns)
				strbuf.append(activity.getString(R.string.highscore_old,
						old_highscore));
			else {
				strbuf.append(activity.getString(R.string.highscore_new));

				Editor editor = prefs.edit();
				editor.putInt(
						activity.getString(R.string.shared_pref_level_highscore_string)
								+ level.levelID + "_" + 0, level.turns);
				editor.commit();
			}

			Text victory_info = new Text(0, 0, TextureManager.defaultFont,
					strbuf.toString(), vbo);
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

			StringBuffer strbuf = new StringBuffer(activity.getString(
					R.string.defeat_info, level.turns));
			if (old_highscore < Integer.MAX_VALUE)
				strbuf.append(activity.getString(R.string.highscore_old,
						old_highscore));

			Text doge_defeat = new Text(0, 0, TextureManager.defaultFont,
					strbuf.toString(), vbo);
			doge_defeat.setColor(Color.BLACK);
			doge_defeat.setX(TEXTBOX_X
					+ (TEXTBOX_WIDTH - doge_defeat.getWidth()) / 2);
			attachChild(doge_defeat);

			supportiveText.setY(WINDOW_Y + TEXTBOX_Y);
			doge_defeat.setY(WINDOW_Y + TEXTBOX_Y + TEXTBOX_HEIGHT
					- doge_defeat.getHeight());

		}

		MenuButtonMenuScene menuScene = new MenuButtonMenuScene(cam, vbo,
				WINDOW_WIDTH, level, false);
		menuScene.setPosition((WINDOW_WIDTH - menuScene.getWidth()) / 2,
				WINDOW_Y + MENUSCENE_Y);
		setChildScene(menuScene);
		triggerAnimation(menuScene);

	}

	private void triggerAnimation(MenuButtonMenuScene menuScene) {
		IEntityModifier modifier = new MoveModifier(0.2f, 0, 0, -2000f, 0,
				EaseStrongOut.getInstance());
		registerEntityModifier(modifier);
		// menuScene.registerEntityModifier(modifier.deepCopy());

		// menuScene.getsetBlendFunction(GL10.GL_SRC_ALPHA,
		// GL10.GL_ONE_MINUS_SRC_ALPHA);
		IEntityModifier menuSceneModifier = new MoveModifier(0.2f,
				menuScene.getX(), menuScene.getX(), menuScene.getY() - 2000f,
				menuScene.getY(), EaseStrongOut.getInstance());
		menuScene.registerEntityModifier(menuSceneModifier);

	}
}
