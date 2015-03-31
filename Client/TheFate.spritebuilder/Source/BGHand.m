//
//  BGHand.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHand.h"
#import "BGGameScene.h"

@interface BGHand ()

@property (nonatomic, weak) BGGameScene *game;
@property (nonatomic, weak) BGPlayer *player;

@property (nonatomic, strong) NSMutableArray *selCardButtons;

@property (nonatomic) CGFloat cardWidth;
@property (nonatomic) CGFloat cardHeight;
@property (nonatomic) CGPoint cardStartPosition;        // 手中卡牌的最左边起始位置
@property (nonatomic) CGPoint cardEndPosition;          // 手中卡牌的最右边终止位置
@property (nonatomic) NSUInteger facedDownCardCount;    // 暗置的牌数

@end

@implementation BGHand

- (void)didLoadFromCCB
{    
    _handCards = [NSMutableArray array];
    _selectedCards = [NSMutableArray array];
    _selCardButtons = [NSMutableArray array];
    
    _cardWidth = PLAYING_CARD_WIDTH;
    _cardHeight = PLAYING_CARD_HEIGHT;
    _cardStartPosition = ccp(self.player.character.contentSize.width+_cardWidth*0.54f, _cardHeight*0.54f);
    _cardEndPosition = ccpAdd(_cardStartPosition, ccp(_cardWidth*(COUNT_MAX_HAND_CARD-1), 0.0f));
}

- (NSArray *)handCardIds
{
    return [BGPlayingCard playingCardIdsByCards:_handCards];
}

/*
 * Deal hand cards
 */
- (void)dealHandCardWithCardIds:(NSArray *)cardIds
{
    [self removeAllChildren];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.player];
    [_handCards addObjectsFromArray:cards];
    [self addCardButtonsWithCards:cards];
    
    [self setAllChildButtonsEnabled:NO];
    [self makeHandCardLeftAlignment];
}

- (void)addCardButtonsWithCards:(NSArray *)cards
{
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:self];
    
    for (BGCardButton *cardButton in cardButtons) {
        NSAssert([cardButton isKindOfClass:[CCButton class]], @"Card is not button");
        [self addChild:cardButton z:self.children.count];
    }
}

- (void)addChild:(CCNode *)node z:(NSInteger)z
{
    if ([node isKindOfClass:[BGCardButton class]])
        ((BGCardButton *)node).oldZOrder = z;
    [super addChild:node z:z];
}

- (BGHandCard *)getHandCardByCardId:(NSInteger)cardId
{
    for (BGHandCard *card in _handCards) {
        if (card.cardId == cardId) return card;
    }
    return nil;
}

- (BGGameScene *)game
{
    return self.player.game;
}

- (BGPlayer *)player
{
    return (BGPlayer *)self.parent;
}

#pragma mark - Buffer handling
/*
 * Remove hand cards for updating buffer
 */
- (void)updateHandCardBuffer
{
    [_handCards removeObjectsInArray:_selectedCards];
    [self clearSelectedCardBuffer];
}

- (void)clearSelectedCardBuffer
{
    [_selectedCards removeAllObjects];
    [_selCardButtons removeAllObjects];
}

- (void)clearHandCards
{
    [self clearSelectedCardBuffer];
    [_handCards removeAllObjects];
    [self removeAllChildren];
}

#pragma mark - Hand cards updating
/*
 * Update(Add/Remove) hand card with all hand card id list that received from server
 * If the received card id list count is great than hand card count, need add. Otherwise, remove.
 */
- (void)updateHandCardWithCardIds:(NSArray *)cardIds
{
//  Hand card id list different between client and server after rejoin game
    if (UpdateReasonRejoin == self.player.updateReason) {
        [self clearHandCards];
        [self addHandCardWithCardIds:cardIds];
        return;
    }
    
    NSMutableArray *newCardIds = [cardIds mutableCopy]; // Received cardIds from server
    NSMutableArray *handCardIds = [BGPlayingCard playingCardIdsByCards:_handCards];
    if ([newCardIds isEqualToArray:handCardIds]) return;
    
    if (newCardIds.count > handCardIds.count) {
        [newCardIds removeObjectsInArray:handCardIds];
        [self addHandCardWithCardIds:newCardIds];
    }
    else if (newCardIds.count < handCardIds.count) {
        [handCardIds removeObjectsInArray:newCardIds];
        [self removeHandCardWithCardIds:handCardIds];
    }
    else {
        self.player.updateReason = UpdateReasonDefault;
        [self clearHandCards];
        [self addHandCardWithCardIds:cardIds];
    }
}

