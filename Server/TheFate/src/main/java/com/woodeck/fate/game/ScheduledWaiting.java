/**
 * 
 */
package com.woodeck.fate.game;

import java.util.ArrayDeque;
import java.util.Deque;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.PluginConstant.Action;

/**
 * @author Killua
 *
 */
public class ScheduledWaiting implements ScheduledCallback {

	private Player player;
	
	
	public ScheduledWaiting(Player player) {
		this.player = player;
	}
	
	@Override
	/**
	 * If the scheduled time is up, server will do corresponding operation automatically.
	 */
	public void scheduledCallback() {
		player.setExecutionId(-1);
		player.getGame().getLogger().debug("Player [{}] run scheduled callback while game state: {}",
				player.getPlayerName(), player.getGame().getState());
		
		switch (player.getGame().getState()) {
			case GameStateHeroChoosing:
				player.selectHero(player.getCandidateHeros().peekFirst(), false);
				break;
			
			case GameStateComparing:
				player.choseCardToCompare(player.getHandCardIds().peekFirst());
				break;
				
			case GameStatePlaying:
				if (player.isNPC()) {
					if (player.isOnlyUseCard()) {
						player.autoActivePlayCard();
					} else {
						player.useHandCard(autoSelectedCardIds());
					}
				} else {
					player.startDiscard();
				}
				break;
				
			case GameStateDiscarding:
				player.okayToDiscard(player.fetchHandCardIds(player.getRequiredSelCardCount()));
				break;
			
			case GameStateCardChoosing:
			case GameStateWaitingDispel: {
				Deque<Integer> cardIds = this.autoSelectedCardIds();				
				if (player.isNPC() && player.isOnlyUseCard()) {
					player.autoPassivePlayCard(cardIds);
				} else {
					player.autoChoseCardToUse(cardIds);
				}
				break;
			}
			
			case GameStateBoolChoosing:
				player.choseOkay();
				break;
				
			case GameStateDeathResolving:
				player.choseCancel();
				break;
				
			case GameStateColorChoosing:
				player.choseColor(CardColor.CardColorRed);
				break;
				
			case GameStateSuitsChoosing:
				player.choseSuits(CardSuits.CardSuitsHearts);
				break;
				
			case GameStateGetting: {
				Player turnOwner = this.player.getGame().getTurnOwner();
				Player targetPlayer = turnOwner.getNotResolvedTargetPlayer();
				Player tarPlayer = (this.player.isTurnOwner()) ? targetPlayer : turnOwner;
				Deque<Integer> cardIdxes = this.autoSelectedCardIndexes(tarPlayer);
				Deque<Integer> cardIds = (cardIdxes.size() > 0) ? 
						new ArrayDeque<Integer>() : this.autoSelectedCardIds(tarPlayer);
				this.player.choseCardToGet(cardIdxes, cardIds);
				break;
			}
				
			case GameStateGiving:
				player.choseCardToGive(autoSelectedCardIds());
				break;
				
			case GameStateRemoving: {
				Player tarPlayer = this.player.getTargetPlayer();
				Deque<Integer> cardIdxes = this.autoSelectedCardIndexes(tarPlayer);
				Deque<Integer> cardIds = (cardIdxes.size() > 0) ? 
						new ArrayDeque<Integer>() : this.autoSelectedCardIds(tarPlayer);
				player.choseCardToRemove(cardIdxes, cardIds);
				break;
			}
				
			case GameStateAssigning:
				player.assignedCard(player.getAssignedCardIds());
				break;
				
			case GameStateDropping: {
				Deque<Integer> cardIds = this.autoSelectedCardIds();
				int selCount = player.getSelectableCardCount();				
				if (cardIds.size() >= selCount && (player.isNPC() || !(player.isTurnOwner() || player.isRequiredTarget()))) {
					player.choseCardToDrop(cardIds);
				} else {
					player.choseCancel();
				}
				break;
			}
				
			case GameStateTargetChoosing:	// e.g.发动技能"战意"后，必须选中一个目标，默认是TurnOwner
				if (player.isRequiredTarget()) {
					String turnOwnerName = player.getGame().getTurnOwner().getPlayerName();
					player.getTargetPlayerNames().add(turnOwnerName);
					player.getHeroSkill().resolveUse();
				} else {
					player.choseCancel();
				}
				break;
				
			case GameStatePlayerDying: {
				Deque<Integer> cardIds = this.autoSelectedCardIds();
				int reqCardCount = player.getRequiredSelCardCount();
				Player dyingPlayer = player.getGame().getDyingPlayer();
				if (cardIds.size() >= Math.max(reqCardCount, 1) &&
					(player.isDying() || (player.isNPC() && dyingPlayer.getRoleCard().isSameWithRole(player.getRoleCard())))) {
					if (Action.ActionChooseCardToDrop == player.getGame().getAction()) {
						player.choseCardToDrop(cardIds);
					} else {
						player.choseCardToUse(cardIds);
					}
				} else if (player.isDying() && Action.ActionChooseOkayOrCancel == player.getGame().getAction()) {
					player.choseOkay();
				} else {
					player.choseCancel();
				}
				break;
			}
				
			default:
				break;
		}
	}
	
	private Deque<Integer> autoSelectedCardIds() {
		int selCount = player.getSelectableCardCount();
		Deque<Integer> cardIds = new ArrayDeque<Integer>(selCount);
		if (0 == selCount) return cardIds;
		
		Deque<Integer> selCardIds = player.getAvailableCardIdList();
		if (selCardIds.size() > selCount) {
			for (Integer cardId : selCardIds) {
				if (cardIds.size() == selCount) break;
				cardIds.add(cardId);
			}
		} else {
			cardIds = selCardIds;
		}
		return cardIds;
	}
	
	private Deque<Integer> autoSelectedCardIndexes(Player tarPlayer) {		
		int selCount = this.player.getSelectableCardCount();
		Deque<Integer> cardIdxes = new ArrayDeque<Integer>(selCount);
		if (!player.isDisarming() && tarPlayer.getHandCardCount() > 0) {
			int count = (tarPlayer.getHandCardCount() >= selCount) ? selCount : tarPlayer.getHandCardCount();
			for (int i = 0; i < count; i++) {
				cardIdxes.add(i);
			}
		}
		return cardIdxes;
	}
	
	private Deque<Integer> autoSelectedCardIds(Player tarPlayer) {
		int selCount = this.player.getSelectableCardCount();
		Deque<Integer> cardIds = new ArrayDeque<Integer>(selCount);
		cardIds.add(tarPlayer.getEquipmentCardIds().peekFirst());
		
		return cardIds;
	}
	
}
