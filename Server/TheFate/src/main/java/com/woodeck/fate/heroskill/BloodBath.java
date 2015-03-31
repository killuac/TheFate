/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class BloodBath extends BaseSkill {

	public BloodBath(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getHealthPoint() < player.getHpLimit() &&
			!player.getGame().getDyingPlayer().equals(player)) {	// Dead player is not self
			return super.trigger(reason, callback);
		} else {
			return false;
		}
	}
	
	@Override
	public void resolveOkay() {
		player.updateHeroHpSp(1, 0, this);
	}
	
	@Override
	public void resolveContinue() {
		player.setHeroSkill(null);		// To avoid dead loop
		player.getGame().continueResolveDeath();
	}
	
	@Override
	public void resolveCancel() {
		this.resolveContinue();
	}
	
}
