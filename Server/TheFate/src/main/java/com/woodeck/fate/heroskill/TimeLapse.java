/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class TimeLapse extends BaseSkill {

	public TimeLapse(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			if (!player.getAvailableCardIdList().isEmpty()) return false;
			return (super.isActiveLaunchable() && player.getHandCardCountByColor(CardColor.CardColorRed) >= 3);
		} else {
			return super.isActiveLaunchable();
		}
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveResult() {
		int count = player.getHandSizeLimit() - player.getHandCardCount();
		if (count > 0) {
			player.drawCard(count);
			player.getGame().turnToNextPlayer();
		} else {
			if (player.getHandCardCount() > player.getHandSizeLimit()) {
				player.discardCard();
			} else {
				player.getGame().turnToNextPlayer();
			}
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorRed);
	}
	
}
