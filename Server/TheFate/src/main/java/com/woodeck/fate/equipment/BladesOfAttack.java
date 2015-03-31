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
public class BladesOfAttack extends EquipmentCard {
	
	private HandCard normalAttack;
	
	public BladesOfAttack(Integer cardId, Player player) {
		super(cardId, player);
		normalAttack = HandCard.newCardWithCardId(getTransformedCardId(), player);
	}

	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && normalAttack.isActiveUsable());		
		if (player.isNPC()) {
			return (isAvailable && player.getHandCardCountByColor(CardColor.CardColorRed) >= getMinHandCardCount());
		} else {
			return isAvailable;
		}
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
//		Need target only when launch the equipment, not equip.
		if (null != player.getEquipmentCard()) {
			normalAttack.selectTargetByNPC();
		}
	}
	
	@Override
	public void resolveUse() {
		normalAttack.resolveUse();
	}
	
	@Override
	public void resolveContinue() {
		normalAttack.resolveContinue();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		normalAttack.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void resolveCancel() {
		if (null != callback) {		// BattleHunger
			callback.resolveCancelByTarget(player);
		} else {
			super.resolveCancel();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorRed);
	}
	
}
