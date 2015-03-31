/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.ArrayDeque;
import java.util.Deque;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Illuminate extends BaseSkill {

	public Illuminate(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && (player.getHandCardCount()+player.getEquipmentCount() >= 3));
		if (player.isNPC() && isAvailable) {
			Deque<Integer> cardSuits = new ArrayDeque<Integer>();
			for (HandCard card : player.getHandCards()) {
				if (!cardSuits.contains(card.getCardSuits())) {
					cardSuits.add(card.getCardSuits());
				}
			}
			return (cardSuits.size() >= 3 && player.getHandCardCount() > 3);
		}
		return isAvailable;
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(3);
		player.getEquipment().makeAllCardsDroppable();
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
			int damage = this.getDamageValue();
			tarPlayer.updateHeroHpSp(-damage, damage, this);
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
		if (player.isNPC()) {
			player.makeAllHandCardsUnavailable();
			Deque<Integer> cardSuits = new ArrayDeque<Integer>();
			for (HandCard card : player.getHandCards()) {
				if (!cardSuits.contains(card.getCardSuits())) {
					cardSuits.add(card.getCardSuits());
					card.setIsAvailable(true);
				}
			}
		} else {
			player.makeAllHandCardsAvailable();
		}
	}
	
}
