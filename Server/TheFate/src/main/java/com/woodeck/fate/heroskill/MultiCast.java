/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.util.Constant;

/**
 * @author Killua
 *
 */
public class MultiCast extends BaseSkill implements ScheduledCallback {

	private HandCard magicCard;
	private boolean isJudged;
	
	public MultiCast(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		magicCard = ((HandCard) callback);
		this.isJudged = false;
		this.isTriggered = magicCard.isMagicCard();
		return (this.isTriggered && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		isJudged = true;
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		return (player.getJudgedCard().isRedColor());
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			if (magicCard.isTargetable()) {
				player.setRequiredTargetCount(magicCard.getTargetCount());
				player.getGamePlugin().sendChooseTargetPlayerMessage(player, true);	// Ask change target
			} else {
				player.getGamePlugin().getApi().scheduleExecution(Constant.DEFAULT_DELAY_SHORT_TIME, 1, this);
			}
		} else {
			this.resolveCancel();
		}
	}
	
	@Override
	public void resolveUse() {
		player.setHeroSkill(null);
//		多重施法产生第2次效果时，可能不满足条件，无法发动
		if (this.magicCard.checkTargetPlayerAvailable()) {
			player.resetTargetPlayerResolveFlag();
			this.magicCard.resolveUse();
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveCancel() {
		if (this.isJudged && this.isJudgedYes()) {		// No change target
			player.resetTargetPlayerResolveFlag();
			this.resolveUse();
		} else {
			player.getGame().backToTurnOwner();
		}
	}

	@Override
	public void scheduledCallback() {
		this.resolveUse();
	}
	
}
