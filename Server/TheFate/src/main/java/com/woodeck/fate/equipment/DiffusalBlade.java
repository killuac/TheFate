/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class DiffusalBlade extends EquipmentCard {
		
	public DiffusalBlade(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
//		Can be triggered multiple times, so don't set isTriggered.
		this.isPassiveLaunchable = (player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isPassiveLaunchable) {
			this.isPassiveLaunchable = (!player.hasDispel() && player.getHandCardCount() > player.getHandSizeLimit());
		}
		return this.isPassiveLaunchable;
	}
	
	@Override
	public void resolveSelect() {
//		Maybe damage source equipped EyeOfSkadi, self player need use 2 evasion. So need backup it.
		int preSelectableCardCount = player.getSelectableCardCount();
		
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
		player.setSelectableCardCount(preSelectableCardCount);
	}
	
	@Override
	public void resolveUse() {
		HandCard card = player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount());
		card.resolveUse();
	}
	
	@Override
	public void resolveCancel() {
		player.cancelDispel();
		player.setEquipmentCard(null);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
