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
public class BattleTrance extends BaseSkill {

	public BattleTrance(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (this.usedTimes < 1) {
			this.usedTimes++;	// Only ask once for each turn
			return super.trigger(reason, callback);
		}
		return false;
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
		if (this.isJudgedYes()) player.setIsFreeAttack(true);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.playCard();
	}

}
