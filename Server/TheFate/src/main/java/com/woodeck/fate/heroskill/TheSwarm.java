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
public class TheSwarm extends BaseSkill {

	public TheSwarm(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && player.getGame().getAlivePlayerCount() > 2 &&
				(player.getHandCardCount()+player.getEquipmentCount() > 1));
		if (player.isNPC() && isAvailable) {
			return (player.getHandCardCountByColor(CardColor.CardColorBlack) >= 2);
		} else {
			return isAvailable;
		}
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(2);
		player.getEquipment().makeCardDroppableWithColor(CardColor.CardColorBlack);
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
			if (player.getTargetPlayerNames().size() <= 2) {
				player.getTargetPlayerNames().add(tarPlayer.getPlayerName());
			}
		}
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null != tarPlayer) {
			if (tarPlayer.getHandCardCount() > 0) {
				tarPlayer.makeAttackCardAvailable();
				player.getGamePlugin().sendChooseCardToDropMessage(tarPlayer, true);
			} else {
				this.resolveCancelByTarget(tarPlayer);
			}
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveContinue() {
		player.setHeroSkill(this);	// Need reset, because it was cleared for target player dying
		this.resolveResult();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorBlack);
	}
	
}
