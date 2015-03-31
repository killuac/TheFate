/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class SangeAndYasha extends EquipmentCard {
	
	private HandCard normalAttack;
	
	public SangeAndYasha(Integer cardId, Player player) {
		super(cardId, player);
		normalAttack = HandCard.newCardWithCardId(getTransformedCardId(), player);	// Not transform color
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && normalAttack.isActiveUsable());
		if (player.isNPC() && isAvailable) {
			return (!player.hasAttack() && player.getHandCardCount() > player.getHandSizeLimit());
		}
		return isAvailable;
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		if (player.isTurnOwner()) {
			player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
		} else {
			player.setRequiredTargetCount(0);
			player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
		}
	}
	
	@Override
	public void selectTargetByNPC() {
		if (null != player.getEquipmentCard()) {
			normalAttack.selectTargetByNPC();
		}
	}
	
	@Override
	public void resolveUse() {
//		Also transform color
		normalAttack = player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount());
		normalAttack.resolveUse();
	}
	
	@Override
	public void resolveContinue() {
		normalAttack.resolveContinue();
	}
	
//	If two used cards are both attack, can not evade
	public boolean isEvadable() {
		for (HandCard card : player.getUsedCards()) {
			if (!card.isAttack())
				return true;
		}
		return false;
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		normalAttack.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void resolveCancel() {
		if (null != callback) {			// BattleHunger
			callback.resolveCancelByTarget(player);
		} else {
			super.resolveCancel();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
