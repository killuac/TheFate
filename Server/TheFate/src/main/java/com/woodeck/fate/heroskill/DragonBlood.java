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
public class DragonBlood extends BaseSkill {

	public DragonBlood(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
//	Triggered while using super skill card "TimeLock"
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (player.getHandCardCount() >= getMinHandCardCount() &&
							player.getHealthPoint() < player.getHpLimit());
		if (player.isNPC() && isTriggered) {
			this.isTriggered = (player.getHandCardCount() == getMinHandCardCount());
		}
		return (isTriggered && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		player.choseCardToDrop(player.getHandCardIds());
	}
	
	@Override
	public void resolveResult() {
		player.updateHeroHpSp(1, 0, this);
	}
	
	@Override
	public void resolveContinue() {
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		if (null != callback) {
			this.callback.resolveContinue();
		} else {
			player.getGame().turnToNextPlayer();
		}
	}
	
}
