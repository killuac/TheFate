//
//  PluginConstant.h
//  FateClient
//
//  Created by Killua Liu on 6/13/13.
//
//

#ifndef FateClient_PluginConstant_h
#define FateClient_PluginConstant_h

// GameType/Extension/Plugin name and handle
#define GAMETYPE_NEWBIE         @"FateNewbie"
#define GAMETYPE_VERSUS         @"FateVersus"
#define GAMETYPE_JUNIOR_SIX     @"FateJuniorSix"
#define GAMETYPE_SENIOR_SIX     @"FateSeniorSix"
#define GAMETYPE_JUNIOR_EIGHT   @"FateJuniorEight"
#define GAMETYPE_SENIOR_EIGHT   @"FateSeniorEight"
#define EXTENSION_NAME          @"TheFate"
#define PLUGIN_STORE            @"StorePlugin"
#define PLUGIN_ZONE             @"ZonePlugin"
#define PLUGIN_ROOM             @"RoomPlugin"
#define PLUGIN_GAME             @"GamePlugin"

// Actions
#define kAction         @"action"       // 标识要做什么事情
#define kGameState      @"gameState"
typedef NS_ENUM(NSInteger, BGAction) {
    ActionNull = -1,                                // 如果有玩家作弊，收到一个无效Action使游戏Crash
    ActionLoginWithUser = 0,						// 登录游戏
//  ActionLoginAsGuest = 1,                         // 游客登录
    ActionRejoinRoom = 2,                           // 断开连接后重新加入房间
    ActionFindRoom = 3,								// 查找房间
    
    ActionStartGame = 11,                           // 开始游戏
    ActionStartRound = 12,                          // 开始牌局
    ActionStartTurn = 13,							// 回合开始阶段
    ActionDrawCard = 14, 							// 摸牌阶段
    ActionPlayCard = 15,                            // 出牌阶段
    ActionWaitingDispel = 16,                     	// 等待驱散
    ActionCancelWaiting = 17,                     	// 取消等待
    ActionDiscardCard = 18,                         // 弃牌阶段
    ActionClearHeroSkill = 19,                      // 清空英雄技能ID(为了正确的显示文本提示)
    ActionClearEquipment = 20,                      // 清空触发的装备ID(为了正确的显示文本提示)
    ActionGameOver = 21,                            // 游戏结束
    ActionRejoinGame = 22,                          // 重新加入游戏
    ActionExitGame = 23,                            // 退出游戏
    ActionTurnEnd = 24,								// 回合结束阶段
    ActionShowAllPlayersRole = 25,					// 拼点后显示所有玩家身份(2V2)
    
    ActionSelectHero = 100,                         // 选择了英雄
    ActionUseHandCard = 101,                        // 使用卡牌
    ActionUseHeroSkill = 102,                       // 使用英雄技能
    ActionUseEquipment = 103,                       // 发动装备技能
    ActionCancelHeroSkill = 104,                    // 取消英雄技能
    ActionCancelEquipment = 105,                    // 取消使用装备
    ActionStartDiscard = 106,                       // 开始弃牌
    ActionOkayToDiscard = 107,                      // 确定弃牌
    
    ActionChoseCardToCompare = 201,                 // 选择了卡牌: 拼点
    ActionChoseCardToUse = 202,                     // 选择了卡牌Id/Idx: 使用
    ActionChoseCardToGet = 203,                     // 选择了目标卡牌: 抽取获得
    ActionChoseCardToGive = 204,                    // 选择了卡牌: 交给其他玩家
    ActionChoseCardToRemove = 205,                  // 选择了卡牌: 移除(弃掉)
    ActionChoseCardToDrop = 206,                    // 选择了卡牌: 弃置
    ActionChoseColor = 207,                         // 选择了卡牌颜色
    ActionChoseSuits = 208,                         // 选择了卡牌花色
    ActionAssignedCard = 209,                       // 分配了卡牌(如能量转移)
    ActionOkay = 210,                               // 选择了确定
    ActionCancel = 211,                             // 选择了取消
    ActionChoseTargetPlayer = 212,					// 指定了目标玩家
    ActionChoseViewPlayerRole = 213,                // 选择了查看玩家身份
    
    ActionChooseCardToCompare = 301,                // 选择卡牌: 拼点
    ActionChooseCardToUse = 302,                    // 选择卡牌: 使用(被动)
    ActionChooseCardToGet = 303,                    // 选择目标卡牌: 抽取获得
    ActionChooseCardToGive = 304,                   // 选择卡牌: 交给其他玩家
    ActionChooseCardToRemove = 305,                 // 选择卡牌: 移除(弃掉)
    ActionChooseCardToDrop = 306,                   // 选择卡牌: 弃/弃置
    ActionChooseColor = 307,                        // 选择颜色阶段
    ActionChooseSuits = 308,                        // 选择花色阶段
    ActionChooseCardToAssign = 309,                 // 选择卡牌: 分配给每个玩家
    ActionChooseOkayOrCancel = 310,                 // 选择是否发动英雄或装备技能
    ActionChooseTargetPlayer = 311,					// 指定目标玩家
    ActionChooseDrawCardOrViewRole = 312,           // 选择查看玩家身份/摸2张牌
    
    ActionTableDeckCardCount = 400,                 // 牌堆剩余牌数
    ActionTableDealRoleCard = 401,                  // 发身份牌
    ActionTableDealCandidateHero = 402,             // 发待选英雄
    ActionTableShowAllSelectedHeros = 403,          // 显示所有玩家选中的英雄
    ActionTableShowAllComparedCards = 404,          // 显示所有玩家用于拼点的牌
    ActionTableFaceDownCardFromDeck = 405,          // 暗置的牌堆顶的牌到桌面
    ActionTableRevealOneCardFromDeck = 406,         // 显示判定牌
    ActionTableFaceUpTheCard = 407,                 // 将桌上暗置的牌明置
    ActionTableShowPlayerAllCards = 408,            // 显示目标玩家手牌(暗置)和装备
    ActionTableShowPlayerHandCard = 409,            // 显示目标玩家手牌
    ActionTableShowPlayerEquipment = 410,           // 显示目标玩家装备
    ActionTableShowAssignedCard = 411,              // 显示待分配的牌(比如能量转移)
    
    ActionPlayerSelectedHero = 500,                 // 选中的英雄
    ActionPlayerDealHandCard = 501,                 // 发初始手牌
    ActionPlayerUpdateHero = 502,                   // 更新英雄的血量/怒气等信息
    ActionPlayerUpdateHandCard = 503,               // 更新玩家手牌
    ActionPlayerUpdateEquipment = 504,              // 更新玩家装备区的牌
    ActionPlayerGetCardFromTable = 505,             // 获得牌桌上的牌
    ActionPlayerSkillOrEquipmentTriggered = 506,    // 技能或装备被触发(锁定技)
    ActionPlayerIsDying = 507,                      // 玩家濒死
    ActionPlayerIsDead = 508,                       // 玩家挂了
    ActionPlayerIsEscaped = 509,                    // 玩家逃跑
    ActionPlayerShowRoleCard = 510,                 // 显示被查看的玩家身份
    
    ActionShowMerchandises = 1000,                  // 商品信息
    ActionShowPickingHeros = 1001,                  // 可挑选的英雄
    ActionBuyGoldCoin = 1002,						// 购买金币
    ActionBuyHeroCard = 1003,						// 购买英雄卡
    ActionBuyVIPCard = 1004,						// VIP会员
    ActionBuyAdditionalCandidates = 1005,			// 购买额外的待选英雄
    ActionPickOneHero = 1006,						// 点选英雄
    ActionChangeRandomHeros = 1007,					// 更换随机的待选英雄
    
    ActionFeedback = 2000                           // 反馈
};

