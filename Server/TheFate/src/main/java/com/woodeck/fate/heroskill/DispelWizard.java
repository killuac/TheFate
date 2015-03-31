/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class DispelWizard extends BaseSkill {

	public DispelWizard(Integer skillId, Player player) {
		super(skillId, player);
	}
	
//	Handle the trigger logic in method game.askForDispel()
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isPassiveLaunchable = (player.getHandCardCount() >= getMinHandCardCount());
		return this.isPassiveLaunchable;
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveSelectByNPC() {
		if (player.getHandCardCountBySuits(CardSuits.CardSuitsHearts) >= getMinHandCardCount()) {
			super.resolveSelectByNPC();
		} else {
			this.resolveCancel();
		}
	}
	
	@Override
	public void resolveUse() {
		HandCard card = player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount());
		card.resolveUse();
	}
	
	@Override
	public void resolveCancel() {
		player.cancelDispel();
		player.setHeroSkill(null);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableBySuits(CardSuits.CardSuitsHearts);
	}
	
}
