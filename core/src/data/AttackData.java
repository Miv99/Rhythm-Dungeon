package data;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.miv.Movement.Direction;

import utils.GeneralUtils;

public class AttackData {
	public static class TileAttackData {
		private boolean isFocus;
		private boolean isAttack;
		private String animationOnTileName;
		
		public void setIsFocus(boolean isFocus) {
			this.isFocus = isFocus;
		}
		
		public void setIsAttack(boolean isAttack) {
			this.isAttack = isAttack;
		}
		
		public void setAnimationOnTileName(String animationOnTileName) {
			this.animationOnTileName = animationOnTileName;
		}
		
		public boolean getIsFocus() {
			return isFocus;
		}
		
		public boolean getIsAttack() {
			return isAttack;
		}
		
		public String getAnimationOnTileName() {
			return animationOnTileName;
		}
	}
	public static enum AttackDirectionDeterminant {
		// Direction determined depends on the target's position relative to self
		// If the target lies on the border between two directions, one is chosen at random
		TARGET_RELATIVE_TO_SELF, 
		// Direction determined is the direction the target is facing
		TARGET_FACING,
		// Direction determined is the direction the attacker is facing
		SELF_FACING
	}
	
	// Component that the entity on affected tiles must have so that it is affected by the attack
	private Class<? extends Component> entityHittableRequirement;
	// Beats after the attack starts that the tiles are actually checked for entities
	private int attackDelayInBeats;
	// Beats after the attack starts that the attacker cannot move for
	private int disabledMovementTimeInBeats;
	private boolean warnTilesBeforeAttack;
	// Marks which tiles are to be targetted depending TileAttackDirectionType (see its comments)
	// Each 2d array can have only one AttackTileType of either isSelf or isTarget being true
	private HashMap<Direction, TileAttackData[][]> directionalTilesAttackData;
	// Determines how Direction in directionalTilesAttackData is determined
	private AttackDirectionDeterminant attackDirectionDeterminant;
	private String attackerAnimationName;
	
	//TODO: status ailment
	
	/**
	 * Use when tile attack data are not simply rotations of each other
	 */
	public AttackData(Class<? extends Component> entityHittableRequirement, int attackDelayInBeats, boolean warnTilesBeforeAttack,
			AttackDirectionDeterminant attackDirectionDeterminant, int disabledMovementTimeInBeats,
			String attackerAnimationName, HashMap<Direction, TileAttackData[][]> directionalTilesAttackData) {
		this.entityHittableRequirement = entityHittableRequirement;
		this.attackDelayInBeats = attackDelayInBeats;
		this.warnTilesBeforeAttack = warnTilesBeforeAttack;
		this.attackDirectionDeterminant = attackDirectionDeterminant;
		this.disabledMovementTimeInBeats = disabledMovementTimeInBeats;
		this.attackerAnimationName = attackerAnimationName;
		this.directionalTilesAttackData = directionalTilesAttackData;
	}
	
	/**
	 * Rotates the tile attack data for each possible direction given the right-facing tile attack data
	 * @param targetTilesFacingRight - which tiles are targetted if the target is facing right
	 */
	public AttackData(Class<? extends Component> entityHittableRequirement, int attackDelayInBeats, boolean warnTilesBeforeAttack,
			AttackDirectionDeterminant attackDirectionDeterminant, int disabledMovementTimeInBeats, 
			String attackerAnimationName, TileAttackData[][] targetTilesFacingRight) {
		this.entityHittableRequirement = entityHittableRequirement;
		this.attackDelayInBeats = attackDelayInBeats;
		this.warnTilesBeforeAttack = warnTilesBeforeAttack;
		this.attackDirectionDeterminant = attackDirectionDeterminant;
		this.disabledMovementTimeInBeats = disabledMovementTimeInBeats;
		this.attackerAnimationName = attackerAnimationName;
		
		// Rotate target tiles
		directionalTilesAttackData = new HashMap<Direction, TileAttackData[][]>();
		directionalTilesAttackData.put(Direction.Right, targetTilesFacingRight);
		directionalTilesAttackData.put(Direction.Left, (TileAttackData[][])GeneralUtils.horizontallyFlipArray(targetTilesFacingRight));
		TileAttackData[][] rotatedCounterClockwise = (TileAttackData[][])GeneralUtils.rotateCounterClockwise(targetTilesFacingRight);
		directionalTilesAttackData.put(Direction.Down, rotatedCounterClockwise);
		directionalTilesAttackData.put(Direction.Up, (TileAttackData[][])GeneralUtils.verticallyFlipArray(rotatedCounterClockwise));
	}
	
	public String getAttackerAnimationName() {
		return attackerAnimationName;
	}
	
	public int getDisabledMovementTimeInBeats() {
		return disabledMovementTimeInBeats;
	}
	
	public AttackDirectionDeterminant getAttackDirectionDeterminant() {
		return attackDirectionDeterminant;
	}
	
	public Class<? extends Component> getEntityHittableRequirement() {
		return entityHittableRequirement;
	}
	
	public int getAttackDelayInBeats() {
		return attackDelayInBeats;
	}
	
	public boolean getWarnTilesBeforeAttack() {
		return warnTilesBeforeAttack;
	}
	
	public HashMap<Direction, TileAttackData[][]> getDirectionalTilesAttackData() {
		return directionalTilesAttackData;
	}
}