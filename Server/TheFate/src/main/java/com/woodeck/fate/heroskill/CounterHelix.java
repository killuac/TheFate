/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class CounterHelix extends BaseSkill {

	public CounterHelix(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public void resolveOkay() {
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		PlayingCard card = player.getJudgedCard();
		return (card.isRedColor() && card.getCardFigure() >= 2 && card.getCardFigure() <= 7);
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			player.setHeroSkill(null);
			player.getGame().setDamageSource(player);	// Self is damage source
			int damage = this.getDamageValue();
			callback.getPlayer().updateHeroHpSp(-damage, damage, this);
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
		player.getGame().setDamageSource(callback.getPlayer());
		
//		Resolve not finished. e.g.ButtleHunger's target equipped BladeMail.
		player.setHeroSkill(null);
		player.setIsResolved(false);
		this.callback.resolveContinue();
	}
	
}
