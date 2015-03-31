//
//  BGFateCard.h
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGCard.h"

typedef NS_ENUM(NSInteger, BGFateCardId) {
    FateCardNull = 0,
    FateCardVersus,                     // 对战模式
    FateCardShadowPunisher,             // 暗影惩戒者
    FateCardConquerorOfHolyLight,       // 圣光征服者
    FateCardSpreadingPlague,            // 蔓延的瘟疫
    FateCardGameofFate,                 // 命运的博弈
    FateCardPuppetOfBackfire,           // 反噬的傀儡
    FateCardSummonerOfDeath,            // 死神的召唤者
    FateCardParanoidMathematician,      // 偏执的数学家
    FateCardRoshanPossession            // Roshan附体
};


@interface BGFateCard : BGCard

@end
