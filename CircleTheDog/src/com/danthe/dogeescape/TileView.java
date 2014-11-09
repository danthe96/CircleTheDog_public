package com.danthe.dogeescape;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.model.Level;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.model.TileType;

public class TileView extends TiledSprite implements ChangeListener {

	public final Tile tile;

	// TODO remove additional tile Positions
	public TileView(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion circleTextureReg,
			VertexBufferObjectManager pVertexBufferObjectManager, Tile tile) {
		super(pX, pY, pWidth, pHeight, circleTextureReg,
				pVertexBufferObjectManager);
		this.tile = tile;
		updateGraphics();

		tile.addChangeListener(this);
	}

	private void updateGraphics() {
		switch (tile.getTileType()) {
		case EMPTY:
			this.setCurrentTileIndex(0);
			break;
		case STAKE:
			this.setCurrentTileIndex(1);
			this.setPosition(mX - mWidth * .35f, mY - mHeight / 2f);
			this.setWidth(1.7f * mWidth);
			this.setHeight(1.5f * mHeight);
			break;
		case ROCK:
			this.setCurrentTileIndex(2);
			break;
		case ICE:
			this.setCurrentTileIndex(6 - tile.getCountdown());
			break;
		case LAVA:
			this.setCurrentTileIndex(6);
			break;
		case SWAMP:
			this.setCurrentTileIndex(7);
			break;
		case TURTLE:
			this.setCurrentTileIndex(8);
			break;
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
				&& Level.playersTurn) {
			switch (tile.getTileType()) {
			case EMPTY:
				tile.setTileTypeOnHumanOrder(TileType.STAKE);
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

	@Override
	public void onStateChanged() {
		updateGraphics();
	}

}
