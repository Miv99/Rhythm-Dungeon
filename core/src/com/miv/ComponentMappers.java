package com.miv;

import com.badlogic.ashley.core.ComponentMapper;

import components.AnimationComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.WeaponComponent;

public class ComponentMappers {
	public static ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<ImageComponent> im = ComponentMapper.getFor(ImageComponent.class);
	public static ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
	public static ComponentMapper<HitboxComponent> hm = ComponentMapper.getFor(HitboxComponent.class);
}
