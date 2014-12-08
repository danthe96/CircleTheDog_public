package com.danthe.dogeescape.view;

import org.andengine.entity.modifier.IEntityModifier;

/**
 * To Be continued
 * @author Daniel
 *
 */
@SuppressWarnings("unused")
public class MenuChangeAnimation {

	public enum Direction {
		LEFT,
		RIGHT
	};
	
	private IEntityModifier moveIn;
	private IEntityModifier moveOut;
	private Direction direction;
	
	public MenuChangeAnimation(Direction direction) {
		this.direction = direction;
		
		
	}
	
//	public void registerMoveIns
}
