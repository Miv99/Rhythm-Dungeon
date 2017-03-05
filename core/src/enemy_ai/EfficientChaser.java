package enemy_ai;

public abstract class EfficientChaser extends EnemyAI {
	public EfficientChaser(EnemyAIParams params) {
		super(params);
	}

	@Override
	public abstract void onActivation();

	@Override
	public void onNewBeat() {
		//TODO: use A* to have the enemy move towards the player
	}
}
