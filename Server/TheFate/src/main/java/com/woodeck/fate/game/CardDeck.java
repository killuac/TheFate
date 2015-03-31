/**
 * 
 */
package com.woodeck.fate.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyRoomVariable;
import com.woodeck.fate.card.HeroCard;
import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.fatecard.FateCard;
import com.woodeck.fate.model.User;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class CardDeck {
		
	public interface CardDeckListener {
		public void onDeckCardChanged(CardDeck cardDeck);
	}
	private CardDeckListener listener;
	
	private Game game;
	private Deque<Integer> heroCardIds;
	private Deque<Integer> remainingCardIds;
	private List<Integer> discardCardIds = new ArrayList<Integer>();
	
	
	/**
	 * Shuffle all deck cards
	 */
	public CardDeck(Game game) {
		this.game = game;
		
//		Hero cards
		heroCardIds = this.getShuffledCardIdList(HeroCard.cardCount());
		heroCardIds.removeAll(Arrays.asList(9, 23, 24, 30));
//		heroCardIds = new ArrayDeque<Integer>(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10));
		this.removePickedHeroIds();
		
//		Playing cards
		remainingCardIds = this.getShuffledCardIdList(PlayingCard.cardCount());
		remainingCardIds.removeAll(Arrays.asList(85, 86));	// TODO: Hard code
	}
	
	private Deque<Integer> getShuffledCardIdList(int count) {
		List<Integer> cardIdList = new ArrayList<Integer>(count);
		for (int i = 1; i < count; i++) {
			cardIdList.add(i);
		}
		Collections.shuffle(cardIdList);
		Deque<Integer> cardIds = new ArrayDeque<Integer>(cardIdList);
		
		return cardIds;
	}
	
	private void removePickedHeroIds() {
		int zoneId = game.getPlugin().getApi().getZoneId();
		int roomId = game.getPlugin().getApi().getRoomId();
		ReadOnlyRoomVariable roomVal = game.getPlugin().getApi().getRoomVariable(zoneId, roomId, VarConstant.kVarPickedHero);
		int[] array = roomVal.getValue().getIntegerArray(VarConstant.kParamPickedHeroIds, new int[]{});
		for (int i = 0; i < array.length; i++) {
			heroCardIds.remove(array[i]);
		}
		
//		Update room variable for clear picked hero ids
		EsObject value = new EsObject();
		value.setIntegerArray(VarConstant.kParamPickedHeroIds, new int[]{});
		game.getPlugin().getApi().updateRoomVariable(zoneId, roomId, VarConstant.kVarPickedHero, true, value, false, false);
	}
	
	/**
	 * Getter and Setter method
	 */
	public void setListener(CardDeckListener listener) {
		this.listener = listener;
	}
	
	public Deque<Integer> getRemainingCardIds() {
		return remainingCardIds;
	}
	
	/**
	 * Card fetching and updating
	 */
	public int fetchOneFateCardId() {
		Deque<Integer> fateCardIds = this.getShuffledCardIdList(FateCard.cardCount());
		fateCardIds.removeAll(Arrays.asList(1, 9));	// TODO:Remove "Versus" and "Roshan"
		return fateCardIds.pollFirst();
	}
	
	public Deque<Integer> fetchRoleCardIds(int count) {
		return this.getShuffledCardIdList(count+1);
	}
	
	public Deque<Integer> fetchHeroCardIdsToPlayer(int count, Player player) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>(count);
		User user = BeanHelper.getUserBean().getUserByName(player.getPlayerName());
		Deque<Integer> availableHeroIds = (player.isNPC()) ? heroCardIds : user.getAvailableHeroIds();
		
//		1. Fetch one from own hero list(not free)
		if (!player.isNPC() && user.getOwnHeroIds().size() > 0) {
			for (int i = 0; i < heroCardIds.size(); i++) {
				if (user.getOwnHeroIds().contains(heroCardIds.peekFirst())) {
					cardIds.add(heroCardIds.pollFirst());
					count -= 1; break;
				} else {
					heroCardIds.add(heroCardIds.pollFirst());
				}
			}
		}
		
//		2. Fetch from free hero list
		for (int i = 0; i < count; i++) {
			if (availableHeroIds.contains(heroCardIds.peekFirst())) {
				cardIds.add(heroCardIds.pollFirst());
			} else {
				heroCardIds.add(heroCardIds.pollFirst());
				count += 1;		// Hero is not free, user can't select it, fetch next.
			}
		}
		
		return cardIds;
	}
	
	public Deque<Integer> fetchPlayingCardIds(int count) {
		if (count >= remainingCardIds.size()) {
			Collections.shuffle(discardCardIds);
			remainingCardIds.addAll(discardCardIds);
			discardCardIds.clear();
		}
		
		Deque<Integer> cardIds = new ArrayDeque<Integer>(count);
		for (int i = 0; i < count; i++) {
			cardIds.add(remainingCardIds.pollFirst());
		}
		
		listener.onDeckCardChanged(this);
		return cardIds;
	}
	
	public void addDiscardCardIds(Deque<Integer> cardIds) {
		discardCardIds.addAll(cardIds);
	}
	
}
