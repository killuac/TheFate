/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class ViperRaid extends HandCard {
	
	private int handCardCount;
	private boolean isFirstResolveContinue = true;
	
	public ViperRaid(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		this.handCardCount = tarPlayer.getHandCardCount();
		
		if (handCardCount > 3) {
			tarPlayer.setRequiredSelCardCount(3);
			tarPlayer.setIsRequiredDrop(true);
			tarPlayer.makeAllHandCardsAvailable();
			player.getGamePlugin().sendChooseCardToDropMessage(tarPlayer, true);
		} else {
//			Drop card first, otherwise damage maybe make other hero skill insert. (e.g.Purification)
			if (handCardCount > 0) {
				tarPlayer.choseCardToDrop(tarPlayer.getHandCardIds());
			} else {
				this.resolveContinue();
			}
		}
	}
	
	@Override
	public void resolveContinue() {
		if (handCardCount <= 1 && isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			int damageValue = this.getDamageValue();
			player.getTargetPlayer().updateHeroHpSp(-damageValue, damageValue, this);
		} else {
			super.resolveContinue();
		}
	}
	
}
