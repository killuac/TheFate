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
public class Evasion extends HandCard {

	public Evasion(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean isActiveUsable() {
		return false;
	}
	
	@Override
	public void resolveUse() {
		if (!player.getGame().checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyEvasion, this))
			this.resolveResult();
	}
	
	@Override
	public void resolveResult() {
		int usedEvasionCount = player.getUsedEvasionCount();
		if (usedEvasionCount < player.getSelectableCardCount()) {	// Used evasion is not enough
			int selCount = player.getSelectableCardCount();
			player.setSelectableCardCount(selCount-usedEvasionCount);
			player.getGamePlugin().sendChooseCardToUseMessage(player, true);
			player.setSelectableCardCount(selCount);
			return;
		}
		
		Player damageSource = player.getGame().getDamageSource();
		if (null != damageSource.getHeroSkill()) {
			player.finishResolve(damageSource.getHeroSkill());
		} else if (null != damageSource.getEquipmentCard()) {
			player.finishResolve(damageSource.getEquipmentCard());
		} else {
			if (player.isDamageSource()) {	// Frostbite: dead loop
				player.getGame().backToTurnOwner();
			} else {
//				player.finishResolve(damageSource.getLastUsedCard());	// Maybe dispel insert
				player.finishResolve(damageSource.getFirstUsedCard());
			}
		}
	}
	
	@Override
	public void resolveContinue() {
		this.resolveResult();
	}
	
}
