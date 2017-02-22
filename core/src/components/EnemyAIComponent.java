package components;

import com.badlogic.ashley.core.Component;

import enemy_ai.EnemyAI;

public class EnemyAIComponent implements Component {
	private EnemyAI enemyAI;
	
	public void setEnemyAI(EnemyAI enemyAI) {
		this.enemyAI = enemyAI;
	}
	
	public EnemyAI getEnemyAI() {
		return enemyAI;
	}
}
