/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Bristleback extends BaseSkill {

	public Bristleback(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public void resolveOkay() {
		this.isTriggered = true;
		int damage = player.getDamagedValue() - 1;
		player.updateHeroHpSp(-damage, damage, callback);
	}
	
	@Override
	public void resolveCancel() {
		int damage = player.getDamagedValue();
		player.updateHeroHpSp(-damage, damage, callback);
	}
	
}
