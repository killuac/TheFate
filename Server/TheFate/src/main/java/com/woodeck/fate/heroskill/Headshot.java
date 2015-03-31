/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.Constant;

/**
 * @author Killua
 *
 */
public class Headshot extends BaseSkill implements ScheduledCallback {

	public Headshot(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public void resolveOkay() {
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		return player.getJudgedCard().isBlackColor();
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			player.setAttackDamageExtra(1);
		}
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		player.getGamePlugin().getApi().scheduleExecution(Constant.DEFAULT_DELAY_SHORT_TIME, 1, this);
	}
	
	@Override
	public void resolveContinue() {
		this.callback.resolveContinue();
	}
	
	@Override
	public void scheduledCallback() {
		this.callback.resolveResult();
	}
	
}
