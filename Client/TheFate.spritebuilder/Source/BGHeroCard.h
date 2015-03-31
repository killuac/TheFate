//
//  BGHeroCard.h
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGCard.h"
#import "BGHeroSkill.h"

#define kAttribute          @"attribute"
#define kHpLimit            @"hpLimit"
#define kSpLimit            @"spLimit"
#define kHandSizeLimit      @"handSizeLimit"
#define kSkillIds           @"skillIds"
#define kGender             @"gender"
#define kPrice              @"price"

typedef NS_ENUM(NSInteger, BGHeroCardId) {
    HeroCardNull = 0,
    HeroCardLordOfAvernus = 1,				// 死亡骑士
    HeroCardSkeletonKing = 2,              	// 骷髅王
    HeroCardBristleback = 3,               	// 刚背兽
    HeroCardSacredWarrior = 4,             	// 神灵武士
    HeroCardOmniknight = 5,                	// 全能骑士
    HeroCardAxe = 6,                       	// 斧王
    HeroCardCentaurWarchief = 7,           	// 半人马酋长
    HeroCardDragonKnight = 8,              	// 龙骑士
    HeroCardGuardianKnight = 9,            	// 守护骑士
    
    HeroCardGorgon = 10,                    // 蛇发女妖
    HeroCardLightningRevenant = 11,			// 闪电幽魂
    HeroCardJuggernaut = 12,               	// 剑圣
    HeroCardVengefulSpirit = 13,           	// 复仇之魂
    HeroCardStrygwyr = 14,                 	// 血魔
    HeroCardTrollWarlord = 15,             	// 巨魔战将
    HeroCardDwarvenSniper = 16,            	// 矮人火枪手
    HeroCardNerubianAssassin = 17,         	// 地穴刺客
    HeroCardAntimage = 18,                 	// 敌法师
    HeroCardNerubianWeaver = 19,           	// 地穴编织者
    HeroCardUrsaWarrior = 20,              	// 熊战士
    HeroCardChenYunSheng = 21,             	// 陈云生
    
    HeroCardSlayer = 22,                   	// 秀逗魔导师
    HeroCardNecrolyte = 23,                	// 死灵法师
    HeroCardTwinHeadDragon = 24,           	// 双头龙
    HeroCardCrystalMaiden = 25,            	// 水晶室女
    HeroCardLich = 26,                     	// 巫妖
    HeroCardShadowPriest = 27,             	// 暗影牧师
    HeroCardOgreMagi = 28,                 	// 食人魔法师
    HeroCardKeeperOfTheLight = 29,         	// 光之守卫
    HeroCardGoblinTechies = 30,            	// 哥布林工程师
    HeroCardStormSpirit = 31,              	// 风暴之灵
    HeroCardEnchantress = 32,              	// 魅惑魔女
    HeroCardElfLily = 33                  	// 精灵莉莉
};

typedef NS_ENUM(NSInteger, BGHeroAttribute) {
    HeroAttributeStrength = 1,      // 力量型
    HeroAttributeAgility,           // 敏捷型
    HeroAttributeIntelligence       // 智力型
};

typedef NS_ENUM(NSInteger, BGHeroGender) {
    HeroGenderFemale = -1,
    HeroGenderMale = 1
};

@interface BGHeroCard : BGCard

@property (nonatomic, readonly) BGHeroAttribute attribute;
@property (nonatomic, readonly) NSInteger hpLimit;          // Health Point
@property (nonatomic, readonly) NSUInteger spLimit;         // Skill Point
@property (nonatomic, readonly) NSUInteger handSizeLimit;   // 手牌上限
@property (nonatomic, readonly) NSArray *skillIds;
@property (nonatomic, readonly) NSInteger gender;
@property (nonatomic, readonly) NSUInteger price;

@property (nonatomic, copy, readonly) NSString *attrImageName;

+ (NSArray *)heroCardsWithHeroIds:(NSArray *)heroIds;
+ (NSArray *)saleHeroCards;

- (void)playSound;

@end
