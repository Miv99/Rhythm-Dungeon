package com.miv;

import com.badlogic.ashley.core.ComponentMapper;

import components.AnimationComponent;
import components.EnemyComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.PlayerComponent;
import components.WeaponComponent;

public class ComponentMappers {
	public static ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<ImageComponent> im = ComponentMapper.getFor(ImageComponent.class);
	public static ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
	public static ComponentMapper<HitboxComponent> hm = ComponentMapper.getFor(HitboxComponent.class);
	public static ComponentMapper<EnemyComponent> em = ComponentMapper.getFor(EnemyComponent.class);
	public static ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
}
