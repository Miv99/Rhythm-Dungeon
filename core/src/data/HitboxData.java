package data;

public class HitboxData {
	public enum HitboxType {
		Tangible(true, false, false),
		TangibleAttackable(true, true, false),
		// Point of origin of attacks
		TangibleAttackableAttackOrigin(true, true, true),
		Intangible(false, false, false),
		IntangibleAttackable(false, true, false);
		
		// Tangibility refers to collisions with the player and with walls
		private boolean tangible;
		private boolean attackable;
		private boolean attackOrigin;
		
		HitboxType(boolean tangible, boolean attackable, boolean attackOrigin) {
			this.tangible = tangible;
			this.attackable = attackable;
			this.attackOrigin = attackOrigin;
		}
		
		public boolean getTangible() {
			return tangible;
		}
		
		public boolean getAttackable() {
			return attackable;
		}
		
		public boolean getAttackOrigin() {
			return attackOrigin;
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
