package entity_ai;

import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.miv.EntityActions;

import components.PlayerComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;

public class RandomMeteorShower extends EntityAI {
	private String entityAttackAnimationName;
	private int meteorMaxDistanceFromSelf;
	private int meteorsPerAttack;

	public RandomMeteorShower(EntityAIParams params, Entity self, int activationRadiusInTiles, 
			String entityAttackAnimationName, int meteorMaxDistanceFromSelf, int meteorsPerAttack) {
		super(params, self, activationRadiusInTiles);
		
		this.entityAttackAnimationName = entityAttackAnimationName;
		this.meteorMaxDistanceFromSelf = meteorMaxDistanceFromSelf;
		this.meteorsPerAttack = meteorsPerAttack;
	}

	@Override
	protected void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		// Meteors spawn at random location
		TileAttackData[][] t1 = new TileAttackData[meteorMaxDistanceFromSelf*2 + 1][meteorMaxDistanceFromSelf*2 + 1];
		setTileAttackData(t1, meteorMaxDistanceFromSelf, meteorMaxDistanceFromSelf, true, false, "none");
		for(int i = 0; i < meteorsPerAttack; i++) {
			int deltaX = MathUtils.random(1, meteorMaxDistanceFromSelf);
			int deltaY = MathUtils.random(1, meteorMaxDistanceFromSelf);
			setTileAttackData(t1, meteorMaxDistanceFromSelf + deltaX, meteorMaxDistanceFromSelf + deltaY, false, true, "explosion");
		}
		
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
