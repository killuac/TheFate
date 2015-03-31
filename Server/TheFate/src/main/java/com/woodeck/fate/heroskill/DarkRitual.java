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
public class DarkRitual extends BaseSkill {

	public DarkRitual(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC() && super.isActiveLaunchable()) {
			return (player.getHandCardCountByColor(CardColor.CardColorRed) > 0);
		} else {
			return super.isActiveLaunchable();
		}
	}
	
	@Override
	public void resolveSelect() {		
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getEquipment().makeCardDroppableWithColor(CardColor.CardColorRed);
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveResult() {
		player.drawCard(2);
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorRed);
	}

}
