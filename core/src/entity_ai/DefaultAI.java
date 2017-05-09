package entity_ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.miv.EntityActions;

/**
 * Loops through each attack in the attackRotation array in order of the array
 */
public class DefaultAI extends EntityAI {
	private Array<String> attackRotation;
	private int attackRotationIndex = 0;
	
	public DefaultAI(EntityAIParams params, Entity self, int activationRadiusInTiles, String attack) {
		super(params, self, activationRadiusInTiles);
		attackRotation = new Array<String>();
		attackRotation.add(attack);
	}
	
	public DefaultAI(EntityAIParams params, Entity self, int activationRadiusInTiles, Array<String> attackRotation) {
		super(params, self, activationRadiusInTiles);
		this.attackRotation = attackRotation;
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		if(activated) {
			boolean successfulAttack = EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackRotation.get(attackRotationIndex), entityFactory);
			
			if(successfulAttack) {
				attackRotationIndex++;
				if(attackRotationIndex >= attackRotation.size) {
					attackRotationIndex = 0;
				}
			}
		}
	}

}
