package com.miv;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {
	private Entity player;
	
	public InputHandler(Entity player) {
		this.player = player;
	}
	
	public void setPlayer(Entity player) {
		this.player = player;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(player != null) {
			if(keycode == Input.Keys.LEFT) {
				
			} else if(keycode == Input.Keys.RIGHT) {
				
			} else if(keycode == Input.Keys.UP) {
				
			} else if(keycode == Input.Keys.DOWN) {
				
			} else if(keycode == Input.Keys.Z) {
				
			} else if(keycode == Input.Keys.X) {
				
			} else if(keycode == Input.Keys.A) {
				
			} else if(keycode == Input.Keys.S) {
				
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
