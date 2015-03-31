/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class HealingSalve extends HandCard {

	public HealingSalve(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean isActiveUsable() {
		return (player.getHealthPoint() < player.getCharacter().getHpLimit());
	}
	
	@Override
	public boolean isPassiveUsable() {
		return (player.getGame().somebodyIsDying() && !player.getGame().isWaitingDispel());
	}
	
	@Override
	public void resolveUse() {
		int hpChanged = player.getUsedSalveCount();
		if (player.getGame().somebodyIsDying()) {
			player.setIsMadeChoice(true);	// Use a HealingSalve, but not enough, don't ask again.
			player.getGame().getDyingPlayer().updateHeroHpSp(hpChanged, 0, this);
		} else {
			player.updateHeroHpSp(1, 0, this);
		}
	}
	
	@Override
	public void resolveContinue() {
		if (null != player.getGame().getDyingPlayer()) {
			player.makeChoice();
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
}