/*
 * Add(Got) hand card from table or target player
 */
- (void)addHandCardWithCardButtons:(NSArray *)cardButtons
{
    [self setAllChildButtonsEnabled:NO];
    
    for (BGCardButton *cardButton in cardButtons) {
        BGHandCard *card = [BGHandCard handCardWithCardId:cardButton.name.integerValue ofPlayer:self.player];
        [_handCards addObject:card];    // Add to card buffer
        
        BGCardButton *newCardButton = [BGButtonFactory createCardButtonWithCard:card forTarget:self];
        newCardButton.position = cardButton.position;
        [self addChild:newCardButton z:self.children.count];
    }
    
    [self makeHandCardLeftAlignment];
}

/*
 * Add hand card - check if there are faced down cards first
 */
- (void)addHandCardWithCardIds:(NSArray *)cardIds
{
    [self setAllChildButtonsEnabled:NO];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.player];
    [_handCards addObjectsFromArray:cards]; // Update buffer
    
//  If there is faced down cards, need face up them by flipping.
    if (_facedDownCardCount > 0) {
        [self faceUpCardWithCards:cards];
    }
    else {
        if (UpdateReasonTable == self.player.updateReason) {
            
        } else if (UpdateReasonPlayer == self.player.updateReason) {
            [self getCardFromOtherPlayerWithCards:cards];
        } else {
            [self drawCardFromTopDeckWithCards:cards];
        }
    }
}

- (void)drawCardFromTopDeckWithCards:(NSArray *)cards
{
    [self addCardButtonsWithCards:cards];
    [self makeHandCardLeftAlignment];
}

/*
 * If self player is target player, the "fromOtherPlayer" is turn owner.
 */
- (void)getCardFromOtherPlayerWithCards:(NSArray *)cards
{
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:self];
    [cardButtons enumerateObjectsUsingBlock:^(BGCardButton *cardButton, NSUInteger idx, BOOL *stop) {
        CGPoint pos = (self.player.isTurnOwner) ?
            self.player.targetPlayer.centerPosition :
            self.game.turnOwner.centerPosition;
        cardButton.position = CARD_MOVE_POSITION(pos, idx, cardButtons.count);
        [self addChild:cardButton z:self.children.count];
    }];
    
    [self makeHandCardLeftAlignment];
}

- (void)faceUpCardWithCards:(NSArray *)cards
{
    [self runDelayWithDuration:DURATION_CARD_MOVE+DURATION_CARD_FLIP block:^{
        for (NSUInteger i = 0; i < _facedDownCardCount; i++) {
            NSUInteger idx = _handCards.count - _facedDownCardCount + i;
            @try {
                BGCardButton *cardBack = self.children[idx];
                
                BGCardButton *newCardButton = [BGButtonFactory createCardButtonWithCard:cards[i] forTarget:self];
                newCardButton.visible = NO;
                newCardButton.position = cardBack.position;
                [self addChild:newCardButton z:cardBack.zOrder];
                
                [cardBack runFlipFromLeftWithDuration:DURATION_CARD_FLIP toNode:newCardButton block:^{
                    _facedDownCardCount--;
                }];
            }
            @catch (NSException *exception) {
//              Not crash
            }
        }
    }];
}

/*
 * Remove hand card: Is drew/discarded or used by server(time up)
 */
