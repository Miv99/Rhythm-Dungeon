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
	}
	
	private HitboxType[][] hitbox;
	
	public HitboxData(HitboxType[][] hitbox) {
		this.hitbox = hitbox;
	}
	
	public static boolean getTangible(HitboxType hitboxType) {
		return hitboxType.tangible;
	}
	
	public static boolean getAttackble(HitboxType hitboxType) {
		return hitboxType.attackable;
	}
	
	public HitboxType[][] getHitbox() {
		return hitbox;
	}
}
