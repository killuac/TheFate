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
public class BladeDance extends BaseSkill {

	public BladeDance(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.makeTargetPlayerHandCardAvailable(player.getNotResolvedTargetPlayer());
		player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
		return false;
	}
	
	public void makeTargetPlayerHandCardAvailable(Player tarPlayer) {
		tarPlayer.makeAllHandCardsUnavailable();
		
		for (HandCard card : tarPlayer.getHandCards()) {
			card.setIsAvailable(card.isEvasion() && card.isDiamonds());
		}
	}
	
}
