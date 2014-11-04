package com.danthe.dogeescape;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Tile extends TiledSprite {

	public TileType type;
	public boolean blocked;
	public int countdown;

	public Tile(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion circleTextureReg,
			VertexBufferObjectManager pVertexBufferObjectManager, TileType type) {
		super(pX, pY, pWidth, pHeight, circleTextureReg,
				pVertexBufferObjectManager);

		this.type = type;

		switch (type) {
		case EMPTY:
			this.setCurrentTileIndex(0);
			blocked = false;
			break;
		case STAKE:
			this.setCurrentTileIndex(1);
			this.setPosition(mX - mWidth * .35f, mY - mHeight / 2f);
			this.setWidth(1.7f * mWidth);
			this.setHeight(1.5f * mHeight);
			blocked = true;
			break;
		case ROCK:
			this.setCurrentTileIndex(2);
			blocked = true;
			break;
		case ICE:
			this.setCurrentTileIndex(3);
			blocked = true;
			countdown = 3;
			break;
		case LAVA:
			this.setCurrentTileIndex(6);
			blocked = true;
			break;
		case SWAMP:
			this.setCurrentTileIndex(7);
			blocked = false;
			break;
		case TURTLE:
			this.setCurrentTileIndex(8);
			blocked = true;
			break;
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			switch (type) {
			case EMPTY:
				type = TileType.STAKE;
				this.setCurrentTileIndex(1);
				this.setPosition(mX - mWidth * .35f, mY - mHeight / 2f);
				this.setWidth(1.7f * mWidth);
				this.setHeight(1.5f * mHeight);
				blocked = true;

				GameActivity.playersTurn = false;
				break;
			case STAKE:
			case ROCK:
			case ICE:
			case LAVA:
			case SWAMP:
			case TURTLE:
				break;
			}

			return true;
		}

		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);
	}

}
