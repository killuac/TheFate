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
public class BurningSpear extends BaseSkill {

	public BurningSpear(Integer skillId, Player player) {
		super(skillId, player);
		
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		return (player.getHealthPoint() <= 2 && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		player.setAttackDamageExtra(1);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		callback.resolveCancelByTarget(player.getNotResolvedTargetPlayer());
	}
	
}
