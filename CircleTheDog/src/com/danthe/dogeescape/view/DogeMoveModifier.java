package com.danthe.dogeescape.view;

import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.ease.EaseQuadIn;
import org.andengine.util.modifier.ease.EaseQuadInOut;

import com.danthe.dogeescape.view.scenes.GameScene;

public class DogeMoveModifier extends SequenceEntityModifier{
	
	
	
	public DogeMoveModifier(float time, float fromX, float toX, float fromY, float toY) {
		super(new MoveModifier(time/2, fromX, interpolate(fromX, toX), fromY, interpolate(fromY,toY)-GameScene.graphicalTileWidth/2, EaseQuadIn.getInstance()),
				new MoveModifier(time/2, interpolate(fromX, toX), toX, interpolate(fromY,toY)-GameScene.graphicalTileWidth/2, toY, EaseQuadIn.getInstance())
		
				
				);
				//new SequenceModifier(new MoveModifier(time/2, pFromX, pToX, pFromY, pToY))));
	}
	
	public static float interpolate(float f1, float f2) {
		return (f1+f2)/2;
	}

}
