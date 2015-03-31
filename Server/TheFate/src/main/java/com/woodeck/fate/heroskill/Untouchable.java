/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Untouchable extends BaseSkill {

	public Untouchable(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (((HandCard) callback).isNormalAttack()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			player.finishResolve(callback);
			return true;
		}
		return false;
	}
	
}
