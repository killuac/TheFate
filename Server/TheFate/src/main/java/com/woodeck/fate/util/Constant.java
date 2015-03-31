/**
 * 
 */
package com.woodeck.fate.util;

/**
 * @author Killua
 *
 */
public interface Constant {
	
	public static final int EXTRA_WAITING_TIME = 5;		// Extra waiting for client(The time not sync with client, don't know why)
	public static final int DEFAULT_PLAY_TIME = 15;		// Default play time for player
	public static final int DEFAULT_AI_PLAY_TIME = 3;	// Default play time for AI
	public static final int DEFAULT_HERO_SELECTION_TIME = 30;
	public static final int DEFAULT_CARD_ASSIGNING_TIME = 45;
	public static final int DEFAULT_DELAY_SHORT_TIME = 1000;	// Delay time (e.g. Chakra)
	public static final int DEFAULT_DELAY_LONG_TIME = 2500;		// Delay time (e.g. Greed)
	public static final int WAITING_PLAYER_TIME = 15000;		// Waiting time for player join
	
	public static final int MAX_CANDIDATE_HERO_COUNT = 6;
	public static final byte MAX_PLAYER_LEVEL = 60;
	
	public static final int DEFAULT_CANDIDATE_HERO_COUNT = 3;
	public static final int DEFAULT_HAND_CARD_COUNT = 5;
	public static final int DEFAULT_SELECTABLE_CARD_COUNT = 1;
	public static final int DEFAULT_DRAW_CARD_COUNT = 2;
	public static final int DEFAULT_OWN_EQUIPMENT_COUNT = 2;
	public static final int KILL_REWARD_DRAW_CARD_COUNT = 2;
	
	public static final int DEFAULT_ATTACK_DAMAGE = 1;			// 默认攻击造成的伤害
	public static final int DEFAULT_ATTACK_LIMIT = 1;			// 默认最大攻击次数
	public static final int DEFAULT_ATTACK_RANGE = 1;
	
	public static final String AI_PLAYER_NAME_PREFIX = "AI Player";
	public static final String GUEST_USER_NAME_PREFIX = "Guest";
	
	public static final String REPLACE_SIGN = "&";		// 历史记录文本中的替换符
	public static final String DELIMITER_SIGN = "、";	// 多个目标英雄/多张卡牌的分隔符
	public static final String HERO_NAME_SELF = "自己";	// 目标是自己
	
}
