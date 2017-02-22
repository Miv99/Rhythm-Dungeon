package enemy_ai;

import com.badlogic.ashley.core.Entity;

public abstract class EfficientChaser extends EnemyAI {
	private Entity player;
	private Entity self;
	
	public EfficientChaser(Entity self, Entity player) {
		this.player = player;
		this.self = self;
	}

	@Override
	public abstract void onActivation();

	@Override
	public void onNewBeat() {
		//TODO: use A* to have the enemy 
	}
}
