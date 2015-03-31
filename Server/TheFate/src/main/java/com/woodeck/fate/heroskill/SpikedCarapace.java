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
public class SpikedCarapace extends BaseSkill {

	private boolean isFirstResolveContinue;
	
	public SpikedCarapace(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isFirstResolveContinue = true;
		return (!callback.getPlayer().equals(player) && 0 == player.getHandCardCount() && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		player.setHeroSkill(null);
		player.getGame().setDamageSource(player);
		int damageValue = this.getDamageValue();
		callback.getPlayer().updateHeroHpSp(-damageValue, damageValue, this);
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.resolveContinue();
	}
	
	@Override
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			player.getGame().setDamageSource(callback.getPlayer());
//			Maybe equipped "BladeMail", so need continue resolve damage
			player.continueResolveDamage(callback);
		} else {
			super.resolveCancel();
		}
	}
	
}
