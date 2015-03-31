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
public class VampiricAura extends BaseSkill {

	public VampiricAura(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
//	直接返回True，使用1张攻击造成伤害后，限制只能触发一次，防止对方装备了刃甲反弹，再次触发。
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.getHealthPoint() < player.getHpLimit()) {
			super.trigger(reason, callback);
		}
		return this.isTriggered = true;
	}
	
	@Override
	public void resolveOkay() {
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		return player.getJudgedCard().isRedColor();
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			player.setHeroSkill(null);
			player.updateHeroHpSp(1, 0, this);
		} else {
			this.resolveCancel();
		}
	}
	
	@Override
	public void resolveContinue() {
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.callback.resolveContinue();
	}

}
