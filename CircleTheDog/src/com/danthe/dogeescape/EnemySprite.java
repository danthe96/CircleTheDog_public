package com.danthe.dogeescape;

import java.util.List;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.model.Enemy;

public class EnemySprite extends AnimatedSprite implements ChangeListener {

	private Enemy enemy;
	private List<TileView> tileViews;
	private GameActivity parent;

	public EnemySprite(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, Enemy player,
			List<TileView> tileViews, GameActivity parent) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);

		this.enemy = player;
		this.tileViews = tileViews;
		this.parent = parent;
	}

	@Override
	public void onStateChanged() {
		float xStep = (tileViews.get(enemy.getPosition()).getX() - mX) / 10f;
		float yStep = (tileViews.get(enemy.getPosition()).getY() - 9
				* tileViews.get(0).getWidth() / 8 - mY) / 10f;

		for (int i = 0; i < 10; i++) {

			mX += xStep;
			mY += yStep;

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		this.setZIndex(tileViews.get(enemy.getPosition()).getZIndex() + 1);
		parent.resortElements();

		if (enemy.hasWon()) {
			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3);
			try {
				Thread.sleep(1050);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			animate(new long[] { 200, 250 }, 0, 1, true);
		} else if (enemy.hasLost()) {
			animate(new long[] { 100, 250 }, new int[] { 0, 4 }, 3);
			try {
				Thread.sleep(1050);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			animate(new long[] { 200, 250 }, 0, 1, true);
		}

	}
}
