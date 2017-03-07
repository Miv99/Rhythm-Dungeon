package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import data.AttackData;

public class AttackComponent implements Component {
	// Entities used to display a warning sign on tiles of incoming attacks
	private Array<Entity> warningTiles;
	private HashMap<String, AttackData> attacksData;
	
	public AttackComponent(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
		warningTiles = new Array<Entity>();
	}
	
	public void setAttacksData(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
	}
	
	public HashMap<String, AttackData> getAttacksData() {
		return attacksData;
	}
	
	public Array<Entity> getWarningTiles() {
		return warningTiles;
	}
}
