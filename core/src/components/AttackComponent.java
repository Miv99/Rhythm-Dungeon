package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import data.AttackData;
import special_tiles.WarningTile;

public class AttackComponent implements Component {
	// Objects used to display a warning sign on tiles of incoming attacks
	private Array<WarningTile> warningTiles;
	private HashMap<String, AttackData> attacksData;
	private String tileBreakAttackName;
	
	public AttackComponent(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
		warningTiles = new Array<WarningTile>();
	}
	
	public AttackComponent(HashMap<String, AttackData> attacksData, String tileBreakAttackName) {
		this.attacksData = attacksData;
		warningTiles = new Array<WarningTile>();
		this.tileBreakAttackName = tileBreakAttackName;
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
}
