package com.danthe.dogeescape.view;

import java.util.List;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.model.Enemy;
import com.danthe.dogeescape.view.scenes.GameScene;

public class EnemySprite extends AnimatedSprite implements ChangeListener {

	private Enemy enemy;
	private List<TileView> tileViews;
	private GameScene parent;

	public EnemySprite(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, Enemy player,
			List<TileView> tileViews, GameScene parent) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);

		this.enemy = player;
		this.tileViews = tileViews;
		this.parent = parent;
	}

	@Override
	public void onStateChanged() {

		this.setZIndex(2*enemy.getPosition() + 4);
		parent.sortChildren();

		float xStep = (tileViews.get(enemy.getPosition()).getX() - mX) / 10f;
		float yStep = (tileViews.get(enemy.getPosition()).getY() - 9
				* parent.getGraphicalTileWidth() / 8 - mY) / 10f;

		for (int i = 0; i < 10; i++) {

			mX += xStep;
			mY += yStep;

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

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
