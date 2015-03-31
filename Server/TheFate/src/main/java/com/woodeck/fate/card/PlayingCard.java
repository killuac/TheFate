/**
 * 
 */
package com.woodeck.fate.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.util.FileConstant;

import net.sf.plist.NSArray;
import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

/**
 * = author Killua
 *
 */
public class PlayingCard extends Card {
	
	public static final String
		kCardType               = "cardType",
		kCardFigure             = "cardFigure",
		kCardSuits              = "cardSuits",
		kTargetCount            = "targetCount",
		kDamageValue			= "damageValue",
		kIsStrengthenable      	= "isStrengthenable",
		kRequiredSp          	= "requiredSp",
		kEquipmentType          = "equipmentType",
		kPlusDistance           = "plusDistance",
		kMinusDistance          = "minusDistance",
		kAttackRange            = "attackRange",
		kIsActiveLaunchable		= "isActiveLaunchable",
		kIsEquippedOne          = "isEquippedOne",
		kTransformedCardId		= "transformedCardId",
		kMinHandCardCount		= "minHandCardCount",
		kTriggerReasons			= "triggerReasons",
		kDescription            = "description",
		kTipText                = "tipText",
		kTargetTipText          = "targetTipText",
		kDispelTipText          = "dispelTipText",
		kHistoryText			= "historyText",
		kHistoryText2			= "historyText2";
	
	public enum PlayingCardEnum {
		PlayingCardNull (0),
		PlayingCardNormalAttack (1),           	// 普通攻击
		PlayingCardFlameAttack (2),            	// 火焰攻击
		PlayingCardChaosAttack (3),            	// 混乱攻击
		PlayingCardEvasion (4),                	// 闪避
		PlayingCardHealingSalve (5),           	// 治疗药膏

		PlayingCardFanaticism (6),             	// 狂热
		PlayingCardMislead (7),                	// 误导
		PlayingCardChakra (8),                 	// 查克拉
		PlayingCardWildAxe (9),                	// 野性之斧
		PlayingCardDispel (10),                 // 驱散
		PlayingCardDisarm  (11),               	// 缴械
		PlayingCardElunesArrow (12),           	// 月神之箭
		PlayingCardEnergyTransport (13),		// 能量转移
		PlayingCardGreed (14),                 	// 贪婪
		PlayingCardSirenSong (15),             	// 海妖之歌

		PlayingCardGodsStrength (16),          	// 神之力量
		PlayingCardViperRaid (17),             	// 蝮蛇突袭
		PlayingCardTimeLock (18),              	// 时间静止
		PlayingCardSunder (19),                	// 灵魂隔断
		PlayingCardLagunaBlade (20),           	// 神灭斩

		PlayingCardEyeOfSkadi (21),            	// 冰魄之眼
		PlayingCardBladesOfAttack (22),        	// 攻击之爪
		PlayingCardSacredRelic (23),           	// 圣者遗物
		PlayingCardDemonEdge (24),             	// 恶魔刀锋
		PlayingCardDiffusalBlade (25),         	// 散失之刃
		PlayingCardLotharsEdge (26),           	// 洛萨之锋
		PlayingCardStygianDesolator (27),      	// 黯灭之刃
		PlayingCardSangeAndYasha (28),         	// 散夜对剑
		PlayingCardPlunderAxe (29),            	// 掠夺之斧
		PlayingCardMysticStaff (30),           	// 神秘法杖
		PlayingCardEaglehorn (31),             	// 鹰角弓
		PlayingCardQuellingBlade (32),         	// 补刀斧

		PlayingCardPhyllisRing (33),           	// 菲丽丝之戒
		PlayingCardBladeMail (34),             	// 刃甲
		PlayingCardBootsOfSpeed (35),          	// 速度之靴
		PlayingCardPlaneswalkersCloak (36),    	// 流浪法师斗篷
		PlayingCardTalismanOfEvasion (37);     	// 闪避护符
	    
