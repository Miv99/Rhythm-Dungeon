Format of attacks.txt goes as follows:

name=(name of attack)
animation_name=(name of animation that the attacker does)
can_hit=all/player/enemy (type of entity that can be attacked by this attack)
direction=SELF_FACING/TARGET_FACING/TARGET_RELATIVE_TO_SELF (how the direction of the attack is determined; see below*)
auto_rotate=true/false
attack_delay=(integer of number of beats after the attack starts that the damage calculations are done)
disable_movement=(integer of number of beats after the attack starts that the attacker is able to move)
'(any single character)'=(name of animation that is drawn on the tile with the specified character)
(The above line can be repeated any amount with different characters and animation names)
right=[
(String consisting of characters from the legend; see **)
]
(see ***)
left=[
]
up=[
]
down=[
]


*SELF_FACING: Direction determined is the direction the attacker is facing
 TARGET_FACING: Direction determined is the direction the target is facing
 TARGET_RELATIVE_TO_SELF: Direction determined depends on the target's position relative to the attacker

**The legend is the list of "'(any single character)'=" as defined in the same attack. Default legend keys are:
  '#' = the attacker (if direction is SELF_FACING) or the player (if direction is TARGET_FACING or TARGET_RELATIVE_TO_SELF);
	can be used in the custom legend to have an animationon the same tile as the focus
  '-' = empty space used solely for visual aid; never to be used in the custom legend

***If auto_rotate is set to true, only [right] is needed. The rest of the hitboxes' data are created by
   rotating [right] different ways