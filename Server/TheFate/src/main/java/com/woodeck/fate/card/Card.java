/**
 * 
 */
package com.woodeck.fate.card;


/**
 * @author Killua
 *
 */
public abstract class Card {
	
	public static final String
		kCardEnum 		= "cardEnum",
		kCardName 		= "cardName",
		kCardText 		= "cardText",
		kCardDesc 		= "cardDesc";
	
	protected int cardId;
	protected String cardName;
	protected String cardText;
	protected String cardDesc;

	
	public Card(Integer cardId) {
		if (cardId <= 0) return;
		this.cardId = cardId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (cardId == ((Card) obj).getCardId());
	}
	
	/**
	 * Getter method
	 */
	public Integer getCardId() {
		return cardId;
	}
	public String getCardName() {
		return cardName;
	}
	public String getCardText() {
		return cardText;
	}
	public String getCardDesc() {
		return cardDesc;
	}

	public boolean isPlayingCard() {
		return this.equals(PlayingCard.class);
	}
}