	    public final int id;
	    private static Map<Integer, PlayingCardEnum> map;
	    
	    private PlayingCardEnum(int id) {
	    	this.id = id;
	    	registerEnum(this);
	    }
	    
	    private static void registerEnum(PlayingCardEnum cardEnum) {
	    	if (null == map)
	    		map = new HashMap<Integer, PlayingCardEnum>();
	    	map.put(cardEnum.id, cardEnum);
	    }
	    
	    public static PlayingCardEnum getEnumById(int key) {
	    	return map.get(key);
	    }
	}
	
	public interface CardFigure {
		public static final int
		    CardFigure1 = 1,
		    CardFigure2 = 2,
		    CardFigure3 = 3,
		    CardFigure4 = 4,
		    CardFigure5 = 5,
		    CardFigure6 = 6,
		    CardFigure7 = 7,
		    CardFigure8 = 8,
		    CardFigure9 = 9,
		    CardFigure10 = 10,
		    CardFigure11 = 11,
		    CardFigure12 = 12,
		    CardFigure13 = 13;
	}
	
	public interface CardColor {
		public static final int
			CardColorNull = -1,
		    CardColorRed = 0,			// 红色
		    CardColorBlack = 1;			// 黑色
	}
	
	public interface CardSuits {
		public static final int
		    CardSuitsNull = -1,
		    CardSuitsDiamonds = 0,		// 方块
		    CardSuitsClubs = 1,			// 梅花
	    	CardSuitsHearts = 2,		// 红桃
			CardSuitsSpades = 3;		// 黑桃
	}
	
	public interface CardType {
		public static final int
			CardTypeBasic = 0,			// 基本牌
		    CardTypeMagic = 1,			// 魔法牌
		    CardTypeSuperSkill = 2,		// S技能牌
		    CardTypeEquipment = 3;		// 装备牌
	}
	
	public interface EquipmentType {
		public static final int
			EquipmentTypeWeapon = 0,	// 武器
		    EquipmentTypeArmor = 1;		// 防具
	}
	
	
//	cardIdlist and cardList buffer both need, if only use one, the other one will be overwrote.
	private static NSArray cardIdList;
	static {
		try {
			cardIdList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_PLAYING_CARDIDS));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static NSArray cardList;
	static {
		try {
			cardList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_PLAYING_CARD_LIST));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int cardCount() {
		return cardIdList.array().length;
	}
	
	private PlayingCardEnum cardEnum;
	private int cardFigure;	// 转化后的卡牌可能改变其属性
	private int cardSuits;	// Ditto
	private int cardType;
	private int targetCount;
	
	private int damageValue;
	private boolean isStrengthenable;
	private int requiredSp;
	
	private int equipmentType;
	private int plusDistance;
	private int minusDistance;
	private int attackRange;
	private int minHandCardCount;			// 动装备最少需要的手牌数
	private boolean isActiveLaunchable;		// 是否可以主动发动
	private boolean isEquippedOne;			// 武器和防具是否只允许装一个
	private int transformedCardId;			// 转化后的卡牌ID(如散夜对剑:两张手牌当攻击使用)
	private List<TriggerReason> triggerReasons = new ArrayList<TriggerReason>();
	
	protected String historyText;
	protected String historyText2;
	
