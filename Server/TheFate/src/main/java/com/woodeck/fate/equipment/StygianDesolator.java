/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 * TriggerReason changed to TriggerReasonAttackDamage(7) because hero skill maybe interrupt SP resolve.
 * (e.g. ManaBreak)
 */
public class StygianDesolator extends EquipmentCard {

	public StygianDesolator(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getSkillPoint() < player.getSpLimit()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
			this.player.setDealDamagedSp(player.getDealDamagedSp() + 1);
		}
		return false;
	}
	
}
