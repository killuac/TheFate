/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class StrygwyrsThirst extends BaseSkill {

	public StrygwyrsThirst(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = true;	// Only trigger once
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (1 == tarPlayer.getHealthPoint()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			tarPlayer.setSelectableCardCount(tarPlayer.getSelectableCardCount()+1);
		}
		return false;
	}

}
