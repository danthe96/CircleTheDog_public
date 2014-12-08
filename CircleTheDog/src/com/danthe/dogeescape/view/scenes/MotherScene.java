package com.danthe.dogeescape.view.scenes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseStrongInOut;

import android.util.Log;

import com.danthe.dogeescape.GameActivity;
import com.danthe.dogeescape.TextureManager;

/**
 * All other Scene will be attached to this scene in order to have a fixed
 * background and to allow more advanced animations
 * 
 * @author Daniel
 * 
 */
public class MotherScene extends Scene {
	private static final float TIME = 0.25f;
	private static final float CLOUD_TIME_MODIFIER = 5f;
	public static final String TAG = "MotherScene";

	public enum Direction {
		LEFT, RIGHT
	};

	public IEntityModifierListener oldSceneDetacher = new IEntityModifierListener() {

		@Override
		public void onModifierStarted(IModifier<IEntity> pModifier,
				IEntity pItem) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onModifierFinished(IModifier<IEntity> pModifier,
				IEntity pItem) {
			if (!(pItem.equals(getChildScene()))) {
				Log.d(TAG, "Detached Child");
				detachChild(pItem);
			}

		}
	};

	public MotherScene(VertexBufferObjectManager vertexBufferObjectManager) {
		Sprite backgroundSprite = new Sprite(0, 0, GameActivity.CAMERA_WIDTH,
				GameActivity.CAMERA_HEIGHT, TextureManager.appBackground,
				vertexBufferObjectManager);
		setBackground(new SpriteBackground(0, 0, 0, backgroundSprite));

		attachChild(new Cloud(CLOUD_TIME_MODIFIER * 100f, 12f, 2f,
				vertexBufferObjectManager, TextureManager.clouds[0]));
		attachChild(new Cloud(CLOUD_TIME_MODIFIER * 300f, 15f, 2.5f,
				vertexBufferObjectManager, TextureManager.clouds[1]));
		attachChild(new Cloud(CLOUD_TIME_MODIFIER * 200f, 9f, 2f,
				vertexBufferObjectManager, TextureManager.clouds[2]));

		Sprite logo = new Sprite(0, 0, GameActivity.CAMERA_WIDTH, 512,
				TextureManager.logoTextureRegion, vertexBufferObjectManager);
		attachChild(logo);
	}

	public void swapScene(Scene newScene) {
		Scene oldScene = getChildScene();
		if (oldScene == null) {
			setChildScene(newScene);
			return;
		}
		HashMap<Scene, Float> oldX = new HashMap<Scene, Float>();
		float transformNewScene = getRanking(newScene) >= getRanking(oldScene) ? 2000f
				: -2000f;
		for (Scene s : getAllChildScenes(newScene)) {
			oldX.put(s, s.getX());
			s.setX(s.getX() + transformNewScene);
		}
		setChildScene(newScene);
		attachChild(oldScene);
		// boolean moveOutAction = false;
		if (getRanking(newScene) >= getRanking(oldScene)) {
			moveOut(oldScene, Direction.LEFT);
			moveIn(newScene, Direction.RIGHT, oldX);
		} else {
			moveOut(oldScene, Direction.RIGHT);
			moveIn(newScene, Direction.LEFT, oldX);
		}

		// if (newScene instanceof LevelSelectScene
		// && oldScene instanceof StorySelectScene) {
		// moveOut(oldScene, Direction.LEFT);
		// moveIn(newScene, Direction.RIGHT);
		//
		// }
		// if (oldScene instanceof LevelSelectScene
		// && newScene instanceof StorySelectScene) {
		// Log.d(TAG, "animate to left");
		// moveOut(oldScene, Direction.RIGHT);
		// moveIn(newScene, Direction.LEFT);
		// moveOutAction = true;
		// }
		// if (oldScene instanceof LevelSelectScene
		// && newScene instanceof GameScene) {
		// Log.d(TAG, "animate to left");
		// moveOut(oldScene, Direction.LEFT);
		// moveIn(newScene, Direction.RIGHT);
		// moveOutAction = true;
		// }
		//
		// if (oldScene instanceof GameScene
		// && newScene instanceof LevelSelectScene) {
		// Log.d(TAG, "animate to left");
		// moveOut(oldScene, Direction.RIGHT);
		// moveIn(newScene, Direction.LEFT);
		// moveOutAction = true;
		// }
		//
		// if (oldScene instanceof GameScene
		// && newScene instanceof StorySelectScene) {
		// Log.d(TAG, "animate to left");
		// moveOut(oldScene, Direction.RIGHT);
		// moveIn(newScene, Direction.LEFT);
		// moveOutAction = true;
		// }
		// if (moveOutAction == false)
		// detachChild(oldScene);

	}

	private int getRanking(Scene scene) {
		if (scene instanceof StorySelectScene) {
			return 0;
		} else if (scene instanceof LevelSelectScene) {
			return 1;
		} else if (scene instanceof GameScene) {
			return 2;
		}
		return -1;
	}

	private void moveOut(Scene scene, Direction direction) {
		float aim = (direction == Direction.RIGHT) ? 2000f : -2000f;
		// Yes this is seriously necessary because otherwise childscenes wont
		// move
		for (Scene s : getAllChildScenes(scene)) {
			IEntityModifier moveOut = new MoveModifier(TIME, s.getX(), s.getX()
					+ aim, s.getY(), s.getY(), EaseStrongInOut.getInstance());
			moveOut.addModifierListener(oldSceneDetacher);
			s.registerEntityModifier(moveOut);
		}
	}

	private void moveIn(Scene scene, Direction direction,
			HashMap<Scene, Float> destinationX) {
		// float start = (direction == Direction.RIGHT) ? 2000f : -2000f;
		// Yes this is seriously necessary because otherwise childscenes wont
		// move
		for (Scene s : getAllChildScenes(scene)) {
			IEntityModifier moveIn = new MoveModifier(TIME, s.getX(),
					destinationX.get(s), s.getY(), s.getY(),
					EaseStrongInOut.getInstance());
			// moveIn.addModifierListener(oldSceneDetacher);
			s.registerEntityModifier(moveIn);

		}
	}

	private LinkedList<Scene> getAllChildScenes(Scene scene) {
		LinkedList<Scene> list = new LinkedList<Scene>();
		getAllChildScenes(scene, list);
		return list;
	}

	private void getAllChildScenes(Scene scene, List<Scene> list) {
		if (scene == null)
			return;
		getAllChildScenes(scene.getChildScene(), list);
		list.add(scene);
	}

	// public static void deepRegisterEntityModifier(Scene scene,
	// IEntityModifier entityModifier) {
	//
	// scene.registerEntityModifier(entityModifier);
	// if (scene.getChildScene() != null)
	// deepRegisterEntityModifier(scene.getChildScene(), entityModifier);
	// }
	// // public static void deepMove(Scene scene, float X, float Y) {
	// // scene.setX(scene.getX()+X);
	// // scene.setY(scene.getY()+Y);
	// // if (scene.getChildScene() != null) deepMove(scene.getChildScene(), X,
	// Y);
	// // }

}
