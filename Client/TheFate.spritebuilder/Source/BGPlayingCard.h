//
//  BGPlayingCard.h
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGCard.h"

#define kCardType               @"cardType"
#define kCardFigure             @"cardFigure"
#define kCardSuits              @"cardSuits"
#define kTargetCount            @"targetCount"
#define kDamageValue            @"damageValue"
#define kIsStrengthenable       @"isStrengthenable"
#define kRequiredSp             @"requiredSp"
#define kEquipmentType          @"equipmentType"
#define kPlusDistance           @"plusDistance"
#define kMinusDistance          @"minusDistance"
#define kAttackRange            @"attackRange"
#define kIsActiveLaunchable     @"isActiveLaunchable"
#define kTransformedCardId      @"transformedCardId"
#define kIsEquippedOne          @"isEquippedOne"
#define kMinHandCardCount       @"minHandCardCount"
#define kTipText                @"tipText"
#define kStrenTipText           @"strenTipText"
#define kTargetTipText          @"targetTipText"
#define kDispelTipText          @"dispelTipText"
#define kUseTipText             @"useTipText"
#define kTriggerTipText         @"triggerTipText"
#define kHistoryText            @"historyText"
#define kHistoryText2           @"historyText2"

typedef NS_ENUM(NSInteger, BGPlayingCardEnum) {
    PlayingCardNull = 0,
    PlayingCardNormalAttack = 1,           	// 普通攻击
    PlayingCardFlameAttack = 2,            	// 火焰攻击
    PlayingCardChaosAttack = 3,            	// 混乱攻击
    PlayingCardEvasion = 4,                	// 闪避
    PlayingCardHealingSalve = 5,           	// 治疗药膏
    
    PlayingCardFanaticism = 6,             	// 狂热
    PlayingCardMislead = 7,                	// 误导
    PlayingCardChakra = 8,                 	// 查克拉
    PlayingCardWildAxe = 9,                	// 野性之斧
    PlayingCardDispel = 10,                 // 驱散
    PlayingCardDisarm  = 11,               	// 缴械
    PlayingCardElunesArrow = 12,           	// 月神之箭
    PlayingCardEnergyTransport = 13,		// 能量转移
    PlayingCardGreed = 14,                 	// 贪婪
    PlayingCardSirenSong = 15,             	// 海妖之歌
    
    PlayingCardGodsStrength = 16,          	// 神之力量
    PlayingCardViperRaid = 17,             	// 蝮蛇突袭
    PlayingCardTimeLock = 18,              	// 时间静止
    PlayingCardSunder = 19,                	// 灵魂隔断
    PlayingCardLagunaBlade = 20,           	// 神灭斩
    
    PlayingCardEyeOfSkadi = 21,            	// 冰魄之眼
    PlayingCardBladesOfAttack = 22,        	// 攻击之爪
    PlayingCardSacredRelic = 23,           	// 圣者遗物
    PlayingCardDemonEdge = 24,             	// 恶魔刀锋
    PlayingCardDiffusalBlade = 25,         	// 散失之刃
    PlayingCardLotharsEdge = 26,           	// 洛萨之锋
    PlayingCardStygianDesolator = 27,      	// 黯灭之刃
    PlayingCardSangeAndYasha = 28,         	// 散夜对剑
    PlayingCardPlunderAxe = 29,            	// 掠夺之斧
    PlayingCardMysticStaff = 30,           	// 神秘法杖
    PlayingCardEaglehorn = 31,             	// 鹰角弓
    PlayingCardQuellingBlade = 32,         	// 补刀斧
    
    PlayingCardPhyllisRing = 33,           	// 菲丽丝之戒
    PlayingCardBladeMail = 34,             	// 刃甲
    PlayingCardBootsOfSpeed = 35,          	// 速度之靴
    PlayingCardPlaneswalkersCloak = 36,    	// 流浪法师斗篷
    PlayingCardTalismanOfEvasion = 37     	// 闪避护符
};

typedef NS_ENUM(NSInteger, BGCardFigure) {
    CardFigure1 = 1,
    CardFigure2,
    CardFigure3,
    CardFigure4,
    CardFigure5,
    CardFigure6,
    CardFigure7,
    CardFigure8,
    CardFigure9,
    CardFigure10,
    CardFigure11,
    CardFigure12,
    CardFigure13
};

