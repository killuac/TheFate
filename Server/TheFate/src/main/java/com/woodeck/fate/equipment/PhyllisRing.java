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
public class PhyllisRing extends EquipmentCard {
	
	public PhyllisRing(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
		player.setDrawCardCount(player.getDrawCardCount()+1);
		return false;
	}
	
//	@Override
//	public void resolveOkay() {
//		player.setDrawCardCount(player.getDrawCardCount()+1);
//		player.startDrawCardAndPlay();
//	}
//	
//	@Override
//	public void resolveCancel() {
//		player.startDrawCardAndPlay();
//	}
	
}
