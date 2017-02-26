package com.miv;

import com.badlogic.ashley.core.Entity;

import dungeons.Floor;

public class Attack {
	public static void entityAttack(Options options, Floor floor, Entity attacker) {
		// Take into account direction of attacker
		
		// TODO: if attacker has PlayerComponent, multiply damage by options.difficulty.playerDamageMultiplier
	}
}
