/**
 * 
 */
package com.woodeck.fate.card;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.plist.NSArray;
import net.sf.plist.NSDictionary;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.woodeck.fate.util.FileConstant;

/**
 * @author Killua
 *
 */
public class RoleCard extends Card {
	
	public enum RoleCardEnum {
		RoleCardNull (0),
		RoleCardSentinel (1),		// 近卫
		RoleCardScourge (2),		// 天灾
		RoleCardNeutral (3);		// 中立
		
		public final int id;
	    private static Map<Integer, RoleCardEnum> map;
	    
	    private RoleCardEnum(int id) {
	    	this.id = id;
	    	registerEnum(this);
	    }
	    
	    private static void registerEnum(RoleCardEnum cardEnum) {
	    	if (null == map)
	    		map = new HashMap<Integer, RoleCardEnum>();
	    	map.put(cardEnum.id, cardEnum);
	    }
	    
	    public static RoleCardEnum getEnumById(int key) {
	    	return map.get(key);
	    }
	}
	
	
	private static NSArray cardList;
	static {
		try {
			cardList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_ROLE_CARD_LIST));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int cardCount() {
		return cardList.array().length;
	}
	
	private RoleCardEnum cardEnum;
	
	public RoleCard(Integer cardId) {
		super(cardId);
		NSDictionary cardInfo = (NSDictionary)cardList.array()[cardId];
		this.cardEnum = RoleCardEnum.getEnumById(cardInfo.get(kCardEnum).toInteger());
		this.cardName = cardInfo.get(kCardName).toString();
		this.cardText = cardInfo.get(kCardText).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (cardEnum == ((RoleCard) obj).getCardEnum());
	}
	
	public boolean isSameWithRole(RoleCard card) {
		return (cardEnum == card.getCardEnum());
	}
	
	/**
	 * Getter method
	 */
	public RoleCardEnum getCardEnum() {
		return cardEnum;
	}
	
	public boolean isSentinel() {
		return (RoleCardEnum.RoleCardSentinel == cardEnum);
	}
	
	public boolean isScourge() {
		return (RoleCardEnum.RoleCardScourge == cardEnum);
	}
	
	public boolean isNeutral() {
		return (RoleCardEnum.RoleCardNeutral == cardEnum);
	}
	
}
