/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class ManaBreak extends BaseSkill {

	public ManaBreak(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (!player.getTargetPlayer().isDead() && player.getTargetPlayer().getHandCardCount() > 0);
		if (isTriggered) super.trigger(reason, callback);
		return isTriggered;
	}
	
	@Override
	public void resolveOkay() {
//		The previous target player has been resolved when trigger this skill.
//		So can't get target player by calling getSortedTargetPlayer method
		Player preTargetPlayer = ((HandCard) callback).getPreTargetPlayer();
		player.setRequiredSelCardCount(1);
		player.getGamePlugin().sendShowPlayerHandCardMessage(player, preTargetPlayer);
		player.getGamePlugin().sendChooseCardToRemoveMessage(player);
	}
	
	@Override
	public void resolveCancel() {
		this.callback.resolveContinue();
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		this.resolveCancel();
	}
	
}
