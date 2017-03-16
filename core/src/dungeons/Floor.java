package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import data.EntityData;
import data.HitboxData.HitboxType;
import factories.EntityFactory;

public class Floor {
	// Disables all actions for all entities (eg movement, attacks)
	private boolean actionsDisabled;
	private Tile[][] tiles;
	// All non-player entities that are spawned on entering the floor
	private Array<Entity> entitiesToBeSpawned = new Array<Entity>();
	
	public Floor(int xSize, int ySize) {
		tiles = new Tile[xSize][ySize];
	}
	
	public void createBreakableTile(EntityFactory entityFactory, EntityData entityData, Point mapPosition, int healthPoints) {
		entitiesToBeSpawned.add(entityFactory.createEntity(entityData, mapPosition, healthPoints));
		tiles[mapPosition.x][mapPosition.y].setHitboxType(HitboxType.INTANGIBLE);
	}
	
	public void setActionsDisabled(boolean actionsDisabled) {
		this.actionsDisabled = actionsDisabled;
	}
	
	public Array<Entity> getEntitiesToBeSpawned() {
		return entitiesToBeSpawned;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public boolean isActionsDisabled() {
		return actionsDisabled;
	}
}
