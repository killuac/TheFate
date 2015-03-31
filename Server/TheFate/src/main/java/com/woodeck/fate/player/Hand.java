/**
 * 
 */
package com.woodeck.fate.player;

import java.util.ArrayDeque;
import java.util.Deque;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class Hand {

	public interface HandListener {
		public void onHandCardChanged(Hand hand, int countChanged, UpdateReason reason);
	}
	private HandListener listener;
	
	
	private Player player;
	private Deque<Integer> cardIds = new ArrayDeque<Integer>();
	private Deque<HandCard> cards = new ArrayDeque<HandCard>();

	
	public Hand(Player player, Deque<Integer> cardIds) {
		this.player = player;
		this.cardIds = cardIds;
		this.cards = this.cardsByCardIds(cardIds);
	}

	/**
	 * Getter and Setter method
	 */
	public void setListener(HandListener listener) {
		this.listener = listener;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Deque<Integer> getCardIds() {
		return cardIds;
	}
	
	public Deque<HandCard> getCards() {
		return this.cards;
	}
	
	public HandCard cardByCardId(int cardId) {
		if (new PlayingCard(cardId).isEquipment()) {
			return EquipmentCard.newEquipmentWithCardId(cardId, player);
		} else {
			return HandCard.newCardWithCardId(cardId, player);
		}
	}
	
	public Deque<HandCard> cardsByCardIds(Deque<Integer> cardIds) {
		Deque<HandCard> cards = new ArrayDeque<HandCard>();
		for (Integer cardId : cardIds) {
			cards.add(this.cardByCardId(cardId));
		}
		return cards;
	}
	
	/**
	 * Hand card id buffer update
	 */
	public void addCardIds(Deque<Integer> cardIds, UpdateReason reason) {
		if (null != cardIds && cardIds.size() > 0) {
			this.cardIds.addAll(cardIds);
			this.cards.addAll(cardsByCardIds(cardIds));
			listener.onHandCardChanged(this, cardIds.size(), reason);
		}
	}
	
	public void addCardId(Integer cardId, UpdateReason reason) {
		this.cardIds.add(cardId);
		this.cards.add(cardByCardId(cardId));
		listener.onHandCardChanged(this, 1, reason);
	}
	
	public void removeCardIds(Deque<Integer> cardIds, UpdateReason reason) {
		if (null != cardIds && cardIds.size() > 0) {
			this.cardIds.removeAll(cardIds);
			this.cards.removeAll(cardsByCardIds(cardIds));
			listener.onHandCardChanged(this, -cardIds.size(), reason);
		}
	}
	
	public void removeCardId(Integer cardId, UpdateReason reason) {
		this.cardIds.remove(cardId);
		this.cards.remove(cardByCardId(cardId));
		listener.onHandCardChanged(this, -1, reason);
	}
	
	public void removeAllCardIds(UpdateReason reason) {
		this.cardIds.clear();
		this.cards.clear();
		listener.onHandCardChanged(this, -cardIds.size(), reason);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Hand [cardIds=");
		builder.append(cardIds);
		builder.append("]");
		return builder.toString();
	}

}
