package entity_ai;

import com.badlogic.ashley.core.Component;
import com.miv.EntityActions;

import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;

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
public class ExplodingTrap extends Stationary {
	private AttackData attackData;
	
	public ExplodingTrap(EntityAIParams params, Class<? extends Component> entityHittableRequirement, boolean warnTilesBeforeAttack,
			int explosionRadiusInTiles, int attackDelayInBeats, String animationOnTile) {
		super(params);
		
		// Create AttackData for the explosion
		TileAttackData[][] tileAttackData = new TileAttackData[explosionRadiusInTiles*2 + 1][explosionRadiusInTiles*2 + 1];
		for(int x = 0; x < tileAttackData.length; x++) {
			for(int y = 0;  y < tileAttackData[x].length; y++) {
				tileAttackData[x][y] = new TileAttackData(false, Math.abs(x - y) >= explosionRadiusInTiles, animationOnTile);
			}
		}
		tileAttackData[explosionRadiusInTiles][explosionRadiusInTiles] = new TileAttackData(true, true, animationOnTile);
		attackData = new AttackData(entityHittableRequirement, attackDelayInBeats, warnTilesBeforeAttack,
				AttackDirectionDeterminant.SELF_FACING, 0, "none", tileAttackData);
	}

	@Override
	public void onActivation() {
		EntityActions.entityStartAttack(options, audio, dungeon, self, target, attackData, entityFactory);
	}
}
