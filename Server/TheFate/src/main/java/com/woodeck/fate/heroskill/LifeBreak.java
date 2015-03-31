/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class LifeBreak extends BaseSkill {

	public LifeBreak(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (p.getHandCardCount() > 0) {
					return (super.isActiveLaunchable() && player.getHealthPoint() > 2);
				}
			}
			return false;
		}
		return super.isActiveLaunchable();
	}
	
	@Override
	public void resolveSelect() {
		player.getGamePlugin().sendChooseTargetPlayerMessage(player, false);
	}
	
	@Override
	public void resolveSelectByNPC() {
		this.selectTargetByNPC();
		this.resolveUse();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectHandCardTargetPlayer();
	}
	
	@Override
	public void resolveUse() {
		usedTimes++;
		int damage = this.getDamageValue();
		player.updateHeroHpSp(-damage, damage, this);
//		Ask for dispel in super method resolveContinue()
	}
	
	@Override
	public void resolveResult() {
		player.setSelectableCardCount(2);
		player.getGamePlugin().sendShowPlayerHandCardMessage(player, player.getTargetPlayer());
		player.getGamePlugin().sendChooseCardToRemoveMessage(player);
	}
	
}
