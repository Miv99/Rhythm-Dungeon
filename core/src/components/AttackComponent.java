package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import data.AttackData;
import special_tiles.WarningTile;

public class AttackComponent implements Component {
	// Objects used to display a warning sign on tiles of incoming attacks
	private Array<WarningTile> warningTiles;
	private Array<WarningTile> warningTilesDeletionQueue;
	private HashMap<String, AttackData> attacksData;
	private String tileBreakAttackName;
	
	private float attackDisabledTimeInBeats;
	
	public AttackComponent(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
		warningTiles = new Array<WarningTile>();
		warningTilesDeletionQueue = new Array<WarningTile>();
	}
	
	public AttackComponent(HashMap<String, AttackData> attacksData, String tileBreakAttackName) {
		this.attacksData = attacksData;
		warningTiles = new Array<WarningTile>();
		this.tileBreakAttackName = tileBreakAttackName;
	}
	
	public void onNewBeat(float deltaBeat) {
		attackDisabledTimeInBeats -= deltaBeat;
		
		for(WarningTile warningTile : warningTiles) {
			warningTile.onNewBeat(deltaBeat);
			if(warningTile.getTimeUntilAttackInBeats() < 0f) {
				warningTilesDeletionQueue.add(warningTile);
			}
		}
		warningTiles.removeAll(warningTilesDeletionQueue, false);
		warningTilesDeletionQueue.clear();
	}
	
	public void setAttackDisabledTimeInBeats(float attackDisabledTimeInBeats) {
		this.attackDisabledTimeInBeats = attackDisabledTimeInBeats;
	}
	
	public void setAttacksData(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
	}
	
	public HashMap<String, AttackData> getAttacksData() {
		return attacksData;
	}
	
	public Array<WarningTile> getWarningTiles() {
		return warningTiles;
	}
	
	public String getTileBreakAttackName() {
		return tileBreakAttackName;
	}
	
	public float getAttackDisabledTimeInBeats() {
		return attackDisabledTimeInBeats;
	}
}
