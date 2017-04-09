package movement_ai;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.miv.ComponentMappers;
import com.miv.EntityActions;
import com.miv.EntityActions.Direction;

import components.HitboxComponent;
import dungeons.Floor;
import movement_ai.PathFinder.NoPathsException;
import utils.MapUtils;

public class EfficientChaser extends MovementAI {
	private int movementDisableAfterMovementInBeats;
	private int movementDisableCounter;
	
	private HitboxComponent targetHitboxComponent;
	private HitboxComponent selfHitboxComponent;
	private Point targetHitboxCenter;
	private Point selfHitboxCenter;
	private Point selfAttackOrigin;
	
	public EfficientChaser(MovementAIParams params, Entity self, int activationRadiusInTiles, int movementDisableAfterMovementInBeats) {
		super(params, self, activationRadiusInTiles);
		
		movementDisableCounter = movementDisableAfterMovementInBeats;
		this.movementDisableAfterMovementInBeats = movementDisableAfterMovementInBeats;
		
		targetHitboxComponent = ComponentMappers.hitboxMapper.get(target);
		selfHitboxComponent = ComponentMappers.hitboxMapper.get(self);
		
		targetHitboxCenter = new Point(targetHitboxComponent.getHitbox().length/2, targetHitboxComponent.getHitbox()[0].length/2);
		selfHitboxCenter = new Point(selfHitboxComponent.getHitbox().length/2, selfHitboxComponent.getHitbox()[0].length/2);
		
		if(selfHitboxComponent.hasAttackOrigin()) {
			selfAttackOrigin = selfHitboxComponent.getAttackOrigin();
		}
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
				System.out.println("rip");
			}
		}
	}
}
