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
public class SpecialBody extends BaseSkill {

	public SpecialBody(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getHealthPoint() <= 2) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			player.finishResolve(callback);
			return true;
		}
		return false;
	}

}
