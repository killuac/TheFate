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
public class HealingSpell extends BaseSkill {
	
	public HealingSpell(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isTriggered) {
			this.isTriggered = (player.getGame().getPartnerPlayers().size() > 1 &&
					player.getHandCardCountByColor(CardColor.CardColorRed) >= getMinHandCardCount());
			if (this.isTriggered) this.selectTargetByNPC();
		}
		
		if (this.isTriggered) {
			this.callback = callback;
			player.setHeroSkill(this);
			player.setIsRequiredTarget(true);
			player.setRequiredSelCardCount(1);
			player.setRequiredTargetCount(1);
			player.makeHandCardAavailableByColor(CardColor.CardColorRed);
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void selectTargetByNPC() {
		Player tarPlayer = player.selectMinHpTargetPlayer(true);
		
		if (tarPlayer.equals(player)) {
			player.getTargetPlayerNames().clear();
			for (Player p : player.getGame().getPartnerPlayers()) {
				if (!p.equals(player)) {
					player.getTargetPlayerNames().add(p.getPlayerName());
				}
			}
		}
	}
	
	@Override
	public void resolveResult() {
		player.setHeroSkill(null);
		player.getTargetPlayer().updateHeroHpSp(1, 0, this);
	}
	
	@Override
	public void resolveContinue() {
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.finishResolve(callback);
	}
	
}
