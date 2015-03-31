//
//  BGHeroSkill.h
//  FateClient
//
//  Created by Killua Liu on 7/29/13.
//
//

#import <Foundation/Foundation.h>
#import "BGChecker.h"

#define kSkillCategory      @"skillCategory"
#define kSkillType          @"skillType"
#define kSkillName          @"skillName"
#define kSkillText          @"skillText"
#define kSkillDesc          @"skillDesc"
#define kIsDispellable      @"isDispellable"
#define kTargetCount        @"targetCount"
#define kMaxTargetCount     @"maxTargetCount"
#define kMinHandCardCount   @"minHandCardCount"
#define kTransformedCardId  @"transformedCardId"
#define kTriggerReasons     @"triggerReasons"
#define kUseTipText         @"useTipText"
#define kTriggerTipText     @"triggerTipText"
#define kYesTipText         @"yesTipText"
#define kDispelTipText      @"dispelTipText"
#define kTargetTipText      @"targetTipText"
#define kHistoryText        @"historyText"
#define kHistoryText2       @"historyText2"

typedef NS_ENUM(NSInteger, BGHeroSkillId) {
    HeroSkillNull = 0,
    HeroSkillDeathCoil = 1,             // 死亡缠绕
    HeroSkillFrostmourne = 2,           // 霜之哀伤
    
    HeroSkillReincarnation = 3,         // 重生
    HeroSkillVampiricAura = 4,          // 吸血
    
    HeroSkillWarpath = 5,               // 战意
    HeroSkillBristleback = 6,           // 刚毛后背
    
    HeroSkillLifeBreak = 7,             // 牺牲
    HeroSkillBurningSpear = 8,          // 沸血之矛
    
    HeroSkillPurification = 9,          // 洗礼
    HeroSkillHolyLight = 10,            // 圣光
    
    HeroSkillBattleHunger = 11,         // 战争饥渴
    HeroSkillCounterHelix = 12,         // 反转螺旋
    
    HeroSkillDoubleEdge = 13,           // 双刃剑
    
    HeroSkillBreatheFire = 14,          // 火焰气息
    HeroSkillDragonBlood = 15,          // 龙族血统
    
    HeroSkillGuardian = 16,             // 援护
    HeroSkillFaith = 17,                // 信仰
    HeroSkillFatherlyLove = 18,			// 父爱
    
    HeroSkillMysticSnake = 19,          // 秘术异蛇
    HeroSkillManaShield = 20,           // 魔法护盾
    
    HeroSkillPlasmaField = 21,          // 等离子场
    HeroSkillUnstableCurrent = 22,		// 不定电流
    
    HeroSkillOmnislash = 23,            // 无敌斩
    HeroSkillBladeDance = 24,           // 剑舞
    
    HeroSkillNetherSwap = 25,           // 移形换位
    HeroSkillWaveOfTerror = 26,         // 恐怖波动
    
    HeroSkillBloodrage = 27,            // 血之狂暴
    HeroSkillStrygwyrsThirst = 28,      // 嗜血
    HeroSkillBloodBath = 29,            // 屠戮
    
    HeroSkillBattleTrance = 30,         // 战斗专注
    HeroSkillFervor = 31,               // 热血战魂
    
    HeroSkillHeadshot = 32,             // 爆头
    HeroSkillTakeAim = 33,              // 瞄准
    HeroSkillShrapnel = 34,             // 散弹
    
    HeroSkillManaBurn = 35,             // 法力燃烧
    HeroSkillVendetta = 36,             // 复仇
    HeroSkillSpikedCarapace = 37,       // 穿刺护甲
    
    HeroSkillManaBreak = 38,            // 法力损毁
    HeroSkillBlink = 39,                // 闪烁
    HeroSkillManaVoid = 40,             // 法力虚空
    
    HeroSkillTheSwarm = 41,             // 蝗虫群
    HeroSkillTimeLapse = 42,            // 时光倒流
    
    HeroSkillFurySwipes = 43,           // 怒意狂击
    HeroSkillEnrage = 44,               // 激怒
    
    HeroSkillOrdeal = 45,               // 神判
    HeroSkillSpecialBody = 46,          // 特殊体质
    
    HeroSkillFierySoul = 47,            // 炽魂
    HeroSkillLagunaBlade = 48,          // 神灭斩
    HeroSkillFanaticismHeart = 49,      // 狂热之心
    
    HeroSkillHeartstopperAura = 50,     // 竭心光环
    HeroSkillSadist = 51,               // 施虐之心
    
    HeroSkillIcePath = 52,              // 冰封
    HeroSkillLiquidFire = 53,           // 液态火
    
    HeroSkillFrostbite = 54,            // 冰封禁制
    HeroSkillBrillianceAura = 55,       // 辉煌光环
    
    HeroSkillDarkRitual = 56,           // 邪恶祭祀
    HeroSkillFrostArmor = 57,           // 霜冻护甲
    
    HeroSkillShallowGrave = 58,         // 薄葬
    HeroSkillShadowWave = 59,           // 暗影波
    
    HeroSkillFireblast = 60,            // 火焰爆轰
    HeroSkillMultiCast = 61,            // 多重施法
    
    HeroSkillIlluminate = 62,           // 冲击波
    HeroSkillChakraMagic = 63,          // 查克拉
    HeroSkillGrace = 64,                // 恩惠
    
    HeroSkillRemoteMines = 65,          // 遥控炸弹
    HeroSkillFocusedDetonate = 66,      // 引爆
    HeroSkillSuicideSquad = 67,         // 自爆
    
    HeroSkillOverload = 68,             // 超负荷
    HeroSkillBallLightning = 69,        // 球状闪电
    
    HeroSkillUntouchable = 70,          // 不可侵犯
    HeroSkillEnchant = 71,              // 魅惑
    HeroSkillNaturesAttendants = 72,    // 自然之助
    
    HeroSkillHealingSpell = 73,         // 治疗术
    HeroSkillDispelWizard = 74,         // 驱散精灵
    HeroSkillMagicControl = 75          // 魔法掌控
};

