package entity_ai;

import java.util.ArrayList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.miv.EntityActions;

import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import movement_ai.Stationary;

/**
 * ExplodingTrap generates its own attack pattern
 * 
 * eg radius of 2:
 * --x--
 * -xxx-
 * xxoxx
 * -xxx-
 * --x--
 */
public class ExplodingTrap extends EntityAI {
	private AttackData attackData;
	
	public ExplodingTrap(EntityAIParams params, Entity self, int activationRadiusInTiles, Class<? extends Component> entityHittableRequirement, boolean warnTilesBeforeAttack,
			int explosionRadiusInTiles, int attackDelayInBeats, String animationOnTile) {
		super(params, self, activationRadiusInTiles);
		
		// Create AttackData for the explosion
		TileAttackData[][] tileAttackData = new TileAttackData[explosionRadiusInTiles*2 + 1][explosionRadiusInTiles*2 + 1];
		for(int x = 0; x < tileAttackData.length; x++) {
			for(int y = 0;  y < tileAttackData[x].length; y++) {
				tileAttackData[x][y] = new TileAttackData(false, Math.abs(x - y) >= explosionRadiusInTiles, animationOnTile);
			}
		}
		tileAttackData[explosionRadiusInTiles][explosionRadiusInTiles] = new TileAttackData(true, true, animationOnTile);
		
		ArrayList<TileAttackData[][]> tileAttackDataArray = new ArrayList<TileAttackData[][]>();
		tileAttackDataArray.add(tileAttackData);
		attackData = new AttackData(entityHittableRequirement, attackDelayInBeats, warnTilesBeforeAttack,
				AttackDirectionDeterminant.SELF_FACING, 0, 0, "none", tileAttackDataArray);
	}

	@Override
	public void onActivation() {
		EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackData, entityFactory);
		EntityActions.killEntity(entityFactory, audio, engine, dungeon.getFloors()[dungeon.getCurrentFloor()], self);
	}

	@Override
	public void onNewBeat() {
		// TODO Auto-generated method stub
		
	}
}
