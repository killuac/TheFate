/**
 * 
 */
package com.woodeck.fate.util;

/**
 * @author Killua
 *
 */
public interface FileConstant {	
	
//	public static final String PATH = "src/main/resources/";	// For "main" test
	public static final String PATH = "extensions/TheFate/resources/";
	
	public static final String
		PLIST_PLAYING_CARDIDS	= PATH + "card/PlayingCardIds.plist",
		PLIST_PLAYING_CARD_LIST = PATH + "card/PlayingCardList.plist",
		PLIST_HERO_SKILL_LIST   = PATH + "card/HeroSkillList.plist",
		PLIST_HERO_CARD_LIST    = PATH + "card/HeroCardList.plist",
		PLIST_FATE_CARD_LIST 	= PATH + "card/FateCardList.plist",
		PLIST_ROLE_CARD_LIST    = PATH + "card/RoleCardList.plist";
	
	public static final String PLIST_ACTION_TEXT = PATH + "ActionText.plist";
	public static final String PLIST_ACTION_HISTORY = PATH + "ActionHistory.plist";
	
	public static final String
		PLIST_MERCHANDISE		= PATH + "Merchandise.plist",
		PLIST_REWARD			= PATH + "Reward.plist";
	
}
