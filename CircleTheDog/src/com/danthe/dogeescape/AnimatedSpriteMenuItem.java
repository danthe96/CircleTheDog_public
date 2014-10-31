package com.danthe.dogeescape;

import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class AnimatedSpriteMenuItem extends SpriteMenuItem {

	private Text[] items;
	private boolean animate;

	public AnimatedSpriteMenuItem(int pID, float pWidth, float pHeight,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			boolean animate, boolean centerXOnly, Text... items) {
		super(pID, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		this.items = items;
		this.animate = animate;

		// this.setBlendFunction(GLES20.GL_SRC_ALPHA,
		// GLES20.GL_ONE_MINUS_SRC_ALPHA);

		for (Text item : items) {
			this.attachChild(item);
			if (!centerXOnly)
				item.setPosition(
						this.getX() + this.getWidth() / 2 - item.getWidth() / 2,
						this.getY() + this.getHeight() / 2 - item.getHeight()
								/ 2);
			else
				item.setPosition(
						this.getX() + this.getWidth() / 2 - item.getWidth() / 2,
						this.getY() + 32);
		}

	}

	@Override
	public void onSelected() {
		if (animate)
			for (Text item : items)
					item.setColor(Color.YELLOW);
	}

	@Override
	public void onUnselected() {
		if (animate)
			for (Text item : items)
					item.setColor(Color.WHITE);
	}

}