- (void)removeHandCardWithCardIds:(NSArray *)cardIds
{
    [self clearSelectedCardBuffer];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.player];
    [cards enumerateObjectsUsingBlock:^(BGHandCard *card, NSUInteger idx, BOOL *stop) {
        for (BGCardButton *cardButton in self.children) {
            if (card.cardId == cardButton.name.integerValue) {
                [_selCardButtons addObject:cardButton];
                NSUInteger idx = [self.children indexOfObject:cardButton];
                [_selectedCards addObject:_handCards[idx]];
                break;
            }
        }
    }];
    
    if (UpdateReasonTable == self.player.updateReason) {
        [self moveSelectedCardToTable];
    } else if (UpdateReasonPlayer == self.player.updateReason) {
        [self moveSelectedCardToPlayer];
    }
    
    [self updateHandCardBuffer];
    [self makeHandCardLeftAlignment];
}

/*
 * Make all hand cards left aligment
 * If one card move multiple times at the same time, need wait one by one(by sequence).
 */
- (void)makeHandCardLeftAlignment
{
    if (0 == self.children.count) return;
    
    [self setAllChildButtonsInteractionEnabled:NO];
    
//  Reset selected cardButtons to old zOrder
    for (BGCardButton *cardButton in _selCardButtons) {
        cardButton.zOrder = cardButton.oldZOrder;
    }
    
    NSArray *sortedChildren = [self.children sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        return ([obj1 zOrder] < [obj2 zOrder]) ? NSOrderedAscending : NSOrderedDescending;
    }];
    
//  If card count is great than 6(No overlap), need narrow the padding. But the first card's position unchanged.
    CGFloat padding = PLAYING_CARD_PADDING(self.children.count, (COUNT_MAX_HAND_CARD));
    padding = (padding > -_cardWidth) ? padding : -_cardWidth;
    
    [sortedChildren enumerateObjectsUsingBlock:^(BGCardButton *cardButton, NSUInteger idx, BOOL *stop) {
        cardButton.zOrder = idx;     // Reset zOrder from zero
        if ([cardButton isKindOfClass:[BGCardButton class]]) cardButton.oldZOrder = idx;
        
        if ([cardButton isMemberOfClass:[BGCardButton class]])
            cardButton.selected = NO;
        
        if (CGPointEqualToPoint(cardButton.position, CGPointZero))
            cardButton.position = _cardEndPosition;
        
        // Can't exceed hand area's width(Don't overlap with equipment area)
        CGPoint targetPos = ccpAdd(_cardStartPosition, ccp((_cardWidth+padding)*idx, 0.0f));
        targetPos = (targetPos.x < _cardEndPosition.x) ? targetPos : _cardEndPosition;
        
        [cardButton runEaseMoveWithDuration:DURATION_CARD_MOVE position:targetPos block:^{
            [self setAllChildButtonsInteractionEnabled:YES];
        }];
    }];
}

#pragma mark - Hand cards availability
/*
 * Enable hand card by receiving available card id list from server
 */
- (void)enableHandCardWithCardIds:(NSArray *)cardIds
{
    [self setAllChildButtonsEnabled:NO];
    
//  Check if hand card id is contained in available card id list
    for (BGCardButton *cardButton in self.children) {
        if (![cardButton isKindOfClass:[CCButton class]]) continue;
        cardButton.enabled = [cardIds containsObject:@(cardButton.name.integerValue)];
        CCColor *color = (cardButton.enabled) ? NORMAL_COLOR : DISABLED_COLOR;
        [cardButton setColorWith:color isRecursive:YES];
        
        // 辅助选牌
        if (cardButton.enabled && [self isAutoSelectable]) {
            [self resetSelectedHandCard];
            cardButton.selected = YES;
            [self cardSelected:cardButton];
        }
    }
}

- (BOOL)isAutoSelectable
{
    return (self.player.selectableCardCount == 1 && _selectedCards.count == 0 &&
            (GameStateCardChoosing  == self.player.game.state ||
             GameStatePlayerDying   == self.player.game.state) &&
            [[NSUserDefaults standardUserDefaults] boolForKey:kAidSelection]);
}

/*
 * Need enable all hand cards menu while discarding
 */
- (void)enableAllHandCards
{
    [self setAllChildButtonsEnabled:YES];
    [self setColorWith:NORMAL_COLOR isRecursive:YES];
}

