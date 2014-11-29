package com.danthe.dogeescape.view;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.interfaces.ChangeListener;
import com.danthe.dogeescape.model.Tile;
import com.danthe.dogeescape.model.Tile.TileType;
import com.danthe.dogeescape.model.level.Level;

public class TileView extends TiledSprite implements ChangeListener {

	public final Tile tile;
	private float defaultX, defaultY, defaultWidth, defaultHeight;

	public static boolean blockInput = false;

	// TODO remove additional tile Positions
	public TileView(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion circleTextureReg,
			VertexBufferObjectManager pVertexBufferObjectManager, Tile tile) {
		super(pX, pY, pWidth, pHeight, circleTextureReg,
				pVertexBufferObjectManager);
		this.defaultX = pX;
		this.defaultY = pY;
		this.defaultWidth = pWidth;
		this.defaultHeight = pHeight;
		this.tile = tile;
		updateGraphics();

		blockInput = false;

		tile.addChangeListener(this);
	}

	private void updateGraphics() {
		switch (tile.getTileType()) {
		case EMPTY:
			this.setCurrentTileIndex(0);
			this.setPosition(defaultX, defaultY);
			this.setWidth(defaultWidth);
			this.setHeight(defaultHeight);
			break;
		case STAKE:
			this.setCurrentTileIndex(1);
			this.setPosition(defaultX - defaultWidth * .35f, defaultY - defaultHeight / 2f);
			this.setWidth(1.7f * defaultWidth);
			this.setHeight(1.5f * defaultHeight);
			break;
		case ROCK:
			this.setCurrentTileIndex(2);
			this.setPosition(defaultX - defaultWidth * .35f, defaultY - defaultHeight / 2f);
			this.setWidth(1.7f * defaultWidth);
			this.setHeight(1.5f * defaultHeight);
			break;
		case BUSH:
			this.setCurrentTileIndex(3);
			this.setPosition(defaultX - defaultWidth * .35f, defaultY - defaultHeight / 2f);
			this.setWidth(1.7f * defaultWidth);
			this.setHeight(1.5f * defaultHeight);
			break;
		case ICE:
			this.setCurrentTileIndex(7 - tile.getCountdown());
			this.setPosition(defaultX - defaultWidth * .35f, defaultY - defaultHeight / 2f);
			this.setWidth(1.7f * defaultWidth);
			this.setHeight(1.5f * defaultHeight);
			break;
		case LAVA:
			this.setCurrentTileIndex(7);
			break;
		case SWAMP:
			this.setCurrentTileIndex(8);
			break;
		case TURTLE:
			this.setCurrentTileIndex(9);
			break;
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
				&& Level.playersTurn && !blockInput) {
			switch (tile.getTileType()) {
			case EMPTY:
				tile.setTileTypeOnHumanOrder(TileType.STAKE);
				break;
			case STAKE:
			case ROCK:
			case BUSH:
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
	
	public float getDefaultWidth() {
		return defaultWidth;
	}

}
