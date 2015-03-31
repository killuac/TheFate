/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class FrostArmor extends BaseSkill {

	public FrostArmor(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (!callback.getPlayer().equals(player) &&
							(callback.getPlayer().getHandCardCount() > 0 ||
							callback.getPlayer().getEquipmentCount() > 0));
		if (this.isTriggered) super.trigger(reason, callback);
		return this.isTriggered;
	}
	
	@Override
	public void resolveOkay() {
		player.getGame().setDamageSource(player);
		player.getTargetPlayerNames().add(callback.getPlayer().getPlayerName());
		
		player.setRequiredSelCardCount(1);
		player.getGamePlugin().sendShowPlayerAllCardsMessage(player, callback.getPlayer());
		player.getGamePlugin().sendChooseCardToRemoveMessage(player);
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.getGame().setDamageSource(callback.getPlayer());		// Back to turn owner
		player.finishResolve(callback);
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		this.resolveCancel();
	}

}
