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
public class BreatheFire extends BaseSkill {
	
	public BreatheFire(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC() && super.isActiveLaunchable()) {
			return (player.getSameSuitsHandCards().size() > getMinHandCardCount());
		} else {
			return super.isActiveLaunchable();
		}
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null != tarPlayer) {
			int damage = this.getDamageValue();
			tarPlayer.updateHeroHpSp(-damage, damage, this);
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveContinue() {
		this.resolveResult();
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
