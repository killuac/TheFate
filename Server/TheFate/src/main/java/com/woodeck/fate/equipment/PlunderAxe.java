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
public class PlunderAxe extends EquipmentCard {

	public PlunderAxe(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void selectTargetByNPC() {
//		 Don't select any target player, only equip for self.
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
//		return (player.getCharacter().isStrength() && super.trigger(reason, callback));
		
		if (player.getCharacter().isStrength()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
			player.drawCard(1);
		}
		return false;
	}
	
//	@Override
//	public void resolveOkay() {
//		player.drawCard(1);
//		this.resolveCancel();
//	}
//	
//	@Override
//	public void resolveCancel() {
//		player.setEquipmentCard(null);
//		this.callback.resolveCancelByTarget(player.getNotResolvedTargetPlayer());
//	}

}
