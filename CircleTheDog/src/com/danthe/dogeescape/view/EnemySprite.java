package com.danthe.dogeescape.view;

import java.util.List;

import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBounceOut;

import com.danthe.dogeescape.TextureManager;
import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.model.level.Level;
import com.danthe.dogeescape.view.DogeModifier.AnimationState;
import com.danthe.dogeescape.view.scenes.GameScene;

public class EnemySprite extends AnimatedSprite implements ChangeListener,
		IAnimationListener {

	private Enemy enemy;
	List<TileView> tileViews;
	private GameScene parent;
	private int oldPosition;

	private DogeModifier dogeModifier;
	// dog sprite is a little bit further up than its corresponding tile
	public final float OFFSET_Y;


	public EnemySprite(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, Enemy enemy,
			List<TileView> tileViews, GameScene parent) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);

		this.setEnemy(enemy);
		this.tileViews = tileViews;
		this.parent = parent;

		OFFSET_Y = pY - tileViews.get(enemy.getPosition()).getY();

//		animate(new long[] { 200, 250 }, 0, 1, true, this);

		setOldPosition(enemy.getPosition());
		MoveYModifier enemyIn = new MoveYModifier(
				(float) (0.7f * (Math.random() / 20f + 1f)), -200, getY(),
				EaseBounceOut.getInstance());
		this.registerEntityModifier(enemyIn);
		dogeModifier = new DogeModifier(this);
		
	}

	@Override
	public void onStateChanged() {

		if (Level.won) {
			TextureManager.doublebark.stop();
			animate(new long[] { 200, 250 }, 0, 1, true, this);
			TextureManager.lose_whining.play();
			return;
		} else if (Level.lost) {
			TextureManager.doublebark.stop();
			TextureManager.win_bark.play();
			animate(new long[] { 250, 100, 250, 100, 250, 100, 250, 800, 225,
					250, 225, 250 }, new int[] { 4, 0, 4, 0, 4, 0, 4, 0, 4, 0,
					4, 0 }, 1, this);
			return;
		}

		this.setZIndex(2 * enemy.getPosition() + 4);
		this.setZIndex(2 * getEnemy().getPosition() + 4);
		parent.sortChildren();

//		float XStep = tileViews.get(getEnemy().getPosition()).getX();
//		float YStep = tileViews.get(getEnemy().getPosition()).getY() + OFFSET_Y;
//
//		this.unregisterEntityModifier(enemyIn);
//		IEntityModifier entityModifier = new DogeMoveModifier(0.2f, tileViews
//				.get(getOldPosition()).getX(), XStep, tileViews.get(getOldPosition())
//				.getY() + OFFSET_Y, YStep);
//		registerEntityModifier(entityModifier);
		dogeModifier.setState(AnimationState.MOVE);
		setOldPosition(getEnemy().getPosition());

//		if (getEnemy().hasWon()) {
//			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3, this);
//		} else if (getEnemy().hasLost()) {
//			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3, this);
//		}

		oldPosition = enemy.getPosition();

		if (enemy.hasWon()) {
			TextureManager.doublebark.play();
			animate(new long[] { 250, 100 }, new int[] { 4, 0 }, 2, this);
		} else if (enemy.hasLost()) {
			TextureManager.doublebark.play();
			animate(new long[] { 250, 100 }, new int[] { 4, 0 }, 2, this);
		}

	}

	@Override
	public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
			int pInitialLoopCount) {
	}

	@Override
	public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
			int pOldFrameIndex, int pNewFrameIndex) {
	}

	@Override
	public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
			int pRemainingLoopCount, int pInitialLoopCount) {
	}

	@Override
	public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
		animate(new long[] { 200, 250 }, 0, 1, true, this);
	}

	public Enemy getEnemy() {
		return enemy;
	}

	public void setEnemy(Enemy enemy) {
		this.enemy = enemy;
	}

	public int getOldPosition() {
		return oldPosition;
	}

	public void setOldPosition(int oldPosition) {
		this.oldPosition = oldPosition;
	}

}
