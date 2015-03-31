//
//  BGTable.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//
//  how candidateHeros/compared/used/discarded cards and so on

#import "cocos2d.h"
#import "BGHeroInformation.h"
#import "BGAssigningPopup.h"

@interface BGTable : CCNode

@property (nonatomic, strong, readonly) CCLayoutBox *heroCardBox;   // 待选的英雄
@property (nonatomic, strong, readonly) BGCardPopup *cardPopup;     // 手牌或待分配的牌
@property (nonatomic, readonly) BOOL isShowHandCard;                // 弹出框是否展示的是手牌
@property (nonatomic, readonly) BOOL isAssigningCard;               // 弹出框是否是分配卡牌

- (NSArray *)sortComparedCardIds:(NSArray *)cardIds;

- (void)showCandidateHerosWithHeroIds:(NSArray *)heroIds;
- (void)showComparedCardWithCardIds:(NSArray *)cardIds maxCardId:(NSInteger)maxCardId;
- (void)showPlayedCardWithCardNodes:(NSArray *)cardNodes andClearTable:(BOOL)isClear;
- (void)showPlayedCardWithCardIds:(NSArray *)cardIds;
- (void)showRemovedCardWithCardIds:(NSArray *)cardIds;
- (void)showAssignedCardWithCardIds:(NSArray *)cardIds;

- (void)faceDownCardFromDeckWithCount:(NSUInteger)count;
- (void)faceUpTableCardWithCardIds:(NSArray *)cardIds;
- (void)revealCardFromDeckWithCardIds:(NSArray *)cardIds;

- (void)showPopupWithHandCount:(NSUInteger)count equipmentIds:(NSArray *)cardIds;
- (void)showPopupWithAssignedCardIds:(NSArray *)cardIds;

- (void)selectHeroCard:(CCButton *)cardButton;
- (void)selectHandCards:(NSArray *)cardButtons;
- (void)selectEquipmentCards:(NSArray *)cardButtons;
- (void)assignCardToEachPlayer;

- (void)assignCardFinished:(CCButton *)sender;

- (void)removePopup;
- (void)clearExistingCards;

@end