/*
 * Need disable all hand cards menu after use/discard card is over
 */
- (void)disableAllHandCardsWithNormalColor
{
    [self setAllChildButtonsEnabled:NO];
    [self setColorWith:NORMAL_COLOR isRecursive:YES];
}

- (void)disableAllHandCardsWithDarkColor
{
    [self setAllChildButtonsEnabled:NO];
    [self setColorWith:DISABLED_COLOR isRecursive:YES];
}

#pragma mark - Hand cards selector
/*
 * Button selector method is called while selecting a hand card
 */
- (void)cardSelected:(BGCardButton *)cardButton
{
    NSAssert(_selectedCards, @"_selectedCards Nil in %@", NSStringFromSelector(_cmd));
    NSAssert(_selCardButtons, @"_selCardButtons Nil in %@", NSStringFromSelector(_cmd));
    
    if (cardButton.isLongPressed) return;
    
    _selectedCard = [self getHandCardByCardId:cardButton.name.intValue];
    
//  Need move up/down while a card is selected/deselected
    CGPoint targetPos;
    CGFloat cardPosY = _cardStartPosition.y;
    CGFloat moveHeight = _cardHeight/6;
    
    _selectedCard.isSelected = !_selectedCard.isSelected;
    if (cardButton.selected) {
        cardButton.zOrder = 100;    // Make the selected card on the top
        targetPos = ccp(cardButton.position.x, cardPosY+moveHeight);
        [_selCardButtons addObject:cardButton];
        [_selectedCards addObject:_selectedCard];
    }
    else {
        cardButton.zOrder = cardButton.oldZOrder;
        targetPos = ccp(cardButton.position.x, cardPosY);
        [_selCardButtons removeObject:cardButton];
        [_selectedCards removeObject:_selectedCard];
    }
    
    [cardButton runEaseMoveWithDuration:DURATION_SELECTED_CARD_MOVE position:targetPos block:nil];
    
//  If selected cards count great than maximum, deselect and remove the first selected card.
    if (self.player.selectedCardCount > self.player.selectableCardCount) {  // 包括选中的装备
        @try {
            if (self.player.selectedEquipCount > 0) {
                [self.player.equipment cancelFirstSelectedEquipment];
            } else {
                BGHandCard *firstSelCard = _selectedCards.firstObject;
                for (BGCardButton *button in self.children) {
                    if (button.name.integerValue == [_selectedCards.firstObject cardId]) {
                        firstSelCard.isSelected = NO;
                        button.zOrder = button.oldZOrder;
                        button.selected = NO;
                        button.position = ccp(button.position.x, cardPosY);
                        break;
                    }
                }
                [_selCardButtons removeObjectAtIndex:0];
                [_selectedCards removeObjectAtIndex:0];
                
                if (!self.player.heroSkill && !self.player.equipmentCard) {
//                  If the first selected card is same with second selected card or both are Attack, don't need clear selected targets.
                    if (firstSelCard.cardEnum != _selectedCard.cardEnum && (!firstSelCard.isAttack || !_selectedCard.isAttack)) {
                        [self.player clearSelectedTargetPlayers];
                    }
                }
            }
        }
        @catch (NSException *exception) {
            NSLog(@"Exception: %@ in selector %@", exception.description, NSStringFromSelector(_cmd));
        }
    }
    
    if (GameStatePlaying == self.player.game.state) {
        if (!self.player.heroSkill && !self.player.equipmentCard) {
            [self.player addTextPrompt];
        }
        [self.player.heroSkill checkHandCardEnablement];
    }
    
    [self.player checkPlayingButtonEnablementWithSelectedCard:_selectedCard];
    [self.player checkTargetPlayerEnablement];
}

- (void)cancelFirstSelectedCard
{
    if (_selCardButtons.firstObject) {
        [_selCardButtons.firstObject setSelected:NO];
        [self cardSelected:_selCardButtons.firstObject];
    }
}

