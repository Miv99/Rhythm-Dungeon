package entity_ai;

import com.badlogic.ashley.core.Entity;
import com.miv.EntityActions;

public class Dragon extends EntityAI {

	public Dragon(EntityAIParams params, Entity self, int activationRadiusInTiles) {
		super(params, self, activationRadiusInTiles);
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		if(activated) {
			EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, "dragon_fire_breath", entityFactory);
		}
	}
}
