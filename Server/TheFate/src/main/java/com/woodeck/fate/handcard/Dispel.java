/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Dispel extends HandCard {

	public Dispel(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean isActiveUsable() {
		return false;
	}
	
	@Override
	public boolean isPassiveUsable() {
		return (player.getGame().isWaitingDispel());
	}
	
	@Override
	public void resolveUse() {
		player.getGame().getDispelPlayers().add(player);
		
		if (player.checkHeroSkill(TriggerReason.TriggerReasonCastMagic, this)) {
			return;
		}
		
		if (player.getGame().isWaitingDispel()) {
			player.getGame().resetCancelDispel();	// Reset dispel answer(Not cancel)
		}
		player.getGame().askForDispel(null);
	}
	
}