typedef NS_ENUM(NSInteger, BGCardColor) {
    CardColorNull = -1,
    CardColorRed = 0,           // 红色
    CardColorBlack              // 黑色
};

typedef NS_ENUM(NSInteger, BGCardSuits) {
    CardSuitsNull = -1,
    CardSuitsDiamonds = 0,      // 方块
    CardSuitsClubs,             // 梅花
    CardSuitsHearts,            // 红桃
    CardSuitsSpades             // 黑桃
};

typedef NS_ENUM(NSInteger, BGCardType) {
    CardTypeBasic = 0,          // 基本牌
    CardTypeMagic,              // 魔法牌
    CardTypeSuperSkill,         // S技能牌
    CardTypeEquipment           // 装备牌
};

typedef NS_ENUM(NSInteger, BGEquipmentType) {
    EquipmentTypeWeapon = 0,    // 武器
    EquipmentTypeArmor          // 防具
};

@class BGPlayer;

@interface BGPlayingCard : BGCard
{
    NSString *_tipText;
    NSString *_useTipText;
    NSString *_targetTipText;
    NSString *_dispelTipText;
    NSString *_historyText;
    NSString *_historyText2;
}

@property (nonatomic, readonly) NSInteger cardEnum;
@property (nonatomic, readonly) BGCardFigure cardFigure;
@property (nonatomic, readonly) BGCardSuits cardSuits;
@property (nonatomic, readonly) BGCardColor cardColor;
@property (nonatomic, readonly) BGCardType cardType;
@property (nonatomic, readonly) BOOL isTargetable;              // 使用卡牌是否需要手动指定目标
@property (nonatomic, readonly) NSUInteger targetCount;         // 需要指定的目标数量
@property (nonatomic, readonly) NSUInteger damageValue;         // 可以造成的伤害值

@property (nonatomic, copy, readonly) NSString *tipText;
@property (nonatomic, copy, readonly) NSString *strenTipText;
@property (nonatomic, copy, readonly) NSString *targetTipText;
@property (nonatomic, copy, readonly) NSString *dispelTipText;
@property (nonatomic, copy, readonly) NSString *useTipText;
@property (nonatomic, copy, readonly) NSString *triggerTipText;

@property (nonatomic, copy, readonly) NSString *cardFullText;   // Text+Suits+Figure
@property (nonatomic, copy, readonly) NSString *historyText;
@property (nonatomic, copy, readonly) NSString *historyText2;

@property (nonatomic, readonly) BOOL isAttack;
@property (nonatomic, readonly) BOOL isMislead;
@property (nonatomic, readonly) BOOL isWildAxe;
@property (nonatomic, readonly) BOOL isRedColor;

// Magic
@property (nonatomic, readonly) BOOL isStrengthenable;
@property (nonatomic, readonly) BOOL isDispellable;
@property (nonatomic, readonly) BOOL isDispel;

// Magic/Super Skill
@property (nonatomic, readonly) NSUInteger requiredSp;          // 强化需要怒气

// Equipment
@property (nonatomic, readonly) BGEquipmentType equipmentType;
@property (nonatomic, readonly) NSUInteger plusDistance;
@property (nonatomic, readonly) NSInteger minusDistance;
@property (nonatomic, readonly) NSUInteger attackRange;
@property (nonatomic, readonly) NSInteger transformedCardId;    // 转化后的卡牌ID(如散夜对剑:两张手牌当攻击使用)
@property (nonatomic, readonly) NSUInteger minHandCardCount;    // 发动装备最少需要的手牌数
@property (nonatomic, readonly) BOOL isActiveLaunchable;        // 是否可以主动触发
@property (nonatomic, readonly) BOOL isEquippedOne;             // 武器和防具是否只能装备一个
@property (nonatomic, readonly) BOOL isEquipment;

+ (NSMutableArray *)playingCardsByCardIds:(NSArray *)cardIds;
+ (NSMutableArray *)playingCardIdsByCards:(NSArray *)cards;

+ (NSString *)colorTextByColor:(BGCardColor)color;
+ (NSString *)suitsTextBySuits:(BGCardSuits)suits;

- (NSString *)figureDisplayedText;
- (NSString *)suitsDisplayedText;

@end
