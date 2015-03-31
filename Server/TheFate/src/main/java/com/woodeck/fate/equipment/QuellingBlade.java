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
public class QuellingBlade extends EquipmentCard {
		
	public QuellingBlade(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (!callback.getPlayer().equals(player));	// The killer is self, don't need trigger.
		if (isTriggered) {
			player.setEquipmentCard(this);
			player.makeAttackCardAvailable();
			player.getGamePlugin().sendChooseCardToUseMessage(player, true);
		}
		return isTriggered;
	}
	
	@Override
//	Check if trigger hero skill "Frostbite"
	public void resolveUse() {
		if (!player.getGame().checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyAttack, this)) {
			this.resolveContinue();
		}
	}
	
	@Override
	public void resolveContinue() {
		player.getGame().setDamageSource(player);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setEquipmentCard(null);
		player.getGame().continueResolveDeath();
	}
	
}
