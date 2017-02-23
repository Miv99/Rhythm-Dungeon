package com.miv;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.miv.Movement.Direction;

import dungeons.Dungeon;

public class InputHandler implements InputProcessor {
	private Dungeon dungeon;
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(dungeon != null) {
			if(keycode == Input.Keys.LEFT) {
				Movement.moveEntity(dungeon.getFloors()[dungeon.getCurrentFloor()], dungeon.getPlayer(), Direction.Left);
			} else if(keycode == Input.Keys.RIGHT) {
				Movement.moveEntity(dungeon.getFloors()[dungeon.getCurrentFloor()], dungeon.getPlayer(), Direction.Right);
			} else if(keycode == Input.Keys.UP) {
				Movement.moveEntity(dungeon.getFloors()[dungeon.getCurrentFloor()], dungeon.getPlayer(), Direction.Up);
			} else if(keycode == Input.Keys.DOWN) {
				Movement.moveEntity(dungeon.getFloors()[dungeon.getCurrentFloor()], dungeon.getPlayer(), Direction.Down);
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
