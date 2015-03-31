/**
 * 
 */
package com.woodeck.fate.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.plist.NSArray;
import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.woodeck.fate.util.FileConstant;

/**
 * = author Killua
 *
 */
public class HeroCard extends Card {

	public static final String
		kAttribute     	= "attribute",
		kHpLimit    	= "hpLimit",
		kSpLimit    	= "spLimit",
		kHandSizeLimit	= "handSizeLimit",
		kSkillIds		= "skillIds",
		kPrice			= "price";
	
	public interface HeroCardId {
		public static final Integer
			HeroCardNull = 0,
			HeroCardLordOfAvernus = 1,				// 死亡骑士
			HeroCardSkeletonKing = 2,              	// 骷髅王
			HeroCardBristleback = 3,               	// 刚背兽
			HeroCardSacredWarrior = 4,             	// 神灵武士
			HeroCardOmniknight = 5,                	// 全能骑士
			HeroCardAxe = 6,                       	// 斧王
			HeroCardCentaurWarchief = 7,           	// 半人马酋长
			HeroCardDragonKnight = 8,              	// 龙骑士
			HeroCardGuardianKnight = 9,            	// 守护骑士

			HeroCardGorgon = 10,                    // 蛇发女妖
			HeroCardLightningRevenant = 11,			// 闪电幽魂
			HeroCardJuggernaut = 12,               	// 剑圣
			HeroCardVengefulSpirit = 13,           	// 复仇之魂
			HeroCardStrygwyr = 14,                 	// 血魔
			HeroCardTrollWarlord = 15,             	// 巨魔战将
			HeroCardDwarvenSniper = 16,            	// 矮人火枪手
			HeroCardNerubianAssassin = 17,         	// 地穴刺客
			HeroCardAntimage = 18,                 	// 敌法师
			HeroCardNerubianWeaver = 19,           	// 地穴编织者
			HeroCardUrsaWarrior = 20,              	// 熊战士
			HeroCardChenYunSheng = 21,             	// 陈云生

			HeroCardSlayer = 22,                   	// 秀逗魔导师
			HeroCardNecrolyte = 23,                	// 死灵法师
			HeroCardTwinHeadDragon = 24,           	// 双头龙
			HeroCardCrystalMaiden = 25,            	// 水晶室女
			HeroCardLich = 26,                     	// 巫妖
			HeroCardShadowPriest = 27,             	// 暗影牧师
			HeroCardOrgeMagi = 28,                 	// 食人魔法师
			HeroCardKeeperOfTheLight = 29,         	// 光之守卫
			HeroCardGoblinTechies = 30,            	// 哥布林工程师
			HeroCardStormSpirit = 31,              	// 风暴之灵
			HeroCardEnchantress = 32,              	// 魅惑魔女
			HeroCardElfLily = 33;                  	// 精灵莉莉
	}
	
	public interface HeroAttribute {
		public static final int
			HeroAttributeStrength = 1,			// 力量型
			HeroAttributeAgility = 2,			// 敏捷型
			HeroAttributeIntelligence = 3;		// 智力型
	}
	
	
	private static NSArray cardList;
	static {
		try {
			cardList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_HERO_CARD_LIST));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int cardCount() {
		return cardList.array().length;
	}
	
	private int attribute;
	private int hpLimit;			// Health Point
	private int spLimit;			// Skill Point
	private int handSizeLimit;		// 手牌上限
	private int price;
	private List<Integer> skillIds = new ArrayList<Integer>();
	
	public HeroCard(Integer cardId) {
		super(cardId);		
		NSDictionary cardInfo = (NSDictionary)cardList.array()[cardId];
		cardName = cardInfo.get(kCardName).toString();
		cardText = cardInfo.get(kCardText).toString();
		
		attribute = cardInfo.get(kAttribute).toInteger();
		hpLimit = cardInfo.get(kHpLimit).toInteger();
		spLimit = cardInfo.get(kSpLimit).toInteger();
		handSizeLimit = cardInfo.get(kHandSizeLimit).toInteger();
		
		List<NSObject> skills = cardInfo.get(kSkillIds).toList();
		for (NSObject skill : skills) {
			skillIds.add(skill.toInteger());
		}
		
		price = cardInfo.get(kPrice).toInteger();
	}
	
	/**
	 * Getter method
	 */	
	public int getAttribute() {
		return attribute;
	}
	public int getHpLimit() {
		return hpLimit;
	}
	public int getSpLimit() {
		return spLimit;
	}
	public int getHandSizeLimit() {
		return handSizeLimit;
	}
	public List<Integer> getSkillIds() {
		return skillIds;
	}
	public int getPrice() {
		return price;
	}
	public boolean isFree() {
		return (price == 0);
	}
	
	public boolean isStrength() {
		return (HeroAttribute.HeroAttributeStrength == attribute);
	}
	public boolean isAgility() {
		return (HeroAttribute.HeroAttributeAgility == attribute);
	}
	public boolean isIntelligence() {
		return (HeroAttribute.HeroAttributeIntelligence == attribute);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HeroCard [cardName=");
		builder.append(cardName);
		builder.append(", cardText=");
		builder.append(cardText);
		builder.append(", hpLimit=");
		builder.append(hpLimit);
		builder.append(", spLimit=");
		builder.append(spLimit);
		builder.append(", handSizeLimit=");
		builder.append(handSizeLimit);
		builder.append(", skillIds=");
		builder.append(skillIds);
		builder.append("]");
		return builder.toString();
	}
	
}
