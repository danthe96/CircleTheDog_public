package com.danthe.dogeescape.view.scenes;

import java.util.LinkedList;

import org.andengine.entity.scene.Scene;
import com.danthe.dogeescape.KeyListener;

public abstract class DogScene extends Scene implements KeyListener {

	protected LinkedList<KeyListener> keyListeners = new LinkedList<KeyListener>();

}
