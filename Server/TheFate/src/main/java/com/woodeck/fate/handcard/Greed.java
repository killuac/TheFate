/**
 * 
 */
package com.woodeck.fate.handcard;

import java.util.ArrayDeque;
import java.util.Deque;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class Greed extends HandCard implements ScheduledCallback {

	private boolean isGreedEquipment;
	
	public Greed(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {				
		if (player.isNPC() && player.isOnlyUseCard()) {
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (p.getHandCardCount() >= 2) {
					return super.isActiveUsable();
				}
			}
			return false;
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectHandCardTargetPlayer();
	}
	
	@Override
	public void resolveUse() {
//		Maybe trigger skill "MultiCast" and resolve again. So need reset below value.
		this.isGreedEquipment = false;
		player.setIsMadeChoice(false);
		player.getTargetPlayer().setIsMadeChoice(false);
		
		Player tarPlayer = player.getTargetPlayer();
		if (tarPlayer.getHandCardCount() > 0 || tarPlayer.getEquipmentCount() > 0) {
			player.setSelectableCardCount(2);
			player.getGamePlugin().sendShowPlayerAllCardsMessage(player, tarPlayer);
			player.getGamePlugin().sendChooseCardToGetMessage(player, true);
		} else {
			player.getGame().backToTurnOwner(this);
		}
		
		if (!this.isStrengthened && player.getHandCardCount() > 0) {
			tarPlayer.setSelectableCardCount(1);
			player.getGamePlugin().sendShowPlayerHandCardMessage(tarPlayer, player);
			player.getGamePlugin().sendChooseCardToGetMessage(tarPlayer, true);
		} else {
			tarPlayer.makeChoice();
		}
	}

	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		Deque<Integer> greededCardIds;
		if (cardIds.isEmpty()) {	// Greed hand card
			greededCardIds = tarPlayer.getCardIdsByCardIndexes(cardIdxes);
		} else {					// Greed equipment
			this.isGreedEquipment = true;
			greededCardIds = cardIds;
		}
		
		Player greedPlayer = player.getGame().getTurnOwner();
		Player greededPlayer = greedPlayer.getTargetPlayer();
		if (tarPlayer.isTurnOwner()) {
			greededPlayer.makeChoice();
			greededPlayer.setGreededCardIds(greededCardIds);
		} else {
			greedPlayer.makeChoice();
			greedPlayer.setGreededCardIds(greededCardIds);
		}
		
//		Strengthened: Give target player one card after greed finish
		if (this.isStrengthened) {
			if (this.isGreedEquipment) {
				greededPlayer.getEquipment().removeCardIds(greededCardIds, UpdateReason.UpdateReasonPlayer);
			} else {
				greededPlayer.getHand().removeCardIds(greededCardIds, UpdateReason.UpdateReasonPlayer);
			}
			
			// Choose hand card to give target player, All hand cards can be selected.
			if (greedPlayer.getHandCardCount() > 0) {
				greedPlayer.setSelectableCardCount(1);
				greedPlayer.setRequiredSelCardCount(1);
				greedPlayer.makeAllHandCardsAvailable();
				greedPlayer.getGamePlugin().sendChooseCardToGiveMessage(greedPlayer);
			} else {
				this.resolveContinue(new ArrayDeque<Integer>());
			}
		}
//		Wait until both players completed the card choosing.
		else {
			if (greedPlayer.isMadeChoice() && greededPlayer.isMadeChoice()) {
				greedPlayer.getHand().removeCardIds(greededPlayer.getGreededCardIds(), UpdateReason.UpdateReasonPlayer);
				if (this.isGreedEquipment) {
					greededPlayer.getEquipment().removeCardIds(greedPlayer.getGreededCardIds(), UpdateReason.UpdateReasonPlayer);
				} else {
					greededPlayer.getHand().removeCardIds(greedPlayer.getGreededCardIds(), UpdateReason.UpdateReasonPlayer);
				}
				
//				Don't need play animation on client UI, so use "UpdateReasonDefault".
				greedPlayer.getHand().addCardIds(greedPlayer.getGreededCardIds(), UpdateReason.UpdateReasonDefault);
				greededPlayer.getHand().addCardIds(greededPlayer.getGreededCardIds(), UpdateReason.UpdateReasonDefault);
				
				player.getGamePlugin().getApi().scheduleExecution(Constant.DEFAULT_DELAY_LONG_TIME, 1, this);
			}
		}
	}
	
	@Override
	public void resolveContinue() {
//		Wait until both players completed the card choosing.
	}
	
	@Override
//	Give card to target player after strengthened
	public void resolveContinue(Deque<Integer> cardIds) {
		player.getHand().removeCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
		player.getHand().addCardIds(player.getGreededCardIds(), UpdateReason.UpdateReasonDefault);
		
		Player tarPlayer = player.getTargetPlayer();
		tarPlayer.getHand().addCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
		
		player.getGamePlugin().getApi().scheduleExecution(Constant.DEFAULT_DELAY_LONG_TIME, 1, this);
	}
	
	@Override
	public boolean checkTargetPlayerAvailable() {
		return (player.getTargetPlayer().getHandCardCount() > 0 ||
				player.getTargetPlayer().getEquipmentCount() > 0);
	}
	
	
	@Override
	public void scheduledCallback() {
		player.getGame().backToTurnOwner(this);		// Delay for card flip from back to front
	}
	
}