	public PlayingCard(Integer cardId) {
		super(cardId);
		if (cardId <= 0) return;
//		Read playing card figure and suits by card id
		NSDictionary cardInfo = (NSDictionary)cardIdList.array()[cardId];
		cardEnum = PlayingCardEnum.getEnumById(cardInfo.get(kCardEnum).toInteger());
		cardFigure = cardInfo.get(kCardFigure).toInteger();
		cardSuits = cardInfo.get(kCardSuits).toInteger();
		
//		Read playing card detail information by card enumeration
		cardInfo = (NSDictionary)cardList.array()[cardEnum.id];
		cardName = cardInfo.get(kCardName).toString();
		cardText = cardInfo.get(kCardText).toString();
		cardType = cardInfo.get(kCardType).toInteger();
		targetCount = cardInfo.get(kTargetCount).toInteger();
		damageValue = cardInfo.get(kDamageValue).toInteger();
		
		if (this.isMagicCard()) {
			isStrengthenable = cardInfo.get(kIsStrengthenable).toBoolean();
		}
		if (isStrengthenable || this.isSuperSkill()) {
			requiredSp = cardInfo.get(kRequiredSp).toInteger();
		}
		if (this.isEquipment()) {
			equipmentType = cardInfo.get(kEquipmentType).toInteger();
			isActiveLaunchable = cardInfo.get(kIsActiveLaunchable).toBoolean();
			isEquippedOne = cardInfo.get(kIsEquippedOne).toBoolean();
			minHandCardCount = cardInfo.get(kMinHandCardCount).toInteger();
			transformedCardId = cardInfo.get(kTransformedCardId).toInteger();
			if (this.isWeapon()) {
				attackRange = cardInfo.get(kAttackRange).toInteger();
			}
			if (this.isArmor()) {
				plusDistance = cardInfo.get(kPlusDistance).toInteger();
				minusDistance = cardInfo.get(kMinusDistance).toInteger();
			}
			
			List<NSObject> reasonIds = cardInfo.get(kTriggerReasons).toList();
			for (NSObject reasonId : reasonIds) {
				triggerReasons.add(TriggerReason.getEnumById(reasonId.toInteger()));
			}
		}
		
		historyText = cardInfo.get(kHistoryText).toString();
		if (null != cardInfo.get(kHistoryText2)) historyText2 = cardInfo.get(kHistoryText2).toString();
	}
	
	public static Deque<PlayingCard> playingCardsByCardIds(Deque<Integer> cardIds) {
		Deque<PlayingCard> cards = new ArrayDeque<PlayingCard>(cardIds.size());
		for (Integer cardId : cardIds) {
			cards.add(new PlayingCard(cardId.intValue()));
		}
		return cards;
	}
	
