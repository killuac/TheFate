/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.game.Game.GameState;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Omnislash extends BaseSkill {

	private HandCard normalAttack;
	private boolean isLaunched;
	
	public Omnislash(Integer skillId, Player player) {
		super(skillId, player);
		normalAttack = HandCard.newCardWithCardId(getTransformedCardId(), player);
	}
	
	@Override
	public String getHistoryText() {
		return (GameState.GameStateBoolChoosing == player.getGame().getState()) ? historyText : historyText2;
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (this.isLaunched && player.getHandCardCount() > 0 && normalAttack.isActiveUsable());
		if (player.isNPC() && isAvailable) {
			return (player.getHandCardCountByColor(CardColor.CardColorBlack) >= getMinHandCardCount());
		} else {
			return isAvailable;
		}
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		if (player.isNPC()) {
			int count = player.getHandCardCountByColor(CardColor.CardColorBlack);
			return (count >= player.getHandSizeLimit() && super.trigger(reason, callback));
		} else {
			return super.trigger(reason, callback);
		}
	}
	
	@Override
	public void resolveSelect() {
		player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		normalAttack.selectTargetByNPC();
	}
	
	@Override
	public void resolveUse() {
		normalAttack.resolveUse();
	}
	
	@Override
	public void resolveContinue() {
		normalAttack.resolveContinue();
	}
	
	@Override
	public void resolveOkay() {
		player.getGame().askForDispel(this);
	}
	
	@Override
	public void resolveCancel() {
		this.isLaunched = false;
		player.setHeroSkill(null);
		
		if (player.getGame().isDispelled()) {
			player.playCard();
		} else {
			player.startDrawCardAndPlay();
		}
	}
	
	@Override
	public void resolveResult() {
		this.isLaunched = true;
		player.setIsFreeAttack(true);
		player.setHeroSkill(null);
		player.playCard();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		normalAttack.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorBlack);
	}

}
