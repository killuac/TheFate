/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class UnstableCurrent extends BaseSkill {

	public UnstableCurrent(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public void resolveOkay() {
		player.getCardsFromTable(player.getGame().getDamageSource().getSelectableCardCount(), true);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.finishResolve(callback);
	}

}
