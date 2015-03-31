/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class FanaticismHeart extends BaseSkill {

	public FanaticismHeart(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (((HandCard) callback).isMagicCard() && usedTimes < 1);
		if (this.isTriggered) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			this.usedTimes++;
			player.setAttackLimit(player.getAttackLimit()+1);
			player.getGame().backToTurnOwner();
		}
		return this.isTriggered;
	}

}
