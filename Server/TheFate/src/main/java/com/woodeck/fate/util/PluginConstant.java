/**
 * 
 */
package com.woodeck.fate.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.plist.NSDictionary;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

/**
 * @author Killua
 *
 */

public interface PluginConstant {
	
	public static final String
		GAMETYPE_NEWBIE			= "FateNewbie",
		GAMETYPE_VERSUS			= "FateVersus",
		GAMETYPE_JUNIOR_SIX		= "FateJuniorSix",
		GAMETYPE_SENIOR_SIX		= "FateSeniorSix",
		GAMETYPE_JUNIOR_EIGHT	= "FateJuniorEight",
		GAMETYPE_SENIOR_EIGHT	= "FateSeniorEight",
		EXTENSION_NAME			= "TheFate",
		PLUGIN_USER				= "UserPlugin",
		PLUGIN_ZONE				= "ZonePlugin",
		PLUGIN_ROOM 			= "RoomPlugin",
		PLUGIN_GAME 			= "GamePlugin",
		PLUGIN_STORE			= "StorePlugin";
	
//	Actions: If add new action, also need add it to ActionText.plist file for debug log
	public static final String kAction = "action";		// 标识要做什么事情
	public static final String kGameState = "gameState";
	public enum Action {
		ActionNull (-1),                                // 如果有玩家作弊，收到一个无效Action使游戏Crash
		ActionLoginWithUser (0),						// 登录游戏
//		ActionLoginAsGuest (1),							// 游客登录
		ActionRejoinRoom (2),							// 断开连接后重新加入房间
		ActionFindRoom (3),								// 查找房间
		
		ActionStartGame (11),                           // 开始游戏
		ActionStartRound (12),                          // 开始牌局
		ActionStartTurn (13),							// 回合开始阶段
		ActionDrawCard (14), 							// 摸牌阶段
		ActionPlayCard (15),                            // 出牌阶段
		ActionWaitingDispel (16),                     	// 等待驱散
		ActionCancelWaiting (17),                     	// 取消等待
		ActionDiscardCard (18),                         // 弃牌阶段
		ActionClearHeroSkill (19),						// 清空英雄技能ID(为了正确的显示文本提示)
		ActionClearEquipment (20),						// 清空触发的装备ID(为了正确的显示文本提示)
		ActionGameOver (21),							// 游戏结束
		ActionRejoinGame (22),							// 重新加入游戏
		ActionExitGame (23),							// 退出游戏
		ActionTurnEnd (24),								// 回合结束阶段
		ActionShowAllPlayersRole (25),					// 拼点后显示所有玩家身份(2V2)
		
		ActionSelectHero (100),                         // 选择了英雄
		ActionUseHandCard (101),                        // 使用卡牌
		ActionUseHeroSkill (102),                       // 使用英雄技能
		ActionUseEquipment (103),                       // 发动装备技能
		ActionCancelHeroSkill (104),                    // 取消英雄技能
		ActionCancelEquipment (105),                    // 取消使用装备
		ActionStartDiscard (106),                       // 开始弃牌
		ActionOkayToDiscard (107),                      // 确定弃牌

		ActionChoseCardToCompare (201),                 // 选择了卡牌: 拼点
		ActionChoseCardToUse (202),                     // 选择了卡牌Id/Idx: 使用
		ActionChoseCardToGet (203),                     // 选择了目标卡牌: 抽取获得
		ActionChoseCardToGive (204),                    // 选择了卡牌: 交给其他玩家
		ActionChoseCardToRemove (205),                  // 选择了卡牌: 移除(弃掉)
		ActionChoseCardToDrop (206),                 	// 选择了卡牌: 弃/弃置
		ActionChoseColor (207),                         // 选择了卡牌颜色
		ActionChoseSuits (208),                         // 选择了卡牌花色
		ActionAssignedCard (209),                       // 分配了卡牌(如能量转移)
		ActionOkay (210),                               // 选择了确定
		ActionCancel (211),                             // 选择了取消
		ActionChoseTargetPlayer (212),					// 指定了目标玩家
		ActionChoseViewPlayerRole (213),				// 选择了查看玩家身份

		ActionChooseCardToCompare (301),                // 选择卡牌: 拼点
		ActionChooseCardToUse (302),                    // 选择卡牌: 使用(被动)
		ActionChooseCardToGet (303),                    // 选择目标卡牌: 抽取获得
		ActionChooseCardToGive (304),                   // 选择卡牌: 交给其他玩家
		ActionChooseCardToRemove (305),                 // 选择卡牌: 移除(弃掉)
		ActionChooseCardToDrop (306),                	// 选择卡牌: 弃/弃置
		ActionChooseColor (307),                        // 选择颜色阶段
		ActionChooseSuits (308),                        // 选择花色阶段
		ActionChooseCardToAssign (309),                 // 选择卡牌: 分配给每个玩家
		ActionChooseOkayOrCancel (310),                 // 选择是否发动英雄或装备技能
		ActionChooseTargetPlayer (311),					// 指定目标玩家
		ActionChooseDrawCardOrViewRole (312),			// 选择摸2张牌/查看玩家身份
		
