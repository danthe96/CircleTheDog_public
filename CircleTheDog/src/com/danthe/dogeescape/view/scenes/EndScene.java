package com.danthe.dogeescape.view.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
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
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SupportiveMessageManager;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.deprecated.RateAppManager;
import com.danthe.dogeescape.model.level.Level;
import com.danthe.dogeescape.view.AnimatedSpriteMenuItem;

public class EndScene extends Scene {
	private static final float TEXTBOX_WIDTH = 876.5f;
	private static final float TEXTBOX_HEIGHT = 577.5f;
	private static final int TEXTBOX_Y = 276;
	private static final int TEXTBOX_X = 98;
	private static final int MENUSCENE_Y = 924;
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;

	private static final int WINDOW_Y = 525;
	private static final int WINDOW_WIDTH = 1068;
	private static final int WINDOW_HEIGHT = 1212;
	private static final float WINDOW_X = (GameActivity.CAMERA_WIDTH - WINDOW_WIDTH) / 2f;

	private final Scene endResultScene, rateAppScene;

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

		// Part required regardless of rating status
		TextureRegion endScreenTextureReg = TextureManager.endScreenTextureReg;
		this.setBackgroundEnabled(false);
		this.setVisible(false);

		Sprite backgroundSprite = new Sprite(WINDOW_X, WINDOW_Y, WINDOW_WIDTH,
				WINDOW_HEIGHT, endScreenTextureReg, vbo);
		attachChild(backgroundSprite);

		Text levelName = new Text(0, WINDOW_Y + 82.5f,
				TextureManager.defaultBigFont, activity.getString(
						R.string.level, level.levelID + 1), vbo);
		levelName.setColor(Color.BLACK);
		levelName.setX((WINDOW_WIDTH - levelName.getWidth()) / 2);
		attachChild(levelName);
		//

		endResultScene = new EndResultScene(activity, cam, vbo, level);
		endResultScene.setPosition(WINDOW_X, WINDOW_Y);
		rateAppScene = new RateAppScene(activity, cam, vbo);
		rateAppScene.setPosition(WINDOW_X, WINDOW_Y);

		if (RateAppManager.bRateNow(activity.getApplicationContext()))
			setChildScene(rateAppScene);
		else
			setChildScene(endResultScene);

