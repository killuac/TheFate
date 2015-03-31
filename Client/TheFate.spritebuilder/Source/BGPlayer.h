//
//  BGPlayer.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCharacter.h"
#import "BGEquipment.h"
#import "BGHand.h"
#import "BGHistory.h"
#import "BGClient.h"
#import "BGRoleCard.h"
#import "BGPlayingButton.h"
#import "BGButtonFactory.h"
#import "BGPlayerInformation.h"

@class BGGameScene;

@interface BGPlayer : CCSprite {
    BGCharacter *_character;
    BGEquipment *_equipment;
    BGHand *_handArea;
    
    CCButton *_roleButton;
    CCLayoutBox *_roleBox;
    CCSprite *_handCardCountFrame;
    CCLabelTTF *_nickNameLabel;
    CCLabelTTF *_handCardCountLabel;
    BGPlayingButton *_playingButton;
    
    CCSprite *_helpMeMark;
    CCSprite *_deadMark;
    CCSprite *_escapedMark;
}

@property (nonatomic, strong, readonly) CCButton *roleButton;
@property (nonatomic, strong, readonly) BGPlayingButton *playingButton;
@property (nonatomic, strong, readonly) CCSprite *progressBar;
@property (nonatomic, strong, readonly) CCLabelTTF *textPrompt;
@property (nonatomic, strong, readonly) CCLabelBMFont *textPromptBMFont;

@property (nonatomic, copy) NSString *nickName;
@property (nonatomic, copy) NSString *playerName;
@property (nonatomic, readonly) NSUInteger seatIndex;
@property (nonatomic, readonly) CGPoint centerPosition;
@property (nonatomic, readonly) CGSize bgContentSize;

@property (nonatomic, weak, readonly) BGGameScene *game;
@property (nonatomic, readonly) BOOL isMe;
@property (nonatomic, readonly) BOOL isTurnOwner;
@property (nonatomic, readonly) BOOL isDamageSource;
@property (nonatomic, readonly) BOOL isTarget;

@property (nonatomic, readonly) NSInteger distance;
@property (nonatomic) NSUInteger plusDistance;      // +1: 其他玩家计算与自己的距离
@property (nonatomic) NSInteger minusDistance;      // -1: 自己计算与其他玩家的距离
@property (nonatomic) NSUInteger attackRange;
@property (nonatomic, readonly) BOOL isDying;
@property (nonatomic, readonly) BOOL isDead;
@property (nonatomic, readonly) BOOL isEscaped;

@property (nonatomic, strong, readonly) BGRoleCard *roleCard;
@property (nonatomic, strong, readonly) BGCharacter *character;
@property (nonatomic, strong, readonly) BGEquipment *equipment;
@property (nonatomic, strong, readonly) BGHand *handArea;
@property (nonatomic, copy, readonly) NSString *heroName;

@property (nonatomic, strong, readonly) NSArray *targetPlayers;
@property (nonatomic, strong, readonly) BGPlayer *targetPlayer;
@property (nonatomic, strong, readonly) BGHandCard *firstUsedCard;
@property (nonatomic, strong, readonly) BGHandCard *lastUsedCard;
@property (nonatomic, strong, readonly) NSArray *usedCards;         // Used cards of active player
@property (nonatomic, strong, readonly) BGHeroSkill *heroSkill;
@property (nonatomic, strong, readonly) BGHandCard *equipmentCard;
@property (nonatomic, readonly) BOOL isOkayEnabled;
@property (nonatomic, readonly) BOOL isEquipEquipmentCard;
@property (nonatomic, readonly) BOOL isNeedTargetAgain;             // 是否需要进一步指定左/右边的玩家

@property (nonatomic) NSInteger updateReason;
@property (nonatomic) NSInteger selectedHeroId;
@property (nonatomic) NSInteger comparedCardId;
@property (nonatomic, strong) NSMutableArray *targetPlayerNames;    // 指定的目标玩家们
@property (nonatomic, strong) NSArray *preTargetPlayerNames;        // 更改目标之前指定的玩家
@property (nonatomic, strong) NSArray *availableCardIds;            // 可以选择使用的牌
@property (nonatomic, strong) NSArray *availableSkillIds;           // 可以选择使用的技能
@property (nonatomic, strong) NSArray *availableEquipIds;           // 可以选择使用的装备
@property (nonatomic, strong) NSArray *droppableEquipIds;           // 可以选择弃置的装备(发动英雄技能)
@property (nonatomic, strong) NSArray *assignedCardIds;             // 待分配的牌
@property (nonatomic, strong) NSMutableArray *playedCardIds;        // 使用/弃/弃置的牌(发动技能或弃牌等)
@property (nonatomic, strong) NSMutableArray *selectedCardIds;      // 选中将要获得或拆除的牌
@property (nonatomic, strong) NSMutableArray *selectedCardIdxes;
@property (nonatomic) BGCardColor selectedColor;
@property (nonatomic) BGCardSuits selectedSuits;
@property (nonatomic) NSInteger heroSkillId;            // 触发或使用的英雄技能
@property (nonatomic) NSInteger equipmentId;            // 触发或使用的装备技能
@property (nonatomic) NSInteger transformedCardId;      // 转化后的卡牌ID

