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
import utils.GeneralUtils;
import utils.MapUtils;

public class FireBlast extends EntityAI {
	private HitboxComponent targetHitbox;
	private HitboxComponent selfHitbox;
	private String entityAttackAnimationName;

	public FireBlast(EntityAIParams params, Entity self, int activationRadiusInTiles, String entityAttackAnimationName) {
		super(params, self, activationRadiusInTiles);
		
		this.entityAttackAnimationName = entityAttackAnimationName;
		targetHitbox = ComponentMappers.hitboxMapper.get(target);
		selfHitbox = ComponentMappers.hitboxMapper.get(self);
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		Direction targetRelativeToSelf = MapUtils.getRelativeDirection(targetHitbox.getCenterMapPosition(), selfHitbox.getCenterMapPosition());
		int dist = 0;
		if(targetRelativeToSelf == Direction.RIGHT || targetRelativeToSelf == Direction.LEFT) {
			dist = Math.abs(targetHitbox.getCenterMapPositionX() - selfHitbox.getCenterMapPositionX());
		} else {
			dist = Math.abs(targetHitbox.getCenterMapPositionY() - selfHitbox.getCenterMapPositionY());
		}
		
		// Horizontal or vertical line of explosions
		TileAttackData[][] t1 = new TileAttackData[dist + 1][1];
		t1[0][0] = new TileAttackData(true, false, "none");
		for(int x = 1; x < t1.length; x++) {
			t1[x][0] = new TileAttackData(false, true, "explosion");
		}
		
		// Explosion where line ends
		TileAttackData[][] t2 = new TileAttackData[dist + 2][3];
		t2[0][1] = new TileAttackData(true, false, "none");
		t2[dist][0] = new TileAttackData(false, true, "explosion");
		if(dist - 1 == 0) {
			t2[dist - 1][1] = new TileAttackData(true, true, "explosion");
		} else {
			t2[dist - 1][1] = new TileAttackData(false, true, "explosion");
		}
		t2[dist][1] = new TileAttackData(false, true, "explosion");
		t2[dist + 1][1] = new TileAttackData(false, true, "explosion");
		t2[dist][2] = new TileAttackData(false, true, "explosion");
		
		ArrayList<TileAttackData[][]> tileAttackDataArray = new ArrayList<TileAttackData[][]>();
		tileAttackDataArray.add(t1);
		tileAttackDataArray.add(t2);
		AttackData attackData = new AttackData(PlayerComponent.class, 2, new boolean[] { true, true },
				AttackDirectionDeterminant.TARGET_RELATIVE_TO_SELF, 4, 2, entityAttackAnimationName, tileAttackDataArray);
		
		EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackData, entityFactory);
	}

}