	public static Deque<Integer> playingCardIdsByCards(Deque<PlayingCard> cards) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>(cards.size());
		for (PlayingCard card : cards) {
			cardIds.add(card.cardId);
		}
		return cardIds;
	}
	
	/**
	 * Getter method
	 */
	public PlayingCardEnum getCardEnum() {
		return cardEnum;
	}
	
	public int getCardFigure() {
		return cardFigure;
	}
	public void setCardFigure(int cardFigure) {
		this.cardFigure = cardFigure;
	}
	
	public int getCardSuits() {
		return cardSuits;
	}
	public void setCardSuits(int cardSuits) {
		this.cardSuits = cardSuits;
	}
	
	public int getCardColor() {
		if (CardSuits.CardSuitsNull == cardSuits)
			return CardColor.CardColorNull;
		
		if (CardSuits.CardSuitsHearts == cardSuits || CardSuits.CardSuitsDiamonds == cardSuits) {
	        return CardColor.CardColorRed;
	    } else {
	        return CardColor.CardColorBlack;
	    }
	}
	
	public boolean isSpades() {
		return (CardSuits.CardSuitsSpades == cardSuits);
	}
	public boolean isHearts() {
		return (CardSuits.CardSuitsHearts == cardSuits);
	}
	public boolean isClubs() {
		return (CardSuits.CardSuitsClubs == cardSuits);
	}
	public boolean isDiamonds() {
		return (CardSuits.CardSuitsDiamonds == cardSuits);
	}
	
	public boolean isRedColor() {
		return (CardColor.CardColorRed == getCardColor());
	}
	public boolean isBlackColor() {
		return (CardColor.CardColorBlack == getCardColor());
	}
	
	public int getCardType() {
		return cardType;
	}
	
	public int getTargetCount() {
		return targetCount;
	}
	public boolean isTargetable() {
		return (targetCount > 0);
	}
	
	public int getDamageValue() {
		return damageValue;
	}
	public boolean isStrengthenable() {
		return isStrengthenable;
	}
	public boolean isDispellable() {
		return this.isMagicCard();
	}
	public int getRequiredSp() {
		return requiredSp;
	}
	
	public int getEquipmentType() {
		return equipmentType;
	}
	public int getPlusDistance() {
		return plusDistance;
	}
	public int getMinusDistance() {
		return minusDistance;
	}
	public int getAttackRange() {
		return attackRange;
	}
	public int getMinHandCardCount() {
		return minHandCardCount;
	}
	public boolean isActiveLaunchable() {
		return isActiveLaunchable;
	}
	public boolean isEquippedOne() {
		return isEquippedOne;
	}
	public int getTransformedCardId() {
		return transformedCardId;
	}
	public PlayingCard getTransformedCard() {
		return new PlayingCard(this.getTransformedCardId());
	}
	
	public boolean isBasicCard() {
		return (CardType.CardTypeBasic == cardType);
	}
	public boolean isAttack() {
		return (PlayingCardEnum.PlayingCardNormalAttack == cardEnum ||
				PlayingCardEnum.PlayingCardFlameAttack  == cardEnum ||
				PlayingCardEnum.PlayingCardChaosAttack  == cardEnum);
	}
	public boolean isNormalAttack() {
		return (PlayingCardEnum.PlayingCardNormalAttack == cardEnum);
	}
	public boolean isEvasion() {
		return (PlayingCardEnum.PlayingCardEvasion == cardEnum);
	}
	
	public boolean isMagicCard() {
		return (CardType.CardTypeMagic == cardType);
	}
	public boolean isDispel() {
		return (PlayingCardEnum.PlayingCardDispel == cardEnum);
	}
	public boolean isWildAxe() {
		return (PlayingCardEnum.PlayingCardWildAxe == cardEnum);
	}
	
	public boolean isSuperSkill() {
		return (CardType.CardTypeSuperSkill == cardType);
	}
	
	public boolean isEquipment() {
		return (CardType.CardTypeEquipment == cardType);
	}
	public boolean isWeapon() {
		return (EquipmentType.EquipmentTypeWeapon == equipmentType);
	}
	public boolean isArmor() {
		return (EquipmentType.EquipmentTypeArmor == equipmentType);
	}
	
	public List<TriggerReason> getTriggerReasons() {
		return triggerReasons;
	}
	
	public String getFigureText() {
		return (CardFigure.CardFigure1 == cardFigure) ? "A" : Integer.toString(cardFigure);
	}
	public String getSuitsText() {
		switch (cardSuits) {
	        case CardSuits.CardSuitsSpades:
	            return "♠";
	        case CardSuits.CardSuitsHearts:
	            return "♥";
	        case CardSuits.CardSuitsClubs:
	            return "♣";
	        case CardSuits.CardSuitsDiamonds:
	            return "♦";
	        default:
	            return null;
	    }
	}
	public String getCardFullText() {
		StringBuffer sb = new StringBuffer();
		sb.append(cardText);
		sb.append("(");
		sb.append(this.getSuitsText());
		sb.append(this.getFigureText());
		sb.append(")");
		return sb.toString();
	}
	public String getHistoryText() {
		return historyText;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlayingCard [cardName=");
		builder.append(cardName);
		builder.append(", cardText=");
		builder.append(cardText);
		builder.append(", cardFigure=");
		builder.append(cardFigure);
		builder.append(", cardSuits=");
		builder.append(cardSuits);
		builder.append(", cardType=");
		builder.append(cardType);
		builder.append(", targetCount=");
		builder.append(targetCount);
		builder.append(", damageValue=");
		builder.append(damageValue);
		builder.append(", isStrengthenable=");
		builder.append(isStrengthenable);
		builder.append(", requiredSp=");
		builder.append(requiredSp);
		builder.append(", equipmentType=");
		builder.append(equipmentType);
		builder.append(", attackRange=");
		builder.append(attackRange);
		builder.append("]");
		return builder.toString();
	}

}
