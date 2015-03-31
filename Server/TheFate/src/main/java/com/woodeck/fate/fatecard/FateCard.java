/**
 * 
 */
package com.woodeck.fate.fatecard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.sf.plist.NSArray;
import net.sf.plist.NSDictionary;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.woodeck.fate.card.Card;
import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.FileConstant;

/**
 * @author Killua
 *
 */
public class FateCard extends Card {
	
	public interface FateCardId {
		public static final Integer
			FateCardNull = 0,
			FateCardVersus = 1,						// 对战模式
			FateCardShadowPunisher = 2,         	// 暗影惩戒者
			FateCardConquerorOfHolyLight = 3,		// 圣光征服者
			FateCardSpreadingPlague = 4,        	// 蔓延的瘟疫
			FateCardGameofFate = 5,             	// 命运的博弈
			FateCardPuppetOfBackfire = 6,       	// 反噬的傀儡
			FateCardSummonerOfDeath = 7,        	// 死神的召唤者
			FateCardParanoidMathematician = 8,		// 偏执的数学家
			FateCardRoshanPossession = 9;			// Roshan附体
	}
	
	
	private static NSArray cardList;
	static {
		try {
			cardList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_FATE_CARD_LIST));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int cardCount() {
		return cardList.array().length;
	}
	
	public static FateCard newCardWithCardId(Integer cardId) {
		try {
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(FateCard.class.getPackage().getName());
			sbPath.append(".");
			sbPath.append(new FateCard(cardId).getCardName());
			return (FateCard) FateCard.class.getClassLoader().loadClass(sbPath.toString()).
					getConstructor(Integer.class).newInstance(cardId);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public FateCard(Integer cardId) {
		super(cardId);
		NSDictionary cardInfo = (NSDictionary)cardList.array()[cardId];
		this.cardName = cardInfo.get(kCardName).toString();
		this.cardText = cardInfo.get(kCardText).toString();
		this.cardDesc = cardInfo.get(kCardDesc).toString();
	}
	
	public boolean checkGameOver(Game game, Player deadPlayer) {
		RoleCard deadRole = new RoleCard(deadPlayer.getRoleCardId());
		if (deadRole.isNeutral()) return false;
		
//		Check if players who have same role with the dead player are alive, if none, game over.
		for (Player player : game.getAlivePlayers()) {
			if (player.getRoleCard().isSameWithRole(deadRole))
				return false;
		}
		
		for (Player player : game.getAllPlayers()) {
			if (!player.getRoleCard().isSameWithRole(deadRole) && !player.getRoleCard().isNeutral()) {
				player.setIsVictory(true);
			}
		}
		
		return true;
	}
	
	/**
	 * Getter method
	 */
	public boolean isSummonerOfDeath() {
		return (FateCardId.FateCardSummonerOfDeath == cardId);
	}
	
	public boolean isParanoidMathematician() {
		return (FateCardId.FateCardParanoidMathematician == cardId);
	}
	
}
