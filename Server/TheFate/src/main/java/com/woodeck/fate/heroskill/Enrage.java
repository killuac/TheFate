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
public class Enrage extends BaseSkill {

	public Enrage(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		for (Player p : player.getGame().getAlivePlayers()) {
			if (!p.equals(player) && p.getHealthPoint() >= player.getHealthPoint())
				return false;
		}
		return super.trigger(reason, callback);
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