#pragma mark - Hand card using
/*
 * 1. Use hand card/equip equipment with effect animation(Yes/No)
 * 2. Set selected hand card ids by self player
 * 3. Discard excess hand card
 */
- (void)playHandCardAnimated:(BOOL)animated block:(void (^)())block
{
    if (animated) {
        [self.player.game runWithCard:_selectedCards.lastObject
                             ofPlayer:self.player
                           atPosition:ccp(SCREEN_WIDTH/2, SCREEN_HEIGHT*0.35)];
    }
    
    self.player.playedCardIds = [BGHandCard handCardIdsByCards:_selectedCards ofPlayer:self.player];
    
    if (self.player.isEquipEquipmentCard) {
        [self equipEquipmentCard];
    } else {
        if (GameStateGiving == self.game.state) {
            [self moveSelectedCardToPlayer];
        } else {
            [self moveSelectedCardToTable];
        }
    }
    
    [self updateHandCardBuffer];
    [self makeHandCardLeftAlignment];
    [self runDelayWithDuration:DURATION_CARD_MOVE block:block];
    
    if ((ActionPlayCard == self.player.game.action ||
        ActionChooseCardToUse == self.player.game.action)) {
        [_selectedCard playSound];
    }
}

// Auto play hand card from available card id list
- (void)playHandCardAfterTimeIsUpWithBlock:(void (^)())block
{
    [self resetSelectedHandCard];
    
    if (!self.player.availableCardIds) {
        self.player.availableCardIds = [BGPlayingCard playingCardIdsByCards:_handCards];
    }
    
    for (NSUInteger i = 0; i < self.player.selectableCardCount; i++) {
        NSInteger cardId = [self.player.availableCardIds[i] integerValue];
        BGHandCard *card = [self getHandCardByCardId:cardId];
        NSUInteger idx = [_handCards indexOfObject:card];
        if (idx != NSNotFound) {
            [_selCardButtons addObject:self.children[idx]];
            [_selectedCards addObject:_handCards[idx]];
        }
    }
    
    [self playHandCardAnimated:NO block:block];
}

- (void)moveSelectedCardToTable
{
    for (BGCardButton *cardButton in _selCardButtons) {
        cardButton.enabled = cardButton.selected = NO;
        cardButton.zOrder = 0;
        [cardButton removeFromParent];
    }
    
    [self.game.table showPlayedCardWithCardNodes:_selCardButtons andClearTable:YES];
}

- (void)moveSelectedCardToPlayer
{
    CCLayoutBox *layoutBox = [CCLayoutBox node];
    layoutBox.spacing = -PLAYING_CARD_WIDTH/_selCardButtons.count;
    layoutBox.anchorPoint = ccp(0.5f, 0.5f);
    layoutBox.position = self.player.centerPosition;
    
    for (BGCardButton *cardButton in _selCardButtons) {
        cardButton.enabled = cardButton.selected = NO;
        cardButton.zOrder = 0;
        [cardButton removeFromParent];
        [layoutBox addChild:cardButton];
    }
    
    BGPlayer *player = (self.player.isTurnOwner) ? self.player.targetPlayer : self.game.turnOwner;
    [self.game moveCard:layoutBox toPlayer:player];
}

/*
 * Select a equipment card to equip
 */
- (void)equipEquipmentCard
{
    [self.game.table clearExistingCards];
    BGPlayer *equippedPlayer = (self.player.targetPlayer) ? self.player.targetPlayer : self.player;
    [equippedPlayer.equipment addEquipmentWithCardIds:self.player.playedCardIds];
    [_selCardButtons.lastObject removeFromParent];
}

/*
 * Add an got(抽到的) hand card or equipment into hand and face down it
 */
- (void)addAndFaceDownOneCard:(CCButton *)cardButton
{
    cardButton.enabled = NO;
    [self addChild:cardButton z:self.children.count];
    _facedDownCardCount += 1;
}

- (void)resetSelectedHandCard
{
    for (BGHandCard *card in _handCards) {
        card.isSelected = NO;
    }
    
    [self makeHandCardLeftAlignment];
    [self clearSelectedCardBuffer];
}

@end
