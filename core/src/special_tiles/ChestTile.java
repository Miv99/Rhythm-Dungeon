package special_tiles;

public class ChestTile extends SpecialTile {
	public ChestTile randomizeContent(int currentFloor) {
		//TODO: randomize content based on current floor
		return this;
	}

	@Override
	public void onPlayerTrigger() {
		// TODO: give player items
	}

	@Override
	public void onEnemyTrigger() {
		
	}

}
