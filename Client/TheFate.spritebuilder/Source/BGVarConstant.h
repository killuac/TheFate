//
//  BGVarConstant.h
//  FateClient
//
//  Created by Killua Liu on 3/1/14.
//
//

#ifndef FateClient_BGVarConstant_h
#define FateClient_BGVarConstant_h

// Variable names
#define kVarRoomSetting     @"roomSetting"      // 房间设置(playTime/isChattable)
#define kVarUserInfo        @"userInfo"         // 用户信息(name/avatar/level)
#define kVarUserStatus		@"userStatus"		// 用户状态(isReady/isRoomOwner)
#define kVarPickedHero		@"pickedHero"		// 点选的英雄

// roomSetting
#define kParamRoomNumber            @"roomNumber"           // 房间号码
#define kParamRoomPassword          @"roomPassword"         // 房间密码
#define kParamPlayTime              @"playTime"             // 出牌时间
#define kParamIsNoChatting          @"isNoChatting"         // 是否禁聊

// userInfo
#define kParamUserName              @"userName"             // 玩家名
#define kParamNickName				@"nickName"             // 昵称
#define kParamUserAvatar			@"userAvatar"           // 用户头像
#define kParamIsVIP					@"isVIP"				// VIP会员
#define kParamDiscount				@"discount"             // 折扣
#define kParamAddEpRate				@"addEpRate"			// 额外获得的经验(%)
#define kParamVIPValidTime			@"vipValidTime"         // 会员有效期
#define kParamUserLevel				@"userLevel"			// 用户等级
#define kParamGoldCoin				@"goldCoin"             // 总金币数
#define kParamExpPoint				@"expPoint"             // 总经验值
#define kParamLevelUpExp			@"levelUpExp"			// 升级所需的经验值
#define kParamSumKillEnemyCount		@"sumKillEnemyCount"	// 总杀敌数
#define kParamSumDoubleKillCount	@"sumDoubleKillCount"	// 总双杀次数
#define kParamSumTripleKillCount	@"sumTripleKillCount"	// 总三杀次数
#define kParamVictoryCount			@"victoryCount"         // 胜利次数
#define kParamFailureCount			@"failureCount"         // 失败次数
#define kParamEscapeCount			@"escapeCount"          // 逃跑次数
#define kParamWinRate				@"winRate"              // 平均胜率
#define kParamSentinelWinRate		@"sentinelWinRate"      // 近卫胜率
#define kParamScourgeWinRate		@"scourgeWinRate"       // 天灾胜率
#define kParamNeutralWinRate		@"neutralWinRate"		// 中立胜率
#define kParamEscapeRate            @"escapeRate"           // 逃跑率

#define kParamPickedHeroId			@"pickedHeroId"         // 点选的英雄ID
#define kParamOwnHeroIds			@"ownHeroIds"			// 拥有的英雄ID(购买的或VIP免费使用的)

// userStatus
#define kParamIsReady				@"isReady"              // 是否准备开始
#define kParamIsRoomOwner			@"isRoomOwner"          // 是否是房主

#endif
