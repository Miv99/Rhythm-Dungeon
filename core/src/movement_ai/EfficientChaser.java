package movement_ai;

import com.badlogic.ashley.core.Entity;

import entity_ai.PathFinder;

public class EfficientChaser extends MovementAI {
	private int beatsSinceActivation;
	
	public EfficientChaser(MovementAIParams params, Entity self, int activationRadiusInTiles) {
		super(params, self, activationRadiusInTiles);
	}

	@Override
	public void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		if(activated) {
			beatsSinceActivation++;
			if(beatsSinceActivation % 1 == 0) {
				PathFinder.calculateBestPath(dungeon.getFloors()[dungeon.getCurrentFloor()].getTiles(), activationRadiusInTiles, self, target);
			}
		}
	}
}
