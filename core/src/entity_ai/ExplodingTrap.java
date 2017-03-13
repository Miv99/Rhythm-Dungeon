package entity_ai;

import com.badlogic.ashley.core.Component;
import com.miv.Attack;

import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;

/**
 * ExplodingTrap generates its own attack pattern
 */
public class ExplodingTrap extends Stationary {
	private AttackData attackData;
	
	public ExplodingTrap(EnemyAIParams params, Class<? extends Component> entityHittableRequirement, boolean warnTilesBeforeAttack,
			int explosionRadiusInTiles, int attackDelayInBeats, String animationOnTile) {
		super(params);
		
		// Create AttackData for the explosion
		TileAttackData[][] tileAttackData = new TileAttackData[explosionRadiusInTiles*2 + 1][explosionRadiusInTiles*2 + 1];
		for(int x = 0; x < explosionRadiusInTiles; x++) {
			for(int y = 0;  y < explosionRadiusInTiles; y++) {
				if(Math.abs(x - y) >= explosionRadiusInTiles) {
					tileAttackData[x][y] = new TileAttackData(false, true, animationOnTile);
				}
			}
		}
		tileAttackData[explosionRadiusInTiles][explosionRadiusInTiles] = new TileAttackData(true, true, animationOnTile);
		attackData = new AttackData(entityHittableRequirement, attackDelayInBeats, warnTilesBeforeAttack,
				AttackDirectionDeterminant.TARGET_FACING, 0, "none", tileAttackData);
	}

	@Override
	public void onActivation() {
		Attack.entityStartAttack(options, audio, dungeon, self, target, attackData, entityFactory);
	}

}
