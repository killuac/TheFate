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
public class Reincarnation extends BaseSkill {

	public Reincarnation(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = isDyingTriggered = !this.isAlreadyUsed;
		if (this.isTriggered) super.trigger(reason, callback);
		return this.isTriggered;
	}
	
	@Override
	public void resolveOkay() {
		this.isAlreadyUsed = true;
		player.choseCardToDrop(player.getHandCardIds());
	}
	
	@Override
	public void resolveUse() {
		player.getCharacter().setHeroHpSp(player.getHpLimit(), 0);
		player.continueResolveDamage(callback);
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.resolvePlayerDying(callback);
	}

}
