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
public class ManaBurn extends BaseSkill {

	public ManaBurn(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && (player.getHandCardCount()+player.getEquipmentCount() > 0));
		if (player.isNPC()) {
			if (player.getHandCardCountByColor(CardColor.CardColorBlack) == 0) return false;
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (p.getHandCardCount() > 0) {
					return isAvailable;
				}
			}
			return false;
		}
		return isAvailable;
	}
	
	@Override
	public void resolveSelect() {
		player.getEquipment().makeCardDroppableWithColor(CardColor.CardColorBlack);
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectHandCardTargetPlayer();
	}
	
	@Override
	public void resolveResult() {
		player.getGamePlugin().sendShowPlayerHandCardMessage(player, player.getTargetPlayer());
		player.getGamePlugin().sendChooseCardToRemoveMessage(player);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorBlack);
	}
	
}
