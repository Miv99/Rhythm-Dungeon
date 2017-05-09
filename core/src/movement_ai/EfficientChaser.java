package movement_ai;

import com.badlogic.ashley.core.Entity;
import com.miv.EntityActions;
import com.miv.EntityActions.Direction;

import dungeons.Floor;
import movement_ai.PathFinder.NoPathsException;

public class EfficientChaser extends MovementAI {
	public EfficientChaser(MovementAIParams params, Entity self, int activationRadiusInTiles) {
		super(params, self, activationRadiusInTiles);
	}

	@Override
	public void onActivation() {
		
	}

	@Override
	public void onNewBeat() {
		if(activated) {
			
			/**
			if(movementDisableCounter <= 0) {
				// Prioritize moving attack origin towards player. If entity has no attack origin, try to move hitbox center towards player instead.
				Floor currentFloor = dungeon.getFloors()[dungeon.getCurrentFloor()];
				int targetCenterPosX = targetHitboxComponent.getMapPosition().x + targetHitboxCenter.x;
				int targetCenterPosY = targetHitboxComponent.getMapPosition().y + targetHitboxCenter.y;
				int selfCenterPosX;
				int selfCenterPosY;
				if(selfAttackOrigin == null) {
					selfCenterPosX = selfHitboxComponent.getMapPosition().x + selfHitboxCenter.x;
					selfCenterPosY = selfHitboxComponent.getMapPosition().y + selfHitboxCenter.y;
				} else {
					selfCenterPosX = selfHitboxComponent.getMapPosition().x + selfAttackOrigin.x;
					selfCenterPosY = selfHitboxComponent.getMapPosition().y + selfAttackOrigin.y;
				}
				
				Direction targetRelativeToSelf = MapUtils.getRelativeDirection(targetCenterPosX, targetCenterPosY, selfCenterPosX, selfCenterPosY);
				
				if(EntityActions.isValidMovement(currentFloor.getTiles(), self, targetRelativeToSelf)) {
					EntityActions.moveEntity(engine, currentFloor, self, targetRelativeToSelf);
				} else {
					// If path is blocked, try to move around
					if(targetRelativeToSelf.isHorizontal()) {
						System.out.println(targetCenterPosY + ", " + selfCenterPosY);
						if(targetCenterPosY > selfCenterPosY) {
							EntityActions.moveEntity(engine, currentFloor, self, Direction.UP);
						} else if(targetCenterPosY < selfCenterPosY) {
							EntityActions.moveEntity(engine, currentFloor, self, Direction.DOWN);
						}
					} else {
						if(targetCenterPosX > selfCenterPosX) {
							EntityActions.moveEntity(engine, currentFloor, self, Direction.RIGHT);
						} else if(targetCenterPosX < selfCenterPosX) {
							EntityActions.moveEntity(engine, currentFloor, self, Direction.LEFT);
						}
					}
				}
				
				movementDisableCounter = movementDisableAfterMovementInBeats;
			} else if(!selfHitboxComponent.isMovementDisabled()) {
				movementDisableCounter--;
			}
			*/
			
			try {
				Floor currentFloor = dungeon.getFloors()[dungeon.getCurrentFloor()];
				Direction direction = PathFinder.calculateBestPathFirstStep(dungeon.getFloors()[dungeon.getCurrentFloor()].getTiles(), activationRadiusInTiles, self, target);
				EntityActions.moveEntity(engine, currentFloor, self, direction);
			} catch (NoPathsException e) {
				// Do nothing
			}
		}
	}
}
