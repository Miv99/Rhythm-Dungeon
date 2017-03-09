package com.miv;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import components.AttackComponent;

public class Death {
	public static void killEntity(Engine engine, Entity entity) {
		// Remove warning tiles
		if(ComponentMappers.attackMapper.has(entity)) {
			AttackComponent attackComponent = ComponentMappers.attackMapper.get(entity);
			attackComponent.getWarningTiles().clear();
		}
		
		//TODO: do some death animation thing (use same animation for every entity death) and remove entity from engine after animation finishes
	}
}
