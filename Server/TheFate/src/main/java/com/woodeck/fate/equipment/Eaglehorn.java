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
public class Eaglehorn extends EquipmentCard {

	public Eaglehorn(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void selectTargetByNPC() {
//		 Don't select any target player, only equip for self.
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getCharacter().isAgility()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
			player.setAttackLimit(player.getAttackLimit()+1);
		}
		return false;
	}
	
}
