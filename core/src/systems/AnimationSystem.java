package systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.miv.ComponentMappers;

import components.AnimationComponent;
import components.ImageComponent;

public class AnimationSystem extends EntitySystem {
	private ImmutableArray<Entity> entities;
		
	public AnimationSystem() {
		
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(AnimationComponent.class, ImageComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}

	@Override
	public void update(float deltaTime) {
		for(Entity e : entities) {
			AnimationComponent animation = ComponentMappers.animationMapper.get(e);
			ImageComponent image = ComponentMappers.imageMapper.get(e);
			
			animation.update(deltaTime);
			if(animation.getCurrentAnimation() != null) {
				image.setSprite(animation.getKeyFrame());
			}
			if(animation.getQueuedIdleAnimation()) {
				animation.startAnimation(animation.getIdleAnimationName() + "_" + image.getFacing().getStringRepresentation(), PlayMode.NORMAL);
				animation.setPlayingIdleAnimation(true);
				animation.setQueuedIdleAnimation(false);
			}
		}
	}
}