package components;

import com.badlogic.ashley.core.Component;

import movement_ai.MovementAI;

public class MovementAIComponent implements Component {
	private MovementAI movementAI;
	
	public MovementAIComponent(MovementAI movementAI) {
		this.movementAI = movementAI;
	}

	public MovementAI getMovementAI() {
		return movementAI;
	}

	public void setMovementAI(MovementAI movementAI) {
		this.movementAI = movementAI;
	}
}
