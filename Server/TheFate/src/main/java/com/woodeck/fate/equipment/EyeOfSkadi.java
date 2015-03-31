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
public class EyeOfSkadi extends EquipmentCard {

	public EyeOfSkadi(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = true;	// Only trigger once
		player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
		
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		tarPlayer.setSelectableCardCount(tarPlayer.getSelectableCardCount()+1);
		return false;
	}

}
