package com.danthe.dogeescape.view.scenes;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.danthe.dogeescape.GameActivity;

public class Cloud extends Sprite{

	public static final float cloud_heigth = 300f;
	public static final float cloud_width = 300f;
	
	private IEntityModifier entityModifier;

	public Cloud(float height, float time, float sizeFactor,
			VertexBufferObjectManager pSpriteVertexBufferObject, TextureRegion cloud) {
		super(-600, height, cloud_width*sizeFactor, cloud_heigth*sizeFactor, cloud, pSpriteVertexBufferObject);
		
		entityModifier = new LoopEntityModifier(new MoveModifier(time,-600, GameActivity.CAMERA_WIDTH, getY(), getY()));
		
		registerEntityModifier(entityModifier);
		
	}

}
