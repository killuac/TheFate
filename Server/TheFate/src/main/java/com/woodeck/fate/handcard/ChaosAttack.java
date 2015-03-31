/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class ChaosAttack extends NormalAttack {
	
	public ChaosAttack(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		if (player.getSkillPoint() < player.getSpLimit()) {
			this.player.setDealDamagedSp(1);
		}
		super.resolveCancelByTarget(tarPlayer);
	}
	
}
