package com.miv;

import com.badlogic.ashley.core.ComponentMapper;

import components.AnimationComponent;
import components.AttackComponent;
import components.EnemyComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.PlayerComponent;

public class ComponentMappers {
	public static ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<ImageComponent> imageMapper = ComponentMapper.getFor(ImageComponent.class);
	public static ComponentMapper<AttackComponent> attackMapper = ComponentMapper.getFor(AttackComponent.class);
	public static ComponentMapper<HitboxComponent> hitboxMapper = ComponentMapper.getFor(HitboxComponent.class);
	public static ComponentMapper<EnemyComponent> enemyMapper = ComponentMapper.getFor(EnemyComponent.class);
	public static ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
}
