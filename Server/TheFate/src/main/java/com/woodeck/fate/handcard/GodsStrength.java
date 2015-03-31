/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class GodsStrength extends HandCard {

	public GodsStrength(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public void resolveUse() {
		player.drawCard(1);
		player.setAttackDamage(player.getAttackDamage()+1);
		player.getGame().backToTurnOwner();
	}

}
