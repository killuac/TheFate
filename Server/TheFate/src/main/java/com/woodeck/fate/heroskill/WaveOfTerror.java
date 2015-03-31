/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class WaveOfTerror extends BaseSkill {
	
	public WaveOfTerror(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public void resolveOkay() {
		int sp = player.getBeDamagedSp() + player.getDealDamagedSp();
		player.drawCard(sp);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		
		Player damagedPlayer = player.getGame().getDamagedPlayer();
		if (null != damagedPlayer && !damagedPlayer.isDead()) {
			player.continueResolveDamage(callback);
		} else {
			player.finishResolve(callback);
		}
	}
	
}
