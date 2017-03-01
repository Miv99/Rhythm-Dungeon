package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;

import data.AttackData;

public class AttackComponent implements Component {
	private HashMap<String, AttackData> attacksData;
	
	public AttackComponent(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
	}
	
	public void setAttacksData(HashMap<String, AttackData> attacksData) {
		this.attacksData = attacksData;
	}
	
	public HashMap<String, AttackData> getAttacksData() {
		return attacksData;
	}
}
