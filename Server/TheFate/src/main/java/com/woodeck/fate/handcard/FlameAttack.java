/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class FlameAttack extends NormalAttack {
	
	public FlameAttack(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		tarPlayer.setBeDamagedSp(1);
		super.resolveCancelByTarget(preTargetPlayer);
	}
}
