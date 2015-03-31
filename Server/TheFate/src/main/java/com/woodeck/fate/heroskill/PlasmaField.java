/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.ArrayDeque;
import java.util.Deque;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class PlasmaField extends BaseSkill {

	public PlasmaField(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
				if (player.isAttackRangeEnough(tarPlayer)) {
					return super.isActiveLaunchable();
				}
			}
			return false;
		}
		return super.isActiveLaunchable();
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		Player tarPlayer = player.selectMinHpTargetPlayer(false);
		if (!player.isAttackRangeEnough(tarPlayer)) {
			player.getTargetPlayerNames().clear();
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (player.isAttackRangeEnough(p)) {
					player.getTargetPlayerNames().add(p.getPlayerName()); break;
				}
			}
		}
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null == tarPlayer) {
			player.getGame().backToTurnOwner(); return;
		}
		
//		弃了4张不同花色，直接让目标掉血
		if (player.getGame().getTableCardCount() == 4) {
			int damage = this.getDamageValue();
			tarPlayer.updateHeroHpSp(-damage, damage, this);
		} else {
			if (tarPlayer.getHandCardCount() > 0) {
				this.makeTargetPlayerHandCardAvailable(tarPlayer);
				player.getGamePlugin().sendChooseCardToDropMessage(tarPlayer, true);
			} else {
				this.resolveCancelByTarget(tarPlayer);
			}
		}
	}
	
	@Override
	public void resolveContinue() {
		this.resolveResult();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
	private void makeTargetPlayerHandCardAvailable(Player tarPlayer) {
		Deque<Integer> cardSuits = new ArrayDeque<Integer>(player.getGame().getTableCardCount());
		for (PlayingCard card : player.getGame().getTableCards()) {
			cardSuits.add(card.getCardSuits());
		}
		
		for (HandCard card : tarPlayer.getHandCards()) {
			card.setIsAvailable(!cardSuits.contains(card.getCardSuits()));
		}
	}
	
}
