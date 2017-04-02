package movement_ai;

import com.badlogic.ashley.core.Entity;

import entity_ai.EntityAI;
import entity_ai.EntityAI.EntityAIParams;
import movement_ai.MovementAI.MovementAIParams;

public class Stationary extends MovementAI {
	public Stationary(MovementAIParams params, Entity self, int activationRadiusInTiles) {
		super(params, self, activationRadiusInTiles);
	}

	@Override
	public void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		// Do nothing
	}
}
