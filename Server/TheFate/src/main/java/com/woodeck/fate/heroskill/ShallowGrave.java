/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class ShallowGrave extends BaseSkill {
	
	public ShallowGrave(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = isDyingTriggered = (player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isTriggered) {
			this.isTriggered = (callback.getPlayer().getRoleCard().isSameWithRole(player.getRoleCard()) &&
								player.getHandCardCountByColor(CardColor.CardColorRed) >= getMinHandCardCount());
		}
		
		if (this.isTriggered) {
			this.callback = callback;
			player.setHeroSkill(this);
			player.setRequiredSelCardCount(getMinHandCardCount());
			player.makeHandCardAavailableByColor(CardColor.CardColorRed);
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveResult() {
		player.setHeroSkill(null);
		
		Player dyingPlayer = player.getGame().getDyingPlayer();
		int hpChanged = Math.abs(dyingPlayer.getHealthPoint())+1;	// 血量回复至1点
		player.getGame().getDyingPlayer().updateHeroHpSp(hpChanged, 0, this);
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.resolveContinue();
	}
	
	@Override
	public void resolveContinue() {
		if (player.getGame().somebodyIsDying()) {
			player.getGame().askForHelpFromPlayer(player.getGame().getTurnOwner(), callback);
		} else {
			player.getGame().getDyingPlayer().continueResolveDamage(callback);
		}
	}
	
}
