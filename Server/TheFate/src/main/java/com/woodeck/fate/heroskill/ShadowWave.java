/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class ShadowWave extends BaseSkill {

	public ShadowWave(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC() && super.isActiveLaunchable()) {
			for (Player tarPlayer : player.getGame().getPartnerPlayers()) {
				if (tarPlayer.getHealthPoint() < tarPlayer.getHpLimit()) {
					return (player.getSameSuitsHandCards().size() > getMinHandCardCount());
				}
			}
			return false;
		}
		return super.isActiveLaunchable();
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMinHpTargetPlayer(true);		// 给自己阵营的玩家加血
		player.selectMinHpTargetPlayer(false);		// 对地方阵营造成伤害
	}
	
	@Override
	public void resolveResult() {
		this.isFirstResolveContinue = true;
		Player playerA = player.getTargetPlayers().peekFirst();
		playerA.updateHeroHpSp(1, 0, this);
	}
	
	@Override
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			Player playerB = player.getTargetPlayers().peekLast();
			int damage = this.getDamageValue();
			playerB.updateHeroHpSp(-damage, damage, this);	// Maybe trigger "ManaShield"
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		if (player.isNPC()) {
			player.makeAllHandCardsUnavailable();
			for (HandCard card : player.getHandCards()) {
				if (player.getSameSuitsHandCards().contains(card)) {
					card.setIsAvailable(true);
				}
			}
		} else {
			player.makeAllHandCardsAvailable();
		}
	}

}
