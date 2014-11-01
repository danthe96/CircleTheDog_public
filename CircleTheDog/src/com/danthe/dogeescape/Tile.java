package com.danthe.dogeescape;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Tile extends TiledSprite {

	private boolean blocked;
	private GameActivity parent;

	public Tile(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion circleTextureReg,
			VertexBufferObjectManager pVertexBufferObjectManager,
			boolean blocked, GameActivity parent) {
		super(pX, pY, pWidth, pHeight, circleTextureReg,
				pVertexBufferObjectManager);

		this.blocked = blocked;
		this.parent = parent;

		if (!blocked)
			this.setCurrentTileIndex(1);
		else
			this.setCurrentTileIndex(0);
	}

	// public Tile(float pX, float pY, ITiledTextureRegion circleTextureReg,
	// VertexBufferObjectManager pVertexBufferObjectManager,
	// boolean blocked, DogeActivity parent){
	// this(pX, pY, 64, 64, circleTextureReg, pVertexBufferObjectManager,
	// blocked, parent);
	// }

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (parent.isGameLoaded() && !parent.gameScene.hasChildScene()
				&& pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
				&& !blocked) {
			if (parent.blockTile(this)) {
				this.blocked = true;
				this.setPosition(mX - mWidth * .35f, mY - mHeight / 2f);
				this.setWidth(1.7f * mWidth);
				this.setHeight(1.5f * mHeight);

				this.setCurrentTileIndex(0);
			}
		}
		return true;
	}

	public boolean isBlocked() {
		return blocked;
	}

}
