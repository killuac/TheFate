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
public class FurySwipes extends BaseSkill {

	public FurySwipes(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getSkillPoint() > 0) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			player.setAttackLimit(player.getAttackLimit()+player.getSkillPoint());
		}
		return false;
	}

}
