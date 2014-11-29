package com.danthe.dogeescape.view.scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.content.Context;
import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.R;
import com.danthe.dogeescape.SceneManager.SceneType;
import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.SceneSetter;
import com.danthe.dogeescape.model.level.Level;
import com.danthe.dogeescape.model.level.LevelManager;

public class EndScene extends Scene {
	private static final String TAG = "END_SCENE";
	private static EndScene instance = null;

	private static TextureRegion endScreenTextureReg, backToMenuTextureReg,
			retryTextureReg, nextTextureReg;

	public static EndScene createScene(Context context,
			VertexBufferObjectManager vertexBufferObjectManager,
			final SceneSetter sceneSetter, int levelID) {
		instance = new EndScene(context, vertexBufferObjectManager,
				sceneSetter, levelID);

		return instance;
	}

	public static EndScene getInstance() {
		return instance;
	}

	private EndScene(Context context, VertexBufferObjectManager vbo,
			final SceneSetter sceneSetter, final int levelID) {
		Log.d(TAG, "CREATE SCENE");

		endScreenTextureReg = TextureManager.endScreenTextureReg;
		backToMenuTextureReg = TextureManager.backToMenuTextureReg;
		retryTextureReg = TextureManager.retryTextureReg;
		nextTextureReg = TextureManager.nextTextureReg;

		this.setBackgroundEnabled(false);
		Sprite backgroundSprite = new Sprite(
				(GameActivity.CAMERA_WIDTH - endScreenTextureReg.getWidth()) / 2,
				350, endScreenTextureReg, vbo);
		attachChild(backgroundSprite);

		Text levelName = new Text(0, backgroundSprite.getY() + 55,
				TextureManager.defaultBigFont, context.getString(
						R.string.level, levelID + 1), vbo);
		levelName.setColor(Color.BLACK);
		levelName
				.setX((backgroundSprite.getWidth() - levelName.getWidth()) / 2);
		attachChild(levelName);

		float startX = backgroundSprite.getX() + 64;
		if (Level.won) {

			Text doge_victory = new Text(0, 0, TextureManager.defaultFont,
					context.getText(R.string.victory), vbo);
			doge_victory.setColor(Color.BLACK);
			doge_victory.setX(startX + (578 - doge_victory.getWidth()) / 2);
			attachChild(doge_victory);

			Text victory_info = new Text(0, 0, TextureManager.defaultFont,
					context.getString(R.string.victory_info, Level.turns), vbo);
			victory_info.setColor(Color.BLACK);
			victory_info.setX(startX + (578 - victory_info.getWidth()) / 2);
			attachChild(victory_info);

			float centeredY = backgroundSprite.getY()
					+ 192
					+ (310 - doge_victory.getHeight()
							- victory_info.getHeight() - 20) / 2;
			doge_victory.setY(centeredY);
			victory_info.setY(centeredY + doge_victory.getHeight() + 20);

		} else if(Level.lost){

			Text doge_defeat = new Text(0, 0, TextureManager.defaultFont,
					context.getText(R.string.defeat), vbo);
			doge_defeat.setColor(Color.BLACK);
			doge_defeat.setX(startX + (578 - doge_defeat.getWidth()) / 2);
			attachChild(doge_defeat);

			Text defeat_info = new Text(0, 0, TextureManager.defaultFont,
					context.getString(R.string.defeat_info, Level.turns), vbo);
			defeat_info.setColor(Color.BLACK);
			defeat_info.setX(startX + (578 - defeat_info.getWidth()) / 2);
			attachChild(defeat_info);

			float centeredY = backgroundSprite.getY()
					+ 192
					+ (310 - doge_defeat.getHeight() - defeat_info.getHeight() - 20)
					/ 2;
			doge_defeat.setY(centeredY);
			defeat_info.setY(centeredY + doge_defeat.getHeight() + 20);

		}

		Sprite backToMenuSprite = new Sprite(backgroundSprite.getX()
				+ backgroundSprite.getWidth() / 2 - 64 - 128 - 32,
				backgroundSprite.getY() + 640, 128, 128, backToMenuTextureReg,
				vbo) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					sceneSetter.setScene(SceneType.LEVELSELECTSCENE);
					return true;
				}
				return false;
			}

		};
		attachChild(backToMenuSprite);
		this.registerTouchArea(backToMenuSprite);

		Sprite retrySprite = new Sprite(backgroundSprite.getX()
				+ backgroundSprite.getWidth() / 2 - 64,
				backgroundSprite.getY() + 640, 128, 128, retryTextureReg, vbo) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					sceneSetter.setLevelScene(levelID);
					return true;
				}

				return false;
			}
		};
		attachChild(retrySprite);
		this.registerTouchArea(retrySprite);

		if (LevelManager.getInstance().isOpenToPlay(levelID + 1)) {
			Sprite nextSprite = new Sprite(backgroundSprite.getX()
					+ backgroundSprite.getWidth() / 2 + 64 + 32,
					backgroundSprite.getY() + 640, 128, 128, nextTextureReg,
					vbo) {

				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						sceneSetter.setLevelScene(levelID + 1);
						return true;
					}
					return false;
				}

			};
			attachChild(nextSprite);
			this.registerTouchArea(nextSprite);
		}

	}
}
