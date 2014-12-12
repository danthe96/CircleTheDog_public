package com.danthe.dogeescape.view;

import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.andengine.util.modifier.ease.EaseQuadIn;
import org.andengine.util.modifier.ease.EaseQuadInOut;
import org.andengine.util.modifier.ease.EaseQuadOut;

import android.util.Log;

import com.danthe.dogeescape.view.scenes.GameScene;

public class DogeModifier{
	public static final String TAG = "DOGE_MODIFIER";
	public enum AnimationState {
		MOVE_IN,
		MOVE
	};
	
	private AnimationState currentState = AnimationState.MOVE_IN;
	private IEntityModifier currentModifier;
	private EnemySprite enemySprite;
	
	public DogeModifier(EnemySprite enemySprite) {
		this.enemySprite = enemySprite;
		setState(currentState);
	}
	
	public void setState(AnimationState state) {
		Log.d(TAG, "New state set: "+state); 
		IEntityModifier newModifier=null;
		float XStep = enemySprite.tileViews.get(enemySprite.getEnemy().getPosition()).getX();
		float YStep = enemySprite.tileViews.get(enemySprite.getEnemy().getPosition()).getY() + enemySprite.OFFSET_Y;
		switch(state) {
		case MOVE:

			newModifier = new DogeMoveModifier(0.2f, enemySprite.tileViews
				.get(enemySprite.getOldPosition()).getX(), XStep, enemySprite.tileViews.get(enemySprite.getOldPosition())
				.getY() + enemySprite.OFFSET_Y, YStep);
			break;
		case MOVE_IN:
			newModifier = new MoveYModifier((float) (0.7f*(Math.random()/20f+1f)), -200, enemySprite.getY(), EaseBounceOut.getInstance());
			break;
		}
		IEntityModifier sequenceModifier = new SequenceEntityModifier(newModifier, getChillModifier(YStep));
	//	sequenceModifier.addModifierListener(createEntityModifierListener());
		if (currentModifier != null) enemySprite.unregisterEntityModifier(currentModifier);
		this.currentModifier = sequenceModifier;
		enemySprite.registerEntityModifier(sequenceModifier);
		Log.d(TAG, "new modifier set");
	}
	private IEntityModifier getChillModifier(float yStep) {
		return new LoopEntityModifier(new SequenceEntityModifier(
				new MoveYModifier(0.2f, yStep, yStep- GameScene.graphicalTileWidth / 4, EaseQuadOut.getInstance()),
				new MoveYModifier(0.2f, yStep- GameScene.graphicalTileWidth / 4, yStep, EaseQuadInOut.getInstance()),
				new DelayModifier((float) (0.2f*(Math.random()/10f+1f)))));

	}
	
	private class DogeMoveModifier extends SequenceEntityModifier {

		private DogeMoveModifier(float time, float fromX, float toX, float fromY,
				float toY) {
			super(new MoveModifier(time / 2, fromX, interpolate(fromX, toX), fromY,
					interpolate(fromY, toY) - GameScene.graphicalTileWidth / 2,
					EaseQuadIn.getInstance()), new MoveModifier(time / 2,
					interpolate(fromX, toX), toX, interpolate(fromY, toY)
							- GameScene.graphicalTileWidth / 2, toY,
					EaseQuadIn.getInstance())

			);
			// new SequenceModifier(new MoveModifier(time/2, pFromX, pToX, pFromY,
			// pToY))));
		}


	}

	public static float interpolate(float f1, float f2) {
		return (f1 + f2) / 2;
	}

	
	
}
