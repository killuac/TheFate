//
//  BGHand.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHandCard.h"

@interface BGHand : CCNode

@property (nonatomic, strong, readonly) NSArray *handCardIds;
@property (nonatomic, strong, readonly) NSMutableArray *handCards;        // 所有的手牌
@property (nonatomic, strong, readonly) NSMutableArray *selectedCards;    // 选中的手牌
@property (nonatomic, strong, readonly) BGHandCard *selectedCard;

- (void)dealHandCardWithCardIds:(NSArray *)cardIds;
- (void)updateHandCardWithCardIds:(NSArray *)cardIds;
- (void)addHandCardWithCardButtons:(NSArray *)cardButtons;

- (void)enableHandCardWithCardIds:(NSArray *)cardIds;
- (void)enableAllHandCards;
- (void)disableAllHandCardsWithDarkColor;
- (void)disableAllHandCardsWithNormalColor;
- (void)makeHandCardLeftAlignment;

- (void)cancelFirstSelectedCard;
- (void)playHandCardAnimated:(BOOL)animated block:(void (^)())block;    // 主动/被动使用手牌或弃牌
- (void)playHandCardAfterTimeIsUpWithBlock:(void (^)())block;           // Use card after progress time is up
- (void)addAndFaceDownOneCard:(CCButton *)cardButton;

- (void)resetSelectedHandCard;

@end