@property (nonatomic) NSUInteger handCardCount;         // 手牌数
@property (nonatomic) NSUInteger selectedCardCount;     // 选中的手牌或装备数
@property (nonatomic) NSUInteger selectedHandCardCount; // 选中的手牌数
@property (nonatomic) NSUInteger selectedEquipCount;    // 选中的装备数
@property (nonatomic) NSUInteger selectableCardCount;   // 可以选择使用/抽取的牌数
@property (nonatomic) NSUInteger requiredSelCardCount;  // 必须选中的牌数
@property (nonatomic) NSUInteger requiredTargetCount;   // 必须指定的目标玩家数
@property (nonatomic) NSUInteger maxTargetCount;        // 最多可以指定的玩家数

@property (nonatomic) NSInteger hpChanged;
@property (nonatomic) NSInteger spChanged;
@property (nonatomic) BOOL isFirstBlood;
@property (nonatomic) BOOL isDyingTriggered;            // 技能是否由濒死触发的
@property (nonatomic) BOOL isStrengthening;             // 是否选择强化阶段
@property (nonatomic) BOOL isStrengthened;              // 是否强化使用卡牌
@property (nonatomic) BOOL isGreeding;                  // 是否贪婪结算阶段
@property (nonatomic) BOOL isRequiredDrop;              // 是否强制的弃置
@property (nonatomic) BOOL isRequiredTarget;            // 是否必须指定1个目标
@property (nonatomic) BOOL isNoNeedCard;                // 是否不需要手牌发动技能
//@property (nonatomic) BOOL isIgnoreDispel;              // 是否忽略驱散

@property (nonatomic, strong, readonly) NSMutableArray *gameHistory;


- (void)renderRoleWithRoleId:(NSInteger)roleId animated:(BOOL)animated;
- (void)renderHeroWithHeroId:(NSInteger)heroId;
- (void)updateHeroWithHealthPoint:(NSInteger)hp skillPoint:(NSUInteger)sp;
- (void)enableHeroSkillWithSkillIds:(NSArray *)skillIds;

- (void)dealHandCardWithCardIds:(NSArray *)cardIds;
- (void)updateHandCardWithCardIds:(NSArray *)cardIds reason:(NSInteger)reason;
- (void)drawCardWithCardCount:(NSInteger)count;
- (void)enableHandCardWithCardIds:(NSArray *)cardIds;

- (void)updateEquipmentWithCardIds:(NSArray *)cardIds reason:(NSInteger)reason;
- (void)enableEquipmentWithCardIds:(NSArray *)equipmentIds isUse:(BOOL)isUse;

- (void)startTurn;
- (void)playHandCard;
- (void)updateTargetPlayerNamesFromIndex:(NSUInteger)startIdx;
- (void)useHeroSkill;
- (void)useEquipment;
- (void)cancelHeroSkill;
- (void)cancelEquipment;
- (void)chooseColorWithColor:(BGCardColor)color;
- (void)chooseSuitsWithSuits:(BGCardSuits)suits;
- (void)chooseTargetPlayer;
- (void)chooseViewPlayerRole;
- (void)chooseOkay;
- (void)chooseCancel;
- (void)startDiscard;

- (void)useHandCardWithCardIds:(NSArray *)cardIds;
- (void)launchHeroSkillWithSkillId:(NSInteger)skillId;
- (void)launchEquipmentWithCardId:(NSInteger)cardId;

- (void)getCardFromTableWithCardIds:(NSArray *)cardIds;
- (void)getCardFromPlayerWithCardIds:(NSArray *)cardIds cardCount:(NSUInteger)count;
- (void)giveCardToPlayerWithCardIds:(NSArray *)cardIds cardCount:(NSUInteger)count;
- (void)removeCardToTableWithCardIds:(NSArray *)cardIds;

- (void)enablePlayerArea;
- (void)disablePlayerAreaWithDarkColor;
- (void)disablePlayerAreaWithNormalColor;
- (void)checkTargetPlayerEnablement;

- (void)addPlayingButtons;
- (void)removePlayingButtons;
- (void)checkPlayingButtonEnablementWithSelectedCard:(BGHandCard *)selCard;
- (void)showTargetLinePath;

- (void)addProgressBar;
- (void)addProgressBarWithDuration:(CCTime)t;
- (void)removeProgressBar;

- (void)addTextPrompt;
- (void)addTextPromptForTriggering;
- (void)addTextPromptForSelectedHeroSkill;
- (void)addTextPromptForSelectedEquipment;
- (void)addTextPromptLabelWithString:(NSString *)string;
- (void)removeTextPrompt;

- (void)askForHelp;
- (void)diedAndShowRole:(NSInteger)roleId;
- (void)escapedFromGame;

- (void)addGameHistoryWithEsObject:(EsObject *)esObj;
- (void)addHistoryTip:(BGHistory *)history;

- (void)clearSelectedTargetPlayers;
- (void)resetAndRemovePlayingNodes;
- (void)resetSelectedCardIds;
- (void)resetValueAfterResolved;

- (void)showAllRolesMark:(CCButton *)sender;
- (void)markRole:(CCButton *)sender;

@end
