package entity_ai;

import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.miv.ComponentMappers;
import com.miv.EntityActions;

import components.HitboxComponent;
import components.PlayerComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;

public class RandomNearbyExplosion extends EntityAI {
	private String entityAttackAnimationName;
	private int explosionMaxDistanceFromSelf;

	public RandomNearbyExplosion(EntityAIParams params, Entity self, int activationRadiusInTiles, 
			String entityAttackAnimationName, int explosionMaxDistanceFromSelf) {
		super(params, self, activationRadiusInTiles);
		
		this.entityAttackAnimationName = entityAttackAnimationName;
		this.explosionMaxDistanceFromSelf = explosionMaxDistanceFromSelf;
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		// Explosion random distance away from self
		int deltaX = MathUtils.random(explosionMaxDistanceFromSelf);
		int deltaY = MathUtils.random(explosionMaxDistanceFromSelf);
		TileAttackData[][] t1 = new TileAttackData[explosionMaxDistanceFromSelf*2 + 3][explosionMaxDistanceFromSelf*2 + 3];
		setTileAttackData(t1, explosionMaxDistanceFromSelf, explosionMaxDistanceFromSelf, true, false, "none");
		setTileAttackData(t1, explosionMaxDistanceFromSelf + deltaX, explosionMaxDistanceFromSelf + deltaY + 1, false, true, "explosion");
		setTileAttackData(t1, explosionMaxDistanceFromSelf + deltaX + 1, explosionMaxDistanceFromSelf + deltaY, false, true, "explosion");
		setTileAttackData(t1, explosionMaxDistanceFromSelf + deltaX - 1, explosionMaxDistanceFromSelf + deltaY, false, true, "explosion");
		setTileAttackData(t1, explosionMaxDistanceFromSelf + deltaX, explosionMaxDistanceFromSelf + deltaY - 1, false, true, "explosion");
		
		ArrayList<TileAttackData[][]> tileAttackDataArray = new ArrayList<TileAttackData[][]>();
		tileAttackDataArray.add(t1);
		AttackData attackData = new AttackData(PlayerComponent.class, 2, new boolean[] { true, true },
				AttackDirectionDeterminant.TARGET_RELATIVE_TO_SELF, 4, 2, entityAttackAnimationName, tileAttackDataArray);
		
		EntityActions.entityStartAttack(engine, options, audio, dungeon, self, target, attackData, entityFactory);
	}
	
	// Retain isFocus if tile attack data already exists on that location
	private void setTileAttackData(TileAttackData[][] t, int x, int y, boolean isFocus, boolean isAttack, String animationOnTile) {
		if(t[x][y] == null) {
			t[x][y] = new TileAttackData(isFocus, isAttack, animationOnTile);
		} else {
			t[x][y] = new TileAttackData(t[x][y].isFocus(), isAttack, animationOnTile);
		}
	}
}