		ActionTableDeckCardCount (400),					// 牌堆剩余牌数
//		ActionTableDealRoleCard (401),             		// 发身份牌
		ActionTableDealCandidateHero (402),             // 发待选英雄
		ActionTableShowAllSelectedHeros (403),          // 显示所有玩家选中的英雄
		ActionTableShowAllComparedCards (404),          // 显示所有玩家用于拼点的牌
		ActionTableFaceDownCardFromDeck (405),          // 暗置的牌堆顶的牌到桌面
		ActionTableRevealOneCardFromDeck (406),         // 显示判定牌
		ActionTableFaceUpTheCard (407),                 // 将桌上暗置的牌明置
		ActionTableShowPlayerAllCards (408),            // 显示目标玩家手牌(暗置)和装备
		ActionTableShowPlayerHandCard (409),            // 显示目标玩家手牌
		ActionTableShowPlayerEquipment (410),           // 显示目标玩家装备
		ActionTableShowAssignedCard (411),              // 显示待分配的牌(比如能量转移)
		
		ActionPlayerSelectedHero (500),                 // 选中的英雄
		ActionPlayerDealHandCard (501),                 // 发初始手牌
		ActionPlayerUpdateHero (502),                   // 更新英雄的血量/怒气等信息
		ActionPlayerUpdateHandCard (503),               // 更新玩家手牌
		ActionPlayerUpdateEquipment (504),              // 更新玩家装备区的牌
		ActionPlayerGetCardFromTable (505),             // 获得牌桌上的牌
		ActionPlayerSkillOrEquipmentTriggered (506),	// 技能或装备被触发(锁定技)
		ActionPlayerIsDying (507),						// 玩家濒死
		ActionPlayerIsDead (508),						// 玩家挂了
		ActionPlayerIsEscaped (509),					// 玩家逃跑
		ActionPlayerShowRoleCard (510),					// 显示被查看的玩家身份
		
		ActionShowMerchandises (1000),					// 商品信息
		ActionShowPickingHeros (1001),					// 可挑选的英雄
		ActionBuyGoldCoin (1002),						// 购买金币
		ActionBuyHeroCard (1003),						// 购买英雄卡
		ActionBuyVIPCard (1004),						// VIP会员
		ActionBuyAdditionalCandidates (1005),			// 购买额外的待选英雄
		ActionPickOneHero (1006),						// 点选英雄
		ActionChangeRandomHeros (1007),					// 更换随机的待选英雄
		
		ActionFeedback (2000);							// 反馈
		
	    public final int id;
	    public final String text;
	    private static Map<Integer, Action> map;
	    
	    private Action(int id) {
	    	this.id = id;
//	    	Every action text must exist in file ActonText.plist
	    	this.text = sharedActionText().get(Integer.toString(id)).toString();
	    	registerEnum(this);
	    }
	    
	    private static void registerEnum(Action action) {
//	    	Must check, otherwise, create new instance for every action enum.
	    	if (null == map)
	    		map = new HashMap<Integer, Action>();
	    	map.put(action.id, action);
	    }
	    
	    public static Action getEnumById(int key) {
	    	return map.get(key);
	    }
	    
		private static NSDictionary actionText;
		
