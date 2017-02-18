package com.miv;

import com.badlogic.ashley.core.Entity;

import dungeons.Floor;

public class Movement {
	public enum Direction {
		Up,
		Down,
		Left,
		Right
	}
	
	public static void moveEntity(Floor floor, Entity entity, Direction direction, int distance) {
		for(int i = 0; i < distance; i++) {
			moveEntity(floor, entity, direction);
		}
	}
	
	public static void moveEntity(Floor floor, Entity entity, Direction direction) {
		//TODO: update hitboxcomponent, update imagecp,[pmemt if any
		
		// Update tangibleOcucpant on all tiles in the floor that the entity's hitboxes now preside in
		
		// Begin movement animation
	}
}
