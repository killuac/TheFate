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
public class Ordeal extends BaseSkill {
	
	public Ordeal(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isTriggered) {
			this.isTriggered = (callback.isJudgedYes() && 
					!callback.getPlayer().getRoleCard().isSameWithRole(player.getRoleCard()));
		}
		
		if (this.isTriggered) {
			this.callback = callback;
			player.setHeroSkill(this);
			player.makeAllHandCardsAvailable();
			player.setRequiredSelCardCount(getMinHandCardCount());
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveResult() {
		player.setHeroSkill(null);
		callback.getPlayer().revealOneCardFromDeck(callback);
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.callback.resolveJudge();
	}
	
}