		for (Scene scene : MotherScene.getAllChildScenes(this))
			triggerAnimation(scene);

	}

	private void triggerAnimation(Scene scene) {
		// IEntityModifier modifier = new MoveModifier(0.2f, 0, 0, -2000f, 0,
		// EaseStrongOut.getInstance());
		// registerEntityModifier(modifier);
		// menuScene.registerEntityModifier(modifier.deepCopy());

		// menuScene.getsetBlendFunction(GL10.GL_SRC_ALPHA,
		// GL10.GL_ONE_MINUS_SRC_ALPHA);
		IEntityModifier modifier = new MoveModifier(0.45f, scene.getX(),
				scene.getX(), scene.getY() - 2000f, scene.getY(),
				EaseStrongOut.getInstance());
		scene.registerEntityModifier(modifier);
		modifier.addModifierListener(new IModifierListener<IEntity>() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {

				instance.setVisible(true);

			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier,
					IEntity pItem) {
				// TODO Auto-generated method stub

			}
		});
	}

	class EndResultScene extends Scene {
		private EndResultScene(Activity activity, Camera cam,
				VertexBufferObjectManager vbo, final Level level) {

			setBackgroundEnabled(false);

			SharedPreferences prefs = activity
					.getPreferences(Context.MODE_PRIVATE);
			int old_highscore = prefs
					.getInt(activity
							.getString(R.string.shared_pref_level_highscore_string)
							+ level.levelID + "_" + 0, Integer.MAX_VALUE);

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

				float centeredY = TEXTBOX_Y
						+ (TEXTBOX_HEIGHT - doge_victory.getHeight()
								- victory_info.getHeight() - SPACE_IN_BETWEEN)
						/ 2;
				doge_victory.setY(centeredY);
				victory_info.setY(centeredY + doge_victory.getHeight()
						+ SPACE_IN_BETWEEN);
			} else if (Level.lost) {
				Text supportiveText = new Text(0, 0,
						TextureManager.comicSansFont, SupportiveMessageManager
								.getInstance().getSupportiveMessage(),
						new TextOptions(HorizontalAlign.CENTER), vbo);
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

				supportiveText.setY(TEXTBOX_Y);
				doge_defeat.setY(TEXTBOX_Y + TEXTBOX_HEIGHT
						- doge_defeat.getHeight());
			}

			MenuButtonMenuScene menuScene = new MenuButtonMenuScene(cam, vbo,
					WINDOW_WIDTH, level, false);
			menuScene.setPosition((WINDOW_WIDTH - menuScene.getWidth()) / 2,
					WINDOW_Y + MENUSCENE_Y);
			setChildScene(menuScene);

		}
	}

	class RateAppScene extends Scene implements IOnMenuItemClickListener {

		private static final int RATE_ID = 0;
		private static final int FEEDBACK_ID = 1;

		private static final int SPACE_IN_BETWEEN = 60;

		private static final float RATEBUTTONS_Y = 889.5f;
		private static final float RATEBUTTONS_WIDTH = TEXTBOX_WIDTH;
		private static final int RATEBUTTONS_HEIGHT = 276;

		private Activity activity;
		private MenuScene rateMenuScene;

		RateAppScene(Activity activity, Camera cam,
				VertexBufferObjectManager vbo) {

			this.activity = activity;

			setBackgroundEnabled(false);

			Text rate_top = new Text(0, 0, TextureManager.defaultFont,
					activity.getText(R.string.rate_top), vbo);
			rate_top.setColor(Color.BLACK);
			rate_top.setX(TEXTBOX_X + (TEXTBOX_WIDTH - rate_top.getWidth()) / 2);
			attachChild(rate_top);
			Text rate_info = new Text(0, 0, TextureManager.defaultFont,
					activity.getText(R.string.rate_info), vbo);
			rate_info.setColor(Color.BLACK);
			rate_info.setX(TEXTBOX_X + (TEXTBOX_WIDTH - rate_info.getWidth())
					/ 2);
			attachChild(rate_info);

			float centeredY = TEXTBOX_Y
					+ (TEXTBOX_HEIGHT - rate_top.getHeight()
							- rate_info.getHeight() - SPACE_IN_BETWEEN) / 2;
			rate_top.setY(centeredY);
			rate_info.setY(centeredY + rate_top.getHeight() + SPACE_IN_BETWEEN);

			rateMenuScene = new MenuScene(cam);

			TextMenuItem rateText = new TextMenuItem(1,
					TextureManager.defaultFont,
					activity.getText(R.string.rate_yes), vbo);
			final IMenuItem rateItem = new ScaleMenuItemDecorator(
					new AnimatedSpriteMenuItem(RATE_ID, RATEBUTTONS_WIDTH,
							RATEBUTTONS_HEIGHT / 2,
							TextureManager.textBoxTextureReg, vbo, true, false,
							rateText), 1.1f, 1f);
			rateMenuScene.addMenuItem(rateItem);

			TextMenuItem feedbackText = new TextMenuItem(1,
					TextureManager.defaultFont,
					activity.getText(R.string.rate_no), vbo);
			final IMenuItem feedbackItem = new ScaleMenuItemDecorator(
					new AnimatedSpriteMenuItem(FEEDBACK_ID, RATEBUTTONS_WIDTH,
							RATEBUTTONS_HEIGHT / 2,
							TextureManager.textBoxTextureReg, vbo, true, false,
							feedbackText), 1.1f, 1f);
			feedbackItem.setY(rateItem.getY() + rateItem.getHeight());
			rateMenuScene.addMenuItem(feedbackItem);

			rateMenuScene.setPosition(WINDOW_X + TEXTBOX_X, WINDOW_Y
					+ RATEBUTTONS_Y);
			rateMenuScene.setBackgroundEnabled(false);
			rateMenuScene.setOnMenuItemClickListener(this);

			setChildScene(rateMenuScene);

		}

		@Override
		public boolean onMenuItemClicked(MenuScene pMenuScene,
				IMenuItem pMenuItem, float pMenuItemLocalX,
				float pMenuItemLocalY) {
			switch (pMenuItem.getID()) {
			case RATE_ID:
				Intent rateIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=com.danthe.dogeescape"));
				activity.startActivityIfNeeded(rateIntent, 0);
				instance.setChildScene(endResultScene);
				RateAppManager.neverPromptAgain(activity);
				return true;
			case FEEDBACK_ID:
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
						Uri.fromParts("mailto", "ZoresTechnologies@gmail.com",
								null));
				activity.startActivityIfNeeded(Intent.createChooser(
						emailIntent, "Send email using..."), 0);
				instance.setChildScene(endResultScene);
				RateAppManager.neverPromptAgain(activity);
				return true;
			}
			return false;
		}

	}

}
