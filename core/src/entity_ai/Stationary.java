package entity_ai;

public abstract class Stationary extends EntityAI {
	public Stationary(EntityAIParams params) {
		super(params);
	}

	@Override
	public abstract void onActivation();

	@Override
	public void onNewBeat() {
		// Do nothing
	}
}
