/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Shrapnel extends BaseSkill {

	private HandCard disarm;
	
	public Shrapnel(Integer skillId, Player player) {
		super(skillId, player);
		disarm = HandCard.newCardWithCardId(getTransformedCardId(), player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && disarm.isActiveUsable());
		if (player.isNPC() && isAvailable) {
			return (player.getHandCardCountBySuits(CardSuits.CardSuitsHearts) >= getMinHandCardCount());
		} else {
			return isAvailable;
		}
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		disarm.selectTargetByNPC();
	}
	
	@Override
	public void resolveUse() {
		if (player.getSkillPoint() > 0) {
			disarm.resolveStrengthen(null);
		} else {
			player.resolveUseHandCard(disarm);
		}
	}
	
	@Override
	public void resolveResult() {
		this.disarm.resolveUse();
	}
	
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		if (this.disarm.isStrengthened()) {
			this.disarm.resolveResult(tarPlayer, cardIdxes, cardIds);
		} else {
			super.resolveResult(tarPlayer, cardIdxes, cardIds);
		}
	}
	
	@Override
	public void resolveOkay() {
		player.getCharacter().updateHeroHpSp(0, -disarm.getRequiredSp());
		disarm.setIsStrengthened(true);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		if (player.getGame().isDispelled()) {
			super.resolveCancel();
		} else {
			player.resolveUseHandCard(disarm);
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableBySuits(CardSuits.CardSuitsHearts);
	}
	
}
