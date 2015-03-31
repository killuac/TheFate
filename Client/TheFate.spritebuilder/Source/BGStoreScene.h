//
//  BGStoreScene.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGMerchandise.h"
#import "BGScrollNode.h"
#import "BGHeroInformation.h"

#define NORMAL_FONT_COLOR   [CCColor colorWithRed:0.6 green:0.58 blue:0.5]
#define SELECTED_FONT_COLOR [CCColor colorWithRed:1.0 green:0.8 blue:0.7]

#define kSegmentHero        @"hero"
#define kSegmentMember      @"member"
#define kSegmentGoldCoin    @"goldCoin"

#define kGoldCoinIds        @"goldCoinIds"
#define kVIPTypeIds         @"vipTypeIds"
#define kBoughtProductId    @"productId"
#define kReceiptString      @"receiptString"
#define kBoughtVIPTypeId    @"vipTypeId"
#define kBoughtHeroId       @"heroId"

#define kPrice              @"price"
#define kName               @"name"
#define kTitle              @"title"
#define kDescription        @"description"
#define kFreeUsedHeros      @"freeUsedHeros"

typedef NS_ENUM(NSInteger, BGVIPType) {
    VIPTypeDaily,
    VIPTypeMonthly,
    VIPTypeYearly
};

@class BGRoomScene, BGGameScene;

#if __CC_PLATFORM_IOS
@interface BGStoreScene : CCNode <BGPopupDelegate, UIAlertViewDelegate> {
#else
@interface BGStoreScene : CCNode <BGPopupDelegate> {
#endif
    CCLabelTTF *_goldCoinLabel;
    BGScrollNode *_scrollNode;
    CCLayoutBox *_buttonBox;
    CCScrollView *_scrollView;
    CCLayoutBox *_pickHeroBox;
    CCLabelTTF *_pickHeroTitle;
    
    BGHeroInformation *_heroInformation;
}

@property (nonatomic, strong, readonly) CCEffectNode *effectNode;

@property (nonatomic, strong) NSArray *goldCoinIds;
@property (nonatomic, strong) NSArray *vipTypeIds;
@property (nonatomic) BOOL isPickHero;
@property (nonatomic, strong) NSArray *availableHeroIds;

@property (nonatomic, copy, readonly) NSString *boughtProdcutId;
@property (nonatomic, readonly) NSInteger boughtVIPTypeId;
@property (nonatomic, readonly) NSInteger boughtHeroId;
@property (nonatomic, readonly) NSInteger pickedHeroId;

- (void)loadData;
- (void)updateGoldCoin:(BOOL)isRecharge;
- (BGRoomScene *)backToRoomScene;
- (BGGameScene *)showGameScene;

- (void)segmentSelected:(CCButton *)sender;
- (void)heroSegmentSelected:(CCButton *)sender;     // Pick hero
- (void)buy:(CCButton *)sender;
- (void)back:(CCButton *)sender;

@end
