/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class LotharsEdge extends EquipmentCard {
		
	public LotharsEdge(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.callback = callback;
		Player damageSource = player.getGame().getDamageSource();
		this.isPassiveLaunchable = (!damageSource.getCharacter().isJuggernaut() &&
				player.getHandCardCountByColor(CardColor.CardColorBlack) >= getMinHandCardCount());
		return false;
	}
	
	@Override
	public void resolveSelect() {
//		Maybe damage source equipped EyeOfSkadi, self player need use 2 evasion. So need backup it.
		int preSelectableCardCount = player.getSelectableCardCount();
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.setSelectableCardCount(preSelectableCardCount);
		player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveUse() {
//		If played one evasion is not enough, will ask play evasion again.
		int usedEvasionCount = player.getUsedEvasionCount();
		if (usedEvasionCount < player.getSelectableCardCount()) {
			int selCount = player.getSelectableCardCount();
			player.setSelectableCardCount(selCount-usedEvasionCount);
			player.makeEvasionCardAvailable();
			player.getGamePlugin().sendChooseCardToUseMessage(player, true);
			player.setSelectableCardCount(selCount);
		} else {
			player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount()).resolveUse();
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setEquipmentCard(null);
		callback.resolveCancelByTarget(player);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorBlack);
	}
	
}
