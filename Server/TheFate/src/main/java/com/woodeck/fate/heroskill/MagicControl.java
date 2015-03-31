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
public class MagicControl extends BaseSkill {

	public MagicControl(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = true;
		if (callback.isCard()) {
			Player lastDispelPlayer = player.getGame().getDispelPlayers().pollLast();
			player.getGame().getDispelPlayers().addFirst(lastDispelPlayer);	// Keep it for continue resolve
			Player preDispelPlayer = player.getGame().getDispelPlayers().peekLast();
			if (null == preDispelPlayer) preDispelPlayer = player.getGame().getDamageSource();
			
			player.getPreviousCardsFromTable(preDispelPlayer.getSelectableCardCount());
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
		}
		return false;
	}

}
