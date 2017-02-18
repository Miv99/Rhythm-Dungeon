package data;

public class HitboxData {
	public enum HitboxType {
		Tangible(true, false),
		TangibleAttackable(true, true),
		Intangible(false, false),
		IntangibleAttackable(false, true);
		
		// Tangibility refers to collisions with the player and with walls
		private boolean tangible;
		private boolean attackable;
		
		HitboxType(boolean tangible, boolean attackable) {
			this.tangible = tangible;
			this.attackable = attackable;
		}
		
		public boolean getTangible() {
			return tangible;
		}
		
		public boolean getAttackable() {
			return attackable;
		}
	}
	
	private HitboxType[][] hitbox;
	
	public HitboxData(HitboxType[][] hitbox) {
		this.hitbox = hitbox;
	}
	
	public HitboxType[][] getHitbox() {
		return hitbox;
	}
}
