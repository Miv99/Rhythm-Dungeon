package components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
	private String weaponEquipped;
	
	public void setWeaponEquipped(String weaponEquipped) {
		this.weaponEquipped = weaponEquipped;
	}
	
	public String getWeaponEquipped() {
		return weaponEquipped;
	}
}
