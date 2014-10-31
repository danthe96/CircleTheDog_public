package com.danthe.dogeescape;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Tile extends TiledSprite {

	private boolean blocked;
	private DogeActivity parent;

	public Tile(float pX, float pY, ITiledTextureRegion circleTextureReg,
			VertexBufferObjectManager pVertexBufferObjectManager,
			boolean blocked, DogeActivity parent) {
		super(pX, pY, 64, 64, circleTextureReg, pVertexBufferObjectManager);

		this.blocked = blocked;
		this.parent = parent;

		if (!blocked)
			this.setCurrentTileIndex(1);
		else
			this.setCurrentTileIndex(0);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (parent.isGameLoaded() && !parent.gameScene.hasChildScene()
				&& pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
				&& !blocked) {
			if (parent.blockTile(this)) {
				this.blocked = true;
				this.setCurrentTileIndex(0);
			}
		}
		return true;
	}

	public boolean isBlocked() {
		return blocked;
	}

}
