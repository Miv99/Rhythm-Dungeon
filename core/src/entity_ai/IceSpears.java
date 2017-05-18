package entity_ai;

import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
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

public class IceSpears extends EntityAI {
	private HitboxComponent selfHitbox;
	private String entityAttackAnimationName;

	public IceSpears(EntityAIParams params, Entity self, int activationRadiusInTiles, String entityAttackAnimationName) {
		super(params, self, activationRadiusInTiles);
		
		this.entityAttackAnimationName = entityAttackAnimationName;
		selfHitbox = ComponentMappers.hitboxMapper.get(self);
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		// Horizontal and vertical line of ice shards from self
		Tile[][] mapTiles = dungeon.getFloors()[dungeon.getCurrentFloor()].getTiles();
		int tilesVisibleLeft = MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfHitbox.getAttackOrigin().x, selfHitbox.getAttackOrigin().y, Direction.LEFT);
		int tilesVisibleRight = MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfHitbox.getAttackOrigin().x, selfHitbox.getAttackOrigin().y, Direction.RIGHT);
		int tilesVisibleUp = MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfHitbox.getAttackOrigin().x, selfHitbox.getAttackOrigin().y, Direction.UP);
		int tilesVisibleDown = MapUtils.countVisibleIntangibleTilesInDirection(mapTiles, selfHitbox.getAttackOrigin().x, selfHitbox.getAttackOrigin().y, Direction.DOWN);
		int xDistWall = tilesVisibleLeft + tilesVisibleRight + 1;
		int yDistWall = tilesVisibleUp + tilesVisibleDown + 1;
		TileAttackData[][] t1 = new TileAttackData[xDistWall][yDistWall];
		t1[tilesVisibleLeft][tilesVisibleDown] = new TileAttackData(true, false, "none");
		for(int x = 0; x < t1.length; x++) {
			if(x != tilesVisibleLeft) {
				t1[x][tilesVisibleDown] = new TileAttackData(false, true, "ice_shard");
			}
		}
		for(int y = 0; y < t1[0].length; y++) {
			if(y != tilesVisibleDown) {
				t1[tilesVisibleLeft][y] = new TileAttackData(false, true, "ice_shard");
			}
		}
		
		ArrayList<TileAttackData[][]> tileAttackDataArray = new ArrayList<TileAttackData[][]>();
		tileAttackDataArray.add(t1);
		AttackData attackData = new AttackData(PlayerComponent.class, 1, new boolean[] { true, true },
				AttackDirectionDeterminant.ALWAYS_RIGHT_FROM_SELF, 3, 2, entityAttackAnimationName, tileAttackDataArray);
		
		EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackData, entityFactory);
	}
}
