package com.danthe.dogeescape.view;

import java.util.List;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.view.scenes.GameScene;

public class EnemySprite extends AnimatedSprite implements ChangeListener,
		IAnimationListener {

	private Enemy enemy;
	private List<TileView> tileViews;
	private GameScene parent;
	private int oldPosition;

	// dog sprite is a little bit further up than its corresponding tile
	private final float OFFSET_Y;

	public EnemySprite(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, Enemy enemy,
			List<TileView> tileViews, GameScene parent) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);

		this.enemy = enemy;
		this.tileViews = tileViews;
		this.parent = parent;

		OFFSET_Y = pY - tileViews.get(enemy.getPosition()).getY();

		animate(new long[] { 200, 250 }, 0, 1, true, this);

		oldPosition = enemy.getPosition();
	}

	@Override
	public void onStateChanged() {

		this.setZIndex(2 * enemy.getPosition() + 4);
		parent.sortChildren();

		float XStep = tileViews.get(enemy.getPosition()).getX();
		float YStep = tileViews.get(enemy.getPosition()).getY() + OFFSET_Y;

		IEntityModifier entityModifier = new DogeMoveModifier(0.2f, tileViews
				.get(oldPosition).getX(), XStep, tileViews.get(oldPosition)
				.getY() + OFFSET_Y, YStep);
		registerEntityModifier(entityModifier);

		oldPosition = enemy.getPosition();

		if (enemy.hasWon()) {
			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3, this);
		} else if (enemy.hasLost()) {
			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3, this);
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

}
