/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class DoubleEdge extends BaseSkill {
	
	public DoubleEdge(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			return (super.isActiveLaunchable() && player.getHealthPoint() > 2);
		}
		return super.isActiveLaunchable();
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		usedTimes++;
		int damage = this.getDamageValue();
		player.updateHeroHpSp(-damage, damage, this);
	}
	
	@Override
//	Maybe interrupted by BladeMail, need back to continue resolve
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;			
			int damage = this.getDamageValue();
			player.getTargetPlayer().updateHeroHpSp(-damage, damage, this);	// Maybe trigger "ManaShield"
		} else {
			this.isFirstResolveContinue = true;
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