// Room find result
#define kRoomFindResult         @"roomFindResult"
typedef NS_ENUM(NSInteger, BGRoomFindResult) {
    RoomFindResultFound,
    RoomFindResultNotFound,
    RoomFindResultWrongPassword
};

// Update reasons
#define kPlayerUpdateReason     @"updateReason"     // 玩家手牌更新原因
typedef NS_ENUM(NSInteger, BGUpdateReason) {
    UpdateReasonDefault = 0,                        // 不播放动画
    UpdateReasonTable = 1,							// 获得或失去手牌(从/到)桌面
    UpdateReasonPlayer = 2,                         // 获得或失去手牌(从/到)目标玩家
    UpdateReasonRejoin = 3                          // 重新加入游戏后从Server获取手牌信息
};

// Parameters
#define kParamGameType                  @"gameType"             // 游戏类型
#define kParamZoneId                    @"zoneId"               // 区ID
#define kParamRoomId                    @"roomId"               // 房间ID
#define kParamHeroIsPicked              @"heroIsPicked"         // 英雄以被房间的其他玩家点选

#define kParamPlayerCount               @"playerCount"          // 所有玩家数(包括AI)
#define kParamAllPlayerNames            @"allPlayerNames"       // 所有玩家列表
#define kParamTurnOwnerName             @"turnOwnerName"        // 回合开始的玩家
#define kParamDamageSourceName          @"damageSourceName"     // 伤害来源(默认回合开始的玩家)
#define kParamDeckCardCount             @"deckCardCount"        // 牌堆剩余牌数
#define kParamCardId                    @"cardId"               // 宿命/身份牌ID
#define kParamTargetPlayerNames         @"targetPlayerNames"    // 目标玩家名列表
#define kParamCardIdList                @"idList"               // 卡牌列表(英雄牌/摸的牌/获得的牌/使用的牌/弃置的牌)
#define kParamEquipmentIdList           @"equipmentIdList"      // 已经装备的卡牌列表
#define kParamAvailableCardIdList       @"availableCardIdList"  // 可以选择使用的手牌
#define kParamCardIndexList             @"indexList"            // 选中的哪几张牌
#define kParamAvailableSkillIdList      @"availableSkillIdList" // 可以选择使用的英雄技能
#define kParamAvailableEquipIdList      @"availableEquipIdList" // 可以选择使用的装备
#define kParamDroppableEquipIdList      @"droppableEquipIdList" // 可以弃的装备(发动技能的时候)
#define kParamMaxFigureCardId           @"maxCardId"            // 最大点数的卡牌
#define kParamHandCardCount             @"handCardCount"        // 玩家手牌数量
#define kParamCardCount                 @"cardCount"            // 卡牌数(抽取/获得/分配/失去)
#define kParamSelectableCardCount       @"selectableCount"      // 可选择的卡牌数量
#define kParamRequiredSelCardCount      @"requiredSelCount"     // 必须选中的牌数
#define kParamRequiredTargetCount       @"requiredTarCount"     // 必须指定的目标玩家数
#define kParamMaxTargetCount            @"maxTargetCount"       // 最多指定的目标玩家数
#define kParamSelectedHeroId            @"selHeroId"            // 选中的英雄/购买的英雄
#define kParamSelectedColor             @"selColor"             // 选中的颜色
#define kParamSelectedSuits             @"selSuits"             // 选中的花色
#define kParamHeroSkillId               @"heroSkillId"          // 使用/触发的英雄技能
#define kParamEquipmentId               @"equipmentId"          // 使用/触发的装备
#define kParamIsPickedHero              @"isPickedHero"         // 是否点选了英雄
#define kParamTransformedCardId         @"transformedCardId"    // 技能转化后的卡牌ID
#define kParamIsStrengthening           @"isStrengthening"      // 是否选择强化阶段
#define kParamIsStrengthened            @"isStrengthened"       // 是否被强化
#define kParamIsGreeding				@"isGreeding"           // 是否贪婪结算阶段
#define kParamIsRequiredDrop            @"isRequiredDrop"       // 是否强制的弃置
#define kParamIsRequiredTarget          @"isRequiredTarget"     // 是否必须指定1个目标
#define kParamIsNoNeedCard              @"isNoNeedCard"         // 是否不需要手牌发动技能
//#define kParamIsIgnoreDispel            @"isIgnoreDispel"       // 是否忽略驱散
#define kParamIsNewWaiting              @"isNewWaiting"         // 是否开始新的等待计时
#define kParamIsDyingTriggered          @"isDyingTriggered"     // 技能是否由濒死触发的
#define kParamIsSunder                  @"isSunder"             // 是否使用了灵魂隔断
#define kParamIsFirstBlood              @"isFirstBlood"         // 头血
#define kParamHeroHealthPoint           @"hp"                   // 血量值
#define kParamHeroSkillPoint            @"sp"                   // 怒气值
#define kParamHeroHpChanged             @"hpChanged"            // 血量变化值(+/-)
#define kParamHeroSpChanged             @"spChanged"            // 怒气变化值(+/-)
#define kParamPlusDistance              @"plusDistance"         // +1
#define kParamMinusDistance             @"minusDistance"        // -1
#define kParamAttackRange               @"attackRange"          // 攻击范围

#define kParamHistoryDate				@"historyDate"			// 历史记录时间
#define kParamHistoryText				@"historyText"			// 历史记录文本

// Game Result
#define kParamVictoryResults			@"victoryResults"       // 胜利结果
#define kParamFailureResults			@"failureResults"       // 失败结果
#define kParamIsAlive                   @"isAlive"              // 存活状态
#define kParamIsEscaped                 @"isEscaped"            // 存活状态
#define kParamKillEnemyCount            @"killEnemyCount"		// 每局杀敌数
#define kParamDoubleKillCount           @"doubleKillCount"      // 每局总双杀次数
#define kParamTripleKillCount           @"tripleKillCount"      // 每局总三杀次数
#define kParamGotExpPoint               @"gotExpPoint"          // 获得/扣除的经验值
#define kParamAddExpPoint               @"addExpPoint"          // 额外获得的经验(VIP)
#define kParamRewardGoldCoin            @"rewardGoldCoin"		// 奖励的金币

// Feedback
#define kParamIssueTitle				@"issueTitle"			// 标题
#define kParamIssueContent				@"issueContent"			// 内容

#endif
