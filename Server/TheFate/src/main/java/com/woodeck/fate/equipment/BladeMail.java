/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.heroskill.BaseSkill;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class BladeMail extends EquipmentCard {

	private boolean isFirstResolveContinue;
	
	public BladeMail(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isFirstResolveContinue = true;
		this.isTriggered = !callback.getPlayer().equals(player);	// 伤害来源不是自己
		if (this.isTriggered) {
			player.setHeroSkill(null);		// Maybe trigger skill "Warpath"(战意) before, need clear.
			super.trigger(reason, callback);
		}
		return this.isTriggered;
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
			player.setEquipmentCard(null);
			player.getGame().setDamageSource(player);
			int damageValue = this.getDamageValue();
			callback.getPlayer().updateHeroHpSp(-damageValue, damageValue, this);
		} else {
			this.resolveCancel();
		}
	}
	
	public void resolveCancel() {
		player.setEquipmentCard(null);
		this.resolveContinue();
	}
	
	@Override
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			player.getGame().setDamageSource(callback.getPlayer());
			if (this.callback.isSkill()) {	// Maybe cleared by above trigger, need restore.
				callback.getPlayer().setHeroSkill(((BaseSkill) callback));
			}
			player.finishResolve(this.callback);
		} else {
			super.resolveCancel();
		}
	}
	
}
