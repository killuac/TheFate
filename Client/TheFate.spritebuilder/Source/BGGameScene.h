//
//  BGGameScene.h
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGClient.h"
#import "BGFateCard.h"
#import "BGFaction.h"
#import "BGTable.h"
#import "BGPlayer.h"
#import "BGAlertPopup.h"
#import "BGChatPopup.h"
#import "BGHistoryPopup.h"
#import "BGGameSetting.h"

typedef NS_ENUM(NSUInteger, BGGameState) {
    GameStateNull = 0,
    GameStateHeroChoosing = 1,      // 游戏开始阶段-选择英雄
    GameStateComparing = 2,         // 拼点阶段
    GameStateTurnStarting = 3,      // 回合开始阶段
    GameStatePlaying = 4,           // 主动出牌阶段
    GameStateCardChoosing = 5,      // 被动出牌阶段
    GameStateBoolChoosing = 6,      // 选择发动技能(英雄/装备)
    GameStateTargetChoosing = 7,    // 指定目标阶段
    GameStateColorChoosing = 8,     // 选择颜色阶段
    GameStateSuitsChoosing = 9,     // 选择花色阶段
    GameStateGetting = 10,          // 抽牌阶段
    GameStateGiving = 11,           // 给牌阶段
    GameStateRemoving = 12,         // 拆牌阶段
    GameStateAssigning = 13,        // 分牌阶段
    GameStateDropping = 14,         // 弃/弃置卡牌
    GameStateDiscarding = 15,       // 弃牌阶段
    GameStatePlayerDying = 16,      // 濒死阶段
    GameStateWaitingDispel = 17,    // 等待驱散
    GameStateDeathResolving = 18    // 死亡结算阶段
};

@interface BGGameScene : CCNode <BGPopupDelegate> {
    CCSprite *_fateFrame;
    CCLayoutBox *_factionLayoutBox;
    CCLabelTTF *_deckCardCountLabel;
    CCSprite *_leftArrow, *_rightArrow;
    BGTable *_table;
    
    CCButton *_backButton;
    CCLabelTTF *_textPrompt;
}

@property (nonatomic, strong, readonly) CCEffectNode *effectNode;

@property (nonatomic) NSUInteger playTime;
@property (nonatomic) BOOL isNoChatting;

@property (nonatomic) NSInteger fateCardId;
@property (nonatomic, strong) NSArray *allPlayerNames;  // [0] is self player name
@property (nonatomic, strong) NSArray *allRoleIds;      // [0] is self player's role
@property (nonatomic, strong) NSArray *allHeroIds;      // [0] is selected by self user

@property (nonatomic) BGAction action;
@property (nonatomic) BGGameState state;

@property (nonatomic, strong, readonly) BGTable *table;
@property (nonatomic, strong, readonly) NSMutableArray *allPlayers;
@property (nonatomic, strong, readonly) NSArray *alivePlayers;
@property (nonatomic, strong, readonly) BGPlayer *selfPlayer;       // 玩家自己
@property (nonatomic, strong, readonly) BGPlayer *previosPlayer;    // 上家
@property (nonatomic, strong, readonly) BGPlayer *nextPlayer;       // 下家
@property (nonatomic, strong, readonly) BGPlayer *turnOwner;        // 回合开始的玩家
@property (nonatomic, strong, readonly) BGPlayer *damageSource;     // 伤害来源的玩家
@property (nonatomic, strong, readonly) BGPlayer *activePlayer;     // 当前出牌的玩家
@property (nonatomic, strong, readonly) BGPlayer *preActivePlayer;
@property (nonatomic, strong) BGPlayer *damagedPlayer;
@property (nonatomic, strong) BGPlayer *dyingPlayer;
@property (nonatomic, copy) NSString *turnOwnerName;
@property (nonatomic, copy) NSString *damageSourceName;
@property (nonatomic, copy) NSString *activePlayerName;

@property (nonatomic) NSUInteger deckCardCount;
@property (nonatomic, readonly) NSUInteger playerCount;
@property (nonatomic, readonly) NSUInteger alivePlayerCount;
@property (nonatomic, readonly) BOOL isRoleMode;
@property (nonatomic) BOOL isWaitingDispel;

@property (nonatomic, strong, readonly) NSArray *tipParameters;
@property (nonatomic, strong, readonly) NSDictionary *allGameHistories;
@property (nonatomic, strong, readonly) BGChatPopup *chatPopup;
@property (nonatomic, strong, readonly) BGHistoryPopup *historyPopup;


- (void)renderAllPlayers:(NSArray *)playerNames withRoleIds:(NSArray *)roleIds;
- (BGPlayer *)playerWithName:(NSString *)playerName;
- (BGPlayer *)playerWithSelectedHeroId:(NSInteger)heroId;
- (void)updateFactionWithRoleId:(NSInteger)roleId;

- (void)renderAllPlayersRoleWithRoleIds:(NSArray *)roleIds;
- (void)renderOtherPlayersHeroWithHeroIds:(NSArray *)heroIds;
- (void)addProgressBarForOtherPlayers;
- (void)removeProgressBarForOtherPlayers;
- (void)enablePlayerAreaForOtherPlayers;
- (void)disablePlayerAreaForAllPlayers;

- (void)makeBackgroundToDark;
- (void)makeBackgroundToNormal;
- (void)setArrowVisible:(BOOL)isVisible;

- (void)moveCard:(CCNode *)cardNode toPlayer:(BGPlayer *)player;
- (void)moveCards:(NSArray *)cardNodes toPlayer:(BGPlayer *)player;

- (void)addHistory;
- (void)addHistoryOfPlayerWithEsObject:(EsObject *)esObj;
- (void)showMessage:(NSString *)message ofUserName:(NSString *)userName;

- (id)showPopupWith:(NSString*)file;
- (void)resetValueAfterResolved;

- (id)backToRoomScene;
- (void)backToZoneScene;
- (void)exitGame:(CCButton *)sender;

@end
