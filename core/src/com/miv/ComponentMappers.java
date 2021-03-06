package com.miv;

import com.badlogic.ashley.core.ComponentMapper;

import components.AnimationComponent;
import components.AttackComponent;
import components.EntityAIComponent;
import components.EnemyComponent;
import components.FriendlyAIComponent;
import components.HealthComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.MovementAIComponent;
import components.PlayerComponent;

public class ComponentMappers {
	public static ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<ImageComponent> imageMapper = ComponentMapper.getFor(ImageComponent.class);
	public static ComponentMapper<AttackComponent> attackMapper = ComponentMapper.getFor(AttackComponent.class);
	public static ComponentMapper<HitboxComponent> hitboxMapper = ComponentMapper.getFor(HitboxComponent.class);
	public static ComponentMapper<EnemyComponent> enemyMapper = ComponentMapper.getFor(EnemyComponent.class);
	public static ComponentMapper<EntityAIComponent> entityAIMapper = ComponentMapper.getFor(EntityAIComponent.class);
	public static ComponentMapper<MovementAIComponent> movementAIMapper = ComponentMapper.getFor(MovementAIComponent.class);
	public static ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static ComponentMapper<HealthComponent> healthMapper = ComponentMapper.getFor(HealthComponent.class);
	public static ComponentMapper<FriendlyAIComponent> friendlyAIMapper = ComponentMapper.getFor(FriendlyAIComponent.class);
}