		private static NSDictionary sharedActionText() {
			if (null == actionText) {
				try {
					actionText = (NSDictionary)PropertyListParser.parse(new File(FileConstant.PLIST_ACTION_TEXT));
					
				} catch (PropertyListException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return actionText;
		}
	}
	
//	Room find result
	public static final String kRoomFindResult = "roomFindResult";
	public enum RoomFindResult {
		RoomFindResultFound,
		RoomFindResultNotFound,
		RoomFindResultWrongPassword;
	}
	
//	Update reasons
	public static final String kPlayerUpdateReason = "updateReason";	// 玩家手牌更新原因(为客户端播放动画使用)
	public enum UpdateReason {
		UpdateReasonDefault (0),	// Client不播放动画
		UpdateReasonTable (1),		// 获得或失去手牌(从/到)桌面
		UpdateReasonPlayer (2),		// 获得或失去手牌(从/到)目标玩家
		UpdateReasonRejoin (3);		// 重新加入游戏后从Server获取手牌信息
		
		public final int id;
		private UpdateReason(int id) {
			this.id = id;
		}
	}
	
//	Parameters
	public static final String
		kParamGameType					= "gameType",				// 游戏类型
		kParamIsRoleMode				= "isRoleMode",				// 是否是身份局
		kParamZoneId					= "zoneId",					// 区ID
		kParamRoomId					= "roomId",					// 房间ID
		kParamHeroIsPicked				= "heroIsPicked",			// 英雄以被房间的其他玩家点选
		
		kParamPlayerCount               = "playerCount",        	// 玩家数(包括AI)
	    kParamAllPlayerNames            = "allPlayerNames",     	// 所有玩家列表
	    kParamTurnOwnerName				= "turnOwnerName",			// 回合开始的玩家
	    kParamDamageSourceName			= "damageSourceName",		// 伤害来源(默认回合开始的玩家)
	    kParamDeckCardCount             = "deckCardCount",      	// 牌堆剩余牌数
	    kParamCardId					= "cardId",					// 宿命/身份牌ID
	    kParamTargetPlayerNames         = "targetPlayerNames",		// 目标玩家列表
	    kParamCardIdList                = "idList",             	// 卡牌列表(英雄牌/摸的牌/获得的牌/使用的牌/弃置的牌)
	    kParamEquipmentIdList        	= "equipmentIdList",		// 已装备的卡牌列表
	    kParamAvailableCardIdList       = "availableCardIdList",    // 可以选择使用的卡牌
	    kParamCardIndexList             = "indexList",          	// 选中的哪几张牌
	    kParamAvailableSkillIdList      = "availableSkillIdList",	// 可以选择使用的英雄技能
	    kParamAvailableEquipIdList      = "availableEquipIdList", 	// 可以选择使用的装备
	    kParamDroppableEquipIdList      = "droppableEquipIdList", 	// 可以弃的装备(发动技能的时候)
	    kParamMaxFigureCardId           = "maxCardId",          	// 最大点数的卡牌
	    kParamCardCount                 = "cardCount",          	// 卡牌数(抽取/获得/分配/失去)
	    kParamHandCardCount             = "handCardCount",      	// 玩家手牌数量
	    kParamSelectableCardCount		= "selectableCount",   		// 可选择的卡牌数量
	    kParamRequiredSelCardCount		= "requiredSelCount",		// 必须选中的牌数
	    kParamRequiredTargetCount       = "requiredTarCount",   	// 必须指定的目标玩家数
	    kParamMaxTargetCount			= "maxTargetCount",			// 最多指定的目标玩家数
	    kParamSelectedHeroId            = "selHeroId",          	// 选中的英雄/购买的英雄
	    kParamSelectedColor             = "selColor",           	// 选中的颜色
	    kParamSelectedSuits             = "selSuits",           	// 选中的花色
		kParamHeroSkillId           	= "heroSkillId",        	// 使用/触发的英雄技能
	    kParamEquipmentId       		= "equipmentId",        	// 使用/触发的装备
	    kParamTransformedCardId			= "transformedCardId",		// 转化后的卡牌ID
	    kParamIsPickedHero				= "isPickedHero",			// 是否点选了英雄
	    kParamIsStrengthening			= "isStrengthening",		// 是否强化阶段
	    kParamIsStrengthened            = "isStrengthened",     	// 是否选择了强化
	    kParamIsGreeding                = "isGreeding",         	// 是否贪婪结算阶段
	    kParamIsWildAxing				= "isWildAxing",			// 是否野性之斧选目标阶段
	    kParamIsRequiredDrop         	= "isRequiredDrop",			// 是否强制的弃置
	    kParamIsRequiredTarget			= "isRequiredTarget",		// 是否必须指定1个目标
	    kParamIsNoNeedCard				= "isNoNeedCard",			// 是否不需要手牌发动技能
//	    kParamIsIgnoreDispel            = "isIgnoreDispel",     	// 是否忽略驱散
	    kParamIsNewWaiting				= "isNewWaiting",			// 是否开始新的等待计时
	    kParamIsDyingTriggered			= "isDyingTriggered",		// 技能是否由濒死触发的
	    kParamIsSunder					= "isSunder",				// 是否使用了灵魂隔断
	    kParamIsFirstBlood				= "isFirstBlood",			// 头血
	    kParamHeroHealthPoint           = "hp",                 	// 血量值
	    kParamHeroSkillPoint            = "sp",                 	// 怒气值
	    kParamHeroHpChanged				= "hpChanged",				// 血量变化值(+/-)
	    kParamHeroSpChanged				= "spChanged",				// 怒气变化值(+/-)
		kParamPlusDistance          	= "plusDistance",   		// +1
		kParamMinusDistance          	= "minusDistance",   		// -1
		kParamAttackRange               = "attackRange",        	// 攻击范围
		
		kParamHistoryDate				= "historyDate",			// 历史记录时间
		kParamHistoryText				= "historyText",			// 历史记录文本
		
//		Game Result
		kParamVictoryResults			= "victoryResults",			// 胜利结果
		kParamFailureResults			= "failureResults",			// 失败结果
		kParamIsAlive					= "isAlive",				// 存活状态
		kParamIsEscaped					= "isEscaped",				// 是否逃跑
		kParamKillEnemyCount			= "killEnemyCount",			// 每局杀敌数
		kParamDoubleKillCount			= "doubleKillCount",		// 每局总双杀次数
		kParamTripleKillCount			= "tripleKillCount",		// 每局总三杀次数
		kParamGotExpPoint				= "gotExpPoint",			// 获得/扣除的经验值
		kParamAddExpPoint				= "addExpPoint",			// 额外获得的经验(VIP)
		kParamRewardGoldCoin			= "rewardGoldCoin",			// 奖励的金币
	
//		Feedback
		kParamIssueTitle				= "issueTitle",				// 标题
		kParamIssueContent				= "issueContent";			// 内容
	
}