typedef NS_ENUM(NSInteger, BGSkillCategory) {
    SkillCategoryActive = 0,            // 主动技能
    SkillCategoryPassive,               // 被动技能
};

typedef NS_ENUM(NSInteger, BGSkillType) {
    SkillTypeGeneral = 0,               // 普通技
    SkillTypeLimitOneTurn,              // 限制技(每回合限1次)
    SkillTypeLimitOneRound              // 限定技(每局限1次)
};

@class BGPlayer;

@interface BGHeroSkill : NSObject <BGChecker>
{
    NSString *_useTipText;
    NSString *_triggerTipText;
    NSString *_targetTipText;
    NSString *_dispelTipText;
    NSString *_historyText;
    NSString *_historyText2;
}

@property (nonatomic, weak, readonly) BGPlayer *player;

@property (nonatomic, readonly) NSInteger skillId;
@property (nonatomic, copy, readonly) NSString *skillName;
@property (nonatomic, readonly) BGSkillCategory skillCategory;
@property (nonatomic, readonly) BGSkillType skillType;
@property (nonatomic, copy, readonly) NSString *skillText;
@property (nonatomic, copy, readonly) NSString *skillDesc;
@property (nonatomic, readonly) BOOL isTargetable;
@property (nonatomic, readonly) BOOL isDispellable;
@property (nonatomic, readonly) BOOL isActive;
@property (nonatomic, readonly) BOOL isSimple;  // No need target and hand card to launch the skill
@property (nonatomic, readonly) NSUInteger targetCount;
@property (nonatomic, readonly) NSUInteger maxTargetCount;
@property (nonatomic, readonly) NSUInteger minHandCardCount;
@property (nonatomic, readonly) NSInteger transformedCardId;
@property (nonatomic, readonly) NSInteger transformedCardId2;

@property (nonatomic, readonly) NSArray *triggerReasons;
@property (nonatomic, readwrite) BOOL isSoundPlayed;    // Don't repeat play

@property (nonatomic, copy, readonly) NSString *useTipText;
@property (nonatomic, copy, readonly) NSString *triggerTipText;
@property (nonatomic, copy, readonly) NSString *yesTipText;
@property (nonatomic, copy, readonly) NSString *dispelTipText;
@property (nonatomic, copy, readonly) NSString *targetTipText;

@property (nonatomic, copy, readonly) NSString *historyText;
@property (nonatomic, copy, readonly) NSString *historyText2;

@property (nonatomic, readwrite) BOOL isSelected;

// Create dynamic hero skill instance according to different skill subclass
- (id)initWithSkillId:(BGHeroSkillId)skillId ofPlayer:(BGPlayer *)player;
+ (id)heroSkillWithSkillId:(BGHeroSkillId)skillId ofPlayer:(BGPlayer *)player;

+ (NSMutableArray *)heroSkillsBySkillIds:(NSArray *)skillIds ofPlayer:(BGPlayer *)player;
+ (NSMutableArray *)heroSkillIdsBySkills:(NSArray *)skills;

- (void)playSound;

@end
