/**
 * 
 */
package com.woodeck.fate.handcard;

import java.util.Deque;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class EnergyTransport extends HandCard {

	public EnergyTransport(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {
		return (super.isActiveUsable() && !player.isNPC());
	}
	
	@Override
	public void resolveUse() {
		player.showCardAndChooseToAssign();
	}
	
	@Override
	public void resolveResult(Deque<Integer> cardIds) {
		player.assignCardToEachPlayer(cardIds);
		if (this.isStrengthened) player.drawCard(1);
		player.getGame().backToTurnOwner(this);
	}
}
