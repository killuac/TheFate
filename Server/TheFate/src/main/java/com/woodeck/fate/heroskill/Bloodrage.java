/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Bloodrage extends BaseSkill {

	public Bloodrage(Integer skillId, Player player) {
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
		usedTimes++;
		int damage = this.getDamageValue();
		player.updateHeroHpSp(-damage, damage, this);
	}
	
	@Override
	public void resolveResult() {
		player.drawCard(1);
		player.setAttackDamage(player.getAttackDamage()+1);
		player.getGame().backToTurnOwner();
	}
	
}
