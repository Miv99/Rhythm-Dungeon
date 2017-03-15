package data;

public class HitboxData {
	public enum HitboxType {
		TANGIBLE(true, false, false),
		TANGIBLE_ATTACKABLE(true, true, false),
		// Point of origin of attacks
		TANGIBLE_ATTACKABLE_ATTACK_ORIGIN(true, true, true),
		INTANGIBLE(false, false, false),
		INTANGIBLE_ATTACKABLE(false, true, false);
		
		// Tangibility refers to collisions with the player and with walls
		private boolean tangible;
		private boolean attackable;
		private boolean attackOrigin;
		
		HitboxType(boolean tangible, boolean attackable, boolean attackOrigin) {
			this.tangible = tangible;
			this.attackable = attackable;
			this.attackOrigin = attackOrigin;
		}
		
		public boolean isTangible() {
			return tangible;
		}
		
		public boolean isAttackable() {
			return attackable;
		}
		
		public boolean isAttackOrigin() {
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
