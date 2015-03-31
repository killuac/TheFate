/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Fanaticism extends HandCard {

	public Fanaticism(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {
		if (player.isNPC()) {
			return (super.isActiveUsable() && player.getHealthPoint() > 2 &&
					player.hasAttack() && player.getHandCardCount() > 2);
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void resolveUse() {
		player.setIsFreeAttack(true);
		int damageValue = this.getDamageValue();
		player.updateHeroHpSp(-damageValue, damageValue, this);
	}

}
