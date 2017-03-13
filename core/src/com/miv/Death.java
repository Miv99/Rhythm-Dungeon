package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import audio.Audio;
import components.AttackComponent;
import components.HealthComponent;
import components.HitboxComponent;
import data.HitboxData.HitboxType;
import dungeons.Floor;
import dungeons.Tile;

public class Death {
	public static void killEntity(Audio audio, Engine engine, Floor floor, Entity entity) {
		// Remove warning tiles
		if(ComponentMappers.attackMapper.has(entity)) {
			AttackComponent attackComponent = ComponentMappers.attackMapper.get(entity);
			attackComponent.getWarningTiles().clear();
		}
		
		// Remove entity from occupants sets in previously residing tiles
		if(ComponentMappers.hitboxMapper.has(entity)) {
			Tile[][] mapTiles = floor.getTiles();
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			Point mapPosition = hitboxComponent.getMapPosition();
			for(int x = mapPosition.x; x < mapPosition.x + hitbox.length; x++) {
				for(int y = mapPosition.y; y < mapPosition.y + hitbox[x - mapPosition.x].length; y++) {
					mapTiles[x][y].getTangibleOccupants().remove(entity);
					mapTiles[x][y].getAttackableOccupants().remove(entity);
				}
			}
		}
		
		// Play death sound
		if(ComponentMappers.healthMapper.has(entity)) {
			HealthComponent healthComponent = ComponentMappers.healthMapper.get(entity);
			if(!healthComponent.getDeathSoundName().equals("none")) {
				audio.playSoundEffect(healthComponent.getDeathSoundName());
			}
		}
		
		//TODO: do some death animation thing (use same animation for every entity death) and remove entity from engine after animation finishes
		
		//TODO: remove this
		engine.removeEntity(entity);
	}
}
