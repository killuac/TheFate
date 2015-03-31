/**
 * 
 */
package com.woodeck.fate.util;

/**
 * @author Killua
 *
 */
public interface VarConstant {

	public static final String
		kVarRoomSetting		= "roomSetting",	// 房间设置(playTime/isChattable)
		kVarUserInfo     	= "userInfo",		// 用户信息(name/avatar/level/VIP)
		kVarUserStatus		= "userStatus",		// 用户状态(isReady/isRoomOwner)
		kVarPickedHero		= "pickedHero";		// 点选的英雄
	
	public static final String
//		roomSetting
		kParamRoomNumber			= "roomNumber",			// 房间号码
		kParamRoomPassword			= "roomPassword",		// 房间密码
		kParamPlayTime				= "playTime",			// 出牌时间
		kParamIsNoChatting			= "isNoChatting",		// 是否禁聊
		
		kParamPickedHeroIds			= "pickedHeroIds",		// 记录房间里玩家点选的英雄，防止重复点选
		
//		userInfo
		kParamIsNPC					= "isNPC",				// 是否是NPC
		kParamUserName				= "userName",			// 玩家名
		kParamNickName				= "nickName",			// 昵称
		kParamUserAvatar			= "userAvatar",			// 用户头像
		kParamIsVIP					= "isVIP",				// VIP会员
		kParamDiscount				= "discount",			// 折扣
		kParamAddEpRate				= "addEpRate",			// 额外获得的经验(%)
		kParamVIPValidTime			= "vipValidTime",		// 会员有效期
		kParamUserLevel				= "userLevel",			// 用户等级
		kParamGoldCoin				= "goldCoin",			// 总金币数
		kParamExpPoint				= "expPoint",			// 总经验值
		kParamLevelUpExp			= "levelUpExp",			// 升级所需的经验值
		kParamSumKillEnemyCount		= "sumKillEnemyCount",	// 总杀敌数
		kParamSumDoubleKillCount	= "sumDoubleKillCount",	// 总双杀次数
		kParamSumTripleKillCount	= "sumTripleKillCount",	// 总三杀次数
		kParamVictoryCount			= "victoryCount",		// 胜利次数
		kParamFailureCount			= "failureCount",		// 失败次数
		kParamEscapeCount			= "escapeCount",		// 逃跑次数
		kParamWinRate				= "winRate",			// 平均胜率
		kParamSentinelWinRate		= "sentinelWinRate",	// 近卫胜率
		kParamScourgeWinRate		= "scourgeWinRate",		// 天灾胜率
		kParamNeutralWinRate		= "neutralWinRate",		// 中立胜率
		kParamEscapeRate			= "escapeRate",			// 逃跑率
		kParamOwnHeroIds			= "ownHeroIds",			// 拥有的英雄ID(购买的或VIP免费使用的)
		
		kParamPickedHeroId			= "pickedHeroId",		// 点选的英雄ID
		
//		userStatus
		kParamIsReady				= "isReady",			// 是否准备开始
		kParamIsRoomOwner			= "isRoomOwner";		// 是否是房主
	
}
