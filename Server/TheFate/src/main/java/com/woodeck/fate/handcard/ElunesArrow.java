/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class ElunesArrow extends HandCard {
	
	public ElunesArrow(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void resolveUse() {
		if (this.isStrengthened) {
			player.getGamePlugin().sendChooseSuitsMessage(player);
		} else {
			player.getGamePlugin().sendChooseColorMessage(player);
		}
	}

	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		int damageValue = this.getDamageValue();
		tarPlayer.updateHeroHpSp(-damageValue, damageValue, this);
	}
	
	@Override
	public void resolveResult() {
		Player turnOwner = player.getGame().getTurnOwner();
		Player tarPlayer = player.getTargetPlayer();
		
		if (tarPlayer.getHandCardCount() == 0) {
			this.resolveCancelByTarget(tarPlayer); return;
		}
		
		if (this.isStrengthened) {
			tarPlayer.makeHandCardAavailableBySuits(turnOwner.getSelectedSuits());
		} else {
			tarPlayer.makeHandCardAavailableByColor(turnOwner.getSelectedColor());
		}
		
		player.getGamePlugin().sendChooseCardToDropMessage(tarPlayer, true);
	}
	
}
