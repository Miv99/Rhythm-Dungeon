package entity_ai;

import com.badlogic.ashley.core.Component;
import com.miv.EntityActions;

import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;

/**
 * Fires a new expanding pulse after the old pulse reaches its maximum radius
 */
public class PulsatingExpandingRingTrap extends Stationary {
	// Size is equal to ring max radius
	private AttackData[] attackData;
	
	int currentRadius = 0;
	
	/**
	 * @param pulseExpansionFrequencyInBeats - number of beats before the pulse expands by a radius of 1
	 */
	public PulsatingExpandingRingTrap(EntityAIParams params, Class<? extends Component> entityHittableRequirement, boolean warnTilesBeforeAttack,
			int ringMaxRadiusInTiles, String animationOnTile) {
		super(params);
		
		ringMaxRadiusInTiles++;
		
		currentRadius = 0;
		
		// Create AttackData for each radius of the ring
		attackData = new AttackData[ringMaxRadiusInTiles];
		for(int i = 0; i < ringMaxRadiusInTiles; i++) {
			TileAttackData[][] tileAttackData = new TileAttackData[i*2 + 1][i*2 + 1];
			for(int x = 0; x < tileAttackData.length; x++) {
				for(int y = 0;  y < tileAttackData[x].length; y++) {
					tileAttackData[x][y] = new TileAttackData(false, (Math.abs(x - y) == i) || (x + y == i), animationOnTile);
				}
			}
			for(int a = 1; a < i; a++) {
				tileAttackData[i + a][i + (i - a)] = new TileAttackData(false, true, animationOnTile);
			}
			tileAttackData[i][i] = new TileAttackData(true, false, animationOnTile);
			attackData[i] = new AttackData(entityHittableRequirement, 1, warnTilesBeforeAttack,
					AttackDirectionDeterminant.SELF_FACING, 0, "none", tileAttackData);
		}
	}
	
	@Override
	public void onActivation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewBeat() {
		if(activated) {
			EntityActions.entityStartAttack(options, audio, dungeon, self, target, attackData[currentRadius], entityFactory);
			currentRadius++;
			if(currentRadius >= attackData.length) {
				currentRadius = 0;
			}
		}
	}
}
