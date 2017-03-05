package enemy_ai;

public abstract class Stationary extends EnemyAI {
	public Stationary(EnemyAIParams params) {
		super(params);
	}

	@Override
	public abstract void onActivation();

	@Override
	public void onNewBeat() {
		// Do nothing
	}
}
