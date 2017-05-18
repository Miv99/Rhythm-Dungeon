package entity_ai;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.miv.ComponentMappers;
import com.miv.EntityActions;
import com.miv.EntityActions.Direction;

import components.HitboxComponent;
import components.PlayerComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import dungeons.Tile;
import utils.MapUtils;

public class RandomIceSpears extends EntityAI {
	private HitboxComponent selfHitbox;
	private String entityAttackAnimationName;
	private int iceSpearMaxLengthInOneDirection;
	private int iceSpearsMaxDistanceFromSelf;
	private int iceSpearsCount;

	public RandomIceSpears(EntityAIParams params, Entity self, int activationRadiusInTiles, String entityAttackAnimationName, 
			int iceSpearMaxLengthInOneDirection, int iceSpearsMaxDistanceFromSelf, int iceSpearsCount) {
		super(params, self, activationRadiusInTiles);
		
		this.entityAttackAnimationName = entityAttackAnimationName;
		this.iceSpearMaxLengthInOneDirection = iceSpearMaxLengthInOneDirection;
		this.iceSpearsMaxDistanceFromSelf = iceSpearsMaxDistanceFromSelf;
		this.iceSpearsCount = iceSpearsCount;
		selfHitbox = ComponentMappers.hitboxMapper.get(self);
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		// Horizontal and vertical line of ice shards from random locations near self
		ArrayList<TileAttackData[][]> tileAttackDataArray = new ArrayList<TileAttackData[][]>();
		Tile[][] mapTiles = dungeon.getFloors()[dungeon.getCurrentFloor()].getTiles();
		
		TileAttackData[][] t1 = new TileAttackData[iceSpearsMaxDistanceFromSelf*2 + 1][iceSpearsMaxDistanceFromSelf*2 + 1];
		TileAttackData[][] t2 = new TileAttackData[(iceSpearMaxLengthInOneDirection + iceSpearsMaxDistanceFromSelf + 1)*2 + 1][(iceSpearMaxLengthInOneDirection + iceSpearsMaxDistanceFromSelf + 1)*2 + 1];
		Point selfAttackOrigin = selfHitbox.getAttackOrigin();
		t2[selfAttackOrigin.x][selfAttackOrigin.y] = new TileAttackData(true, false, "none");
		for(int i = 0; i < iceSpearsCount; i++) {
			// Part 1 of attack is the random location of the ice spear spawn point, set near the self
			int deltaX = MathUtils.random(iceSpearsMaxDistanceFromSelf);
			int deltaY = MathUtils.random(iceSpearsMaxDistanceFromSelf);
			
			// Part 2 of attack is the horizontal and vertical lines of ice shards spawned from the ice spear spawn point
			int tilesVisibleLeft = Math.min(iceSpearMaxLengthInOneDirection, MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfAttackOrigin.x + deltaX, selfAttackOrigin.y + deltaY, Direction.LEFT));
			int tilesVisibleRight = Math.min(iceSpearMaxLengthInOneDirection, MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfAttackOrigin.x + deltaX, selfAttackOrigin.y + deltaY, Direction.RIGHT));
			int tilesVisibleUp = Math.min(iceSpearMaxLengthInOneDirection, MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfAttackOrigin.x + deltaX, selfAttackOrigin.y + deltaY, Direction.UP));
			int tilesVisibleDown = Math.min(iceSpearMaxLengthInOneDirection, MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfAttackOrigin.x + deltaX, selfAttackOrigin.y + deltaY, Direction.DOWN));
			for(int x = selfAttackOrigin.x + deltaX - tilesVisibleLeft; x < selfAttackOrigin.x + deltaX + tilesVisibleRight; x++) {
				if(x != selfAttackOrigin.x + deltaX) {
					setTileAttackData(t1, x, selfAttackOrigin.y + deltaY, false, true, "ice_shard");
				}
			}
			for(int y = selfAttackOrigin.y + deltaY - tilesVisibleDown; y < selfAttackOrigin.y + deltaY + tilesVisibleUp; y++) {
				if(y != selfAttackOrigin.y + deltaY) {
					setTileAttackData(t1, selfAttackOrigin.x + deltaX, y, false, true, "ice_shard");
				}
			}
		}
			
		tileAttackDataArray.add(t1);
		tileAttackDataArray.add(t2);
		
		AttackData attackData = new AttackData(PlayerComponent.class, 1, new boolean[] { true, true },
				AttackDirectionDeterminant.ALWAYS_RIGHT_FROM_SELF, 3, 2, entityAttackAnimationName, tileAttackDataArray);
		EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackData, entityFactory);
	}

	// Retain isFocus if tile attack data already exists on that location
	private void setTileAttackData(TileAttackData[][] t, int x, int y, boolean isFocus, boolean isAttack, String animationOnTile) {
		if(t[x][y] == null) {
			t[x][y] = new TileAttackData(isFocus, isAttack, animationOnTile);
		} else {
			t[x][y] = new TileAttackData(t[x][y].isFocus(), isAttack, animationOnTile);
		}
	}
}
