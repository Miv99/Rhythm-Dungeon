package special_tiles;

import dungeons.Dungeon;

/**
 * Causes player to descend a floor
 */
public class LadderTile extends SpecialTile {
	private Dungeon dungeon;
	
	public LadderTile(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	@Override
	public void onPlayerTrigger() {
		dungeon.enterNewFloor(dungeon.getCurrentFloor() + 1);
	}

	@Override
	public void onEnemyTrigger() {
		
	}

}
