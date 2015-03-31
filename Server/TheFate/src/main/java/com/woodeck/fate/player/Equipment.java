/**
 * 
 */
package com.woodeck.fate.player;

import java.util.ArrayDeque;
import java.util.Deque;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class Equipment {

	public interface EquipmentListener {
		public void onEquipmentChanged(Equipment equipment, int countChanged, UpdateReason reason);
	}
	private EquipmentListener listener;
	
	
	private Player player;
	private Deque<Integer> cardIds = new ArrayDeque<Integer>(Constant.DEFAULT_OWN_EQUIPMENT_COUNT);
	private Deque<EquipmentCard> cards = new ArrayDeque<EquipmentCard>(Constant.DEFAULT_OWN_EQUIPMENT_COUNT);

	
	public Equipment(Player player) {
		this.player = player;
	}
	
	/**
	 * Getter and Setter method
	 */
	public void setListener(EquipmentListener listener) {
		this.listener = listener;
	}
	
	public Deque<Integer> getCardIds() {
		return cardIds;
	}
	public Deque<EquipmentCard> getCards() {
		return cards;
	}
	
	public EquipmentCard getEquipmentByCardId(int cardId) {
		for (EquipmentCard card : cards) {
			if (card.getCardId() == cardId)
				return card;
		}
		return null;
	}
	
//	If already equipped the transformed card, get it. Otherwise, new one. (e.g.LotharsEdge)
	public EquipmentCard getTransformedEquipment(int cardId) {
		EquipmentCard card = this.getEquipmentByCardId(cardId);
		if (null == card) card = EquipmentCard.newEquipmentWithCardId(cardId, player);
		return card;
	}
	
	public EquipmentCard getWeapon() {
		for (EquipmentCard card : cards) {
			if (card.isWeapon()) return card;
		}
		return null;
	}
	
	public EquipmentCard getArmor() {
		for (EquipmentCard card : cards) {
			if (card.isArmor()) return card;
		}
		return null;
	}
	
	public boolean hasWeapon() {
		for (EquipmentCard card : cards) {
			if (card.isWeapon())
				return true;
		}
		return false;
	}
	
	public boolean hasArmor() {
		for (EquipmentCard card : cards) {
			if (card.isArmor())
				return true;
		}
		return false;
	}
	
	public boolean hasEquippedOneWeapon() {
		for (EquipmentCard card : cards) {
			if (card.isEquippedOne())
				return true;
		}
		return false;
	}
	
	/**
	 * Equipment card buffer update
	 */
	public void addCardIds(Deque<Integer> cardIds, UpdateReason reason) {
		this.cardIds.clear();
		for (Integer cardId : cardIds) {
			this.addOneCardId(cardId);
		}
		listener.onEquipmentChanged(this, cardIds.size(), reason);
	}
	
	private void addOneCardId(int newCardId) {
		PlayingCard newCard = new PlayingCard(newCardId);
		if (newCard.isEquippedOne()) {
			this.cardIds.clear();	// Don't need inform client
			this.cards.clear();
			player.getGame().getTableCardIds().addAll(cardIds);
		} else {
			for (Integer cardId : this.cardIds) {
				PlayingCard card = new PlayingCard(cardId);
//				Only equip one for the same equipment type
				if (card.getEquipmentType() == newCard.getEquipmentType()) {
					this.cardIds.remove(cardId);	// Ditto
					this.removeCard(cardId);
					player.getGame().getTableCardIds().add(cardId);
					break;
				}
			}
		}
		
		if (newCard.isWeapon()) {
			this.cardIds.addFirst(newCardId);	// [0] is weapon
			this.addCard(newCardId);
		} else {
			this.cardIds.addLast(newCardId);	// [1] is armor
			this.addCard(newCardId);
		}
		
//		Equipped card don't add to table cardIds, so need remove it.
		player.getGame().getTableCardIds().remove(newCardId);
	}
	
	public void addCardId(int newCardId, UpdateReason reason) {
		this.addOneCardId(newCardId);
		listener.onEquipmentChanged(this, 1, reason);
	}
	
	public void removeCardId(int cardId, UpdateReason reason) {
		this.cardIds.remove(cardId);
		this.removeCard(cardId);
		listener.onEquipmentChanged(this, -1, reason);
	}
	
	public void removeCardIds(Deque<Integer> cardIds, UpdateReason reason) {
		if (null != cardIds && cardIds.size() > 0) {
			this.cardIds.removeAll(cardIds);
			for (Integer cardId : cardIds) {
				this.removeCard(cardId);
			}
			listener.onEquipmentChanged(this, -cardIds.size(), reason);
		}
	}
	
	public void removeAllCardIds(UpdateReason reason) {
		this.cardIds.clear();
		this.cards.clear();
		listener.onEquipmentChanged(this, -cardIds.size(), reason);
	}
	
	/**
	 * Equipment card instances
	 */
	private void addCard(int cardId) {
		EquipmentCard card = EquipmentCard.newEquipmentWithCardId(cardId, player);
		if (card.isWeapon()) {
			player.setAttackRange(card.getAttackRange());
		} else {
			player.setPlusDistance(player.getPlusDistance()+card.getPlusDistance());
			player.setMinusDistance(player.getMinusDistance()+card.getMinusDistance());
		}
		cards.add(card);
	}
	
	private void removeCard(int cardId) {		
		for (EquipmentCard card : cards) {
			if (card.getCardId() == cardId) {
				if (card.isWeapon()) {
					player.setAttackRange(Constant.DEFAULT_ATTACK_RANGE);
				} else {
					player.setPlusDistance(player.getPlusDistance()-card.getPlusDistance());
					player.setMinusDistance(player.getMinusDistance()-card.getMinusDistance());
				}
				cards.remove(card); break;
			}
		}
	}
	
	public void makeAllCardsDroppable() {
		for (EquipmentCard card : cards) {
			card.setIsDroppable(true);
		}
	}
	public void makeWeaponDroppable() {
		for (EquipmentCard card : cards) {
			if (card.isWeapon()) card.setIsDroppable(true);
		}
	}
	public void makeArmorDroppable() {
		for (EquipmentCard card : cards) {
			if (card.isArmor()) card.setIsDroppable(true);
		}
	}
	public void makeCardDroppableWithColor(int color) {
		for (EquipmentCard card : cards) {
			if (card.getCardColor() == color) card.setIsDroppable(true);
		}
	}
	public void makeCardDroppableWithSuits(int suits) {
		for (EquipmentCard card : cards) {
			if (card.getCardSuits() == suits) card.setIsDroppable(true);
		}
	}
	
	public void resetTriggerFlag() {
		for (EquipmentCard card : cards) {
			card.resetCallback();
			card.setIsTriggered(false);
			card.setIsPassiveLaunchable(false);
			card.setIsDroppable(false);
		}
	}
	
	public void resetDamagedTriggerFlag() {
		for (EquipmentCard card : cards) {
			if (card.getTriggerReasons().contains(TriggerReason.TriggerReasonBeDamaged) ||
				card.getTriggerReasons().contains(TriggerReason.TriggerReasonBeAttackDamaged) ||
				card.getTriggerReasons().contains(TriggerReason.TriggerReasonAnyBeDamaged) ) {
				card.setIsTriggered(false);
			}
		}
	}
	
	public void resetEquipmentUsedTimes() {
		for (EquipmentCard card : cards) {
			card.setUsedTimes(0);
		}
	}
	
	/**************************************************************************
	 * Check if there is equipment can be triggered according to trigger reason
	 * Check armor fist, then check weapon.
	 */
	public boolean checkTrigger(TriggerReason reason) {		
		return (checkArmorTrigger(reason, null) || checkWeaponTrigger(reason, null));
	}
	
	public boolean checkTrigger(TriggerReason reason, Callback callback) {		
		return (checkArmorTrigger(reason, callback) || checkWeaponTrigger(reason, callback));
	}
	
	private boolean checkWeaponTrigger(TriggerReason reason, Callback callback) {
		boolean isTriggered = false;
		if (hasWeapon() && !getWeapon().isTriggered() &&
			getWeapon().getTriggerReasons().contains(reason)) {
			isTriggered = getWeapon().trigger(reason, callback);
			player.getGame().getLogger().debug("Check trigger Weapon: {}", getWeapon());
		}
		return isTriggered;
	}
	
	private boolean checkArmorTrigger(TriggerReason reason, Callback callback) {
		boolean isTriggered = false;
		if (hasArmor() && !getArmor().isTriggered() &&
			getArmor().getTriggerReasons().contains(reason)) {
			isTriggered = getArmor().trigger(reason, callback);
			player.getGame().getLogger().debug("Check trigger armor: {}", getArmor());
		}
		return isTriggered;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Equipment [cardIds=");
		builder.append(cardIds);
		builder.append("]");
		return builder.toString();
	}
	
}
