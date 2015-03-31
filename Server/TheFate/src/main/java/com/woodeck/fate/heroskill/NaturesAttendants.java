/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class NaturesAttendants extends BaseSkill {

	public NaturesAttendants(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && (player.getHandCardCount()+player.getEquipmentCount() > 0));
		if (player.isNPC() && isAvailable) {
			for (Player tarPlayer : player.getGame().getPartnerPlayers()) {
				if (tarPlayer.getHealthPoint() < tarPlayer.getHpLimit()) {
					return (player.getHandCardCountBySuits(CardSuits.CardSuitsHearts) > 0);
				}
			}
			return false;
		}
		return isAvailable;
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(1);
		player.getEquipment().makeCardDroppableWithSuits(CardSuits.CardSuitsHearts);
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMinHpTargetPlayer(true);
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		this.usedTimes++;
		Deque<Player> tarPlayers = player.getTargetPlayers();
		player.getCharacter().updateHeroHpSp(0, -tarPlayers.size());
		super.resolveUse(cardIds);
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null != tarPlayer) {
			tarPlayer.updateHeroHpSp(1, 0, this);	// Resolve target one by one
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveContinue() {
		this.resolveResult();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableBySuits(CardSuits.CardSuitsHearts);
	}
	
}
