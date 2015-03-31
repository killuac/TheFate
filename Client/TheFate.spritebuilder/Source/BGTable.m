//
//  BGTable.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGTable.h"
#import "BGGameScene.h"

@interface BGTable ()

@property (nonatomic, weak) BGGameScene *game;
@property (nonatomic, weak) BGPlayer *player;
@property (nonatomic) NSUInteger allCardCount;

@end

@implementation BGTable {
    NSTimer *_timer;
    BGHeroInformation *_heroInformation;
}

- (BGGameScene *)game
{
    return (BGGameScene *)self.parent;
}

- (BGPlayer *)player
{
    return self.game.selfPlayer;
}

- (NSUInteger)allCardCount
{
    _allCardCount = self.children.count;
    for (CCNode *node in self.children) {
        if (!node.visible) _allCardCount--;
    }
    return _allCardCount;
}

/*
 * Check if need clear existing card on the table before adding card
 */
- (void)checkCardClearableWithCount:(NSUInteger)count
{
    if (GameStatePlaying == self.game.state ||
        GameStateDiscarding == self.game.state ||
        self.allCardCount+count > COUNT_MAX_TABLE_CARD) {
        [self clearExistingCards];
    }
}

/*
 * Clear the existing cards on the table after one card effect was resolved(结算)
 */
- (void)clearExistingCards
{
    [self removeAllChildren];
}

/*
 * Remove popup after play time is up
 */
- (void)scheduleRemovePopup
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:self.game.playTime+SCHEDULE_DALAY_TIME
                                              target:self
                                            selector:@selector(removePopup)
                                            userInfo:nil
                                             repeats:NO];
}

- (void)removePopup
{
    [_timer invalidate];
    
    [self.game.effectNode clearEffect];
    [self.game removePopupMaskNode];
    
    [_heroCardBox removeAllChildren];
    [_heroCardBox removeFromParent];
    [_heroInformation removeFromParent];
    
    [_cardPopup removeAllChildren];
    [_cardPopup removeFromParent];
    
    _cardPopup = nil;
    _isShowHandCard = _isAssigningCard = NO;
    
    [self.player resetAndRemovePlayingNodes];
}

#pragma mark - Table showing
/*
 * Show the candidate heros on the table
 */
- (void)showCandidateHerosWithHeroIds:(NSArray *)heroIds
{
    [self.game makeBackgroundToDark];
    if (IS_PHONE) {
        CGPoint pos = self.player.progressBar.position;
        self.player.progressBar.position = ccp(pos.x, pos.y*0.8f);
    }
    
    NSArray *heroCards = [BGHeroCard heroCardsWithHeroIds:heroIds];
    [_heroCardBox removeFromParent];
    _heroCardBox = [BGButtonFactory createCardButtonBoxWithCards:heroCards forTarget:self];
    _heroCardBox.spacing = PADDING_CANDIDATE_HEROS;
    _heroCardBox.position = CANDIDATE_HERO_POSITION;
    [self showCardPopup:_heroCardBox];
    
    [self scheduleRemovePopup];
}

- (void)showCardPopup:(CCNode *)node
{
    [self.game addChild:node z:300];
}

/*
 * Arrange the compared card's index, put the compared card of self player as first one.
 */
- (NSArray *)sortComparedCardIds:(NSArray *)cardIds
{
    NSMutableArray *mutableCardIds = [cardIds mutableCopy];
    NSMutableIndexSet *idxSet = [NSMutableIndexSet indexSet];
    NSUInteger idx = 0;
    
    for (id obj in cardIds) {
        if ([obj integerValue] == self.player.comparedCardId) {
            [mutableCardIds removeObjectsAtIndexes:idxSet];
            [mutableCardIds addObjectsFromArray:[cardIds objectsAtIndexes:idxSet]];
            cardIds = mutableCardIds;
            break;
        }
        [idxSet addIndex:idx]; idx++;
    }
    
    return cardIds;
}

/*
 * 1. Show all compared cards of other players on the table. So the index start from 1.
 *    (The comapred card of self player already showed on the table after used it)
 * 2. Send StartRound message after card animation running finished
 */
- (void)showComparedCardWithCardIds:(NSArray *)cardIds maxCardId:(NSInteger)maxCardId
{
//  Face down the compared card of other player first
    NSArray *cardBacks = [BGButtonFactory createCardBackButtonsWithCount:cardIds.count-1 forTarget:nil];
    [cardBacks enumerateObjectsUsingBlock:^(CCButton *cardBack, NSUInteger idx, BOOL *stop) {
        [self addChild:cardBack];
        @try {
            BGPlayer *player = self.game.allPlayers[idx+1];
            cardBack.position = player.centerPosition;
        } @catch (NSException *exception) {
            // To avoid crash
        }
    }];
    
//  Face up all cards by flipping from left after movement
    NSArray *arrangedCardIds = [self sortComparedCardIds:cardIds];
    NSArray *cards = [BGPlayingCard playingCardsByCardIds:arrangedCardIds];
    void(^block)(CCButton *cardBack) = ^(CCButton *cardBack) {
        NSUInteger idx = [self.children indexOfObject:cardBack];
        if (0 == idx) {     // First card is used by self player
            ((BGCardButton *)cardBack).heroLabel.string = [self.game.allPlayers[idx] heroName];
            return;
        }
        
        BGCardButton *cardButton = [BGButtonFactory createCardButtonWithCard:cards[idx] forTarget:nil];
        cardButton.heroLabel.string = [self.game.allPlayers[idx] heroName];
        cardButton.visible = cardButton.enabled = NO;
        cardButton.position = cardBack.position;
        [self addChild:cardButton];
        
//      1. Face up the card
        CCTime duration = DURATION_CARD_FLIP+(idx-1)*DURATION_CARD_FLIP_INTERVAL;
        [cardBack runFlipFromLeftWithDuration:duration toNode:cardButton block:nil];
        
        if (++idx != cardIds.count) return;
        
//      2. If it is the last compared card, find the max compared card and scale up it.
        [self runDelayWithDuration:duration+DURATION_CARD_MOVE block:^{
            NSUInteger index = [arrangedCardIds indexOfObject:@(maxCardId)];
            CCButton *cardBack = self.children[index];
            cardBack.zOrder += 2*cardIds.count;   // Scale up at the foremost screen
            
            for (CCButton *button in self.children) {
                if (![button isEqual:cardBack])
                    button.color = DISABLED_COLOR;
            }
            
            void(^block)() = ^() {
                [self clearExistingCards];
                [[BGClient sharedClient] sendStartRoundRequest];    // 牌局开始
            };
            [cardBack runScaleAndReverseWithDuration:DURATION_CARD_SCALE scale:CARD_SCALE_UP block:block];
        }];
    };
    
//  Move all compared cards to table
    [self.children enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [obj runEaseMoveWithDuration:DURATION_CARD_MOVE
                            position:[self cardTargetPositionWithIndex:idx playerCount:self.game.playerCount]
                              object:obj
                               block:block];
    }];
}

/*
 * Compared or assigned card target position setting
 */
- (CGPoint)cardTargetPositionWithIndex:(NSUInteger)idx playerCount:(NSUInteger)playerCount
{
    CGFloat cardWidth = PLAYING_CARD_WIDTH;
    CGFloat cardHeight = PLAYING_CARD_HEIGHT;
    
    NSUInteger rowCount = ceil((double)playerCount/(COUNT_MAX_TABLE_CARD));
    NSUInteger colCount = ceil((double)playerCount/rowCount);
    CGFloat padding = PADDING_COMPARED_CARD;
    
    CGFloat startPosX = TABLE_AREA_CENTER.x - (colCount-1)*cardWidth/2;
    CGFloat delta = (idx < colCount) ? idx*(cardWidth+padding) : (idx-colCount)*(cardWidth+padding);
    CGFloat cardPosX = startPosX + delta;
    
    CGFloat startPosY = (1 == rowCount) ? TABLE_AREA_CENTER.y : TABLE_AREA_CENTER.y+cardHeight/3;
    CGFloat cardPosY = (idx < colCount) ? startPosY : (startPosY-cardHeight-padding);
    
    return ccp(cardPosX, cardPosY);
}

/*
 * Make each card on the table center alignment
 */
- (void)makePlayedCardCenterAlignment
{
    NSUInteger halfCount = self.allCardCount / 2;
    CGFloat cardPadding = PLAYING_CARD_PADDING(self.allCardCount, COUNT_MAX_TABLE_CARD);
    CGPoint startPos = (self.allCardCount%2 == 0) ? ccpAdd(TABLE_AREA_CENTER, ccp(PLAYING_CARD_WIDTH/2, 0.0f)) : TABLE_AREA_CENTER;
    
    [self.children enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        CGPoint targetPos = CGPointZero;
        CGPoint deltaPos = ccp(abs((int)(halfCount-idx))*(PLAYING_CARD_WIDTH+cardPadding), 0.0f);
        if (idx == halfCount) {
            targetPos = startPos;
        } else if (idx < halfCount) {
            targetPos = ccpSub(startPos, deltaPos);
        } else {
            targetPos = ccpAdd(startPos, deltaPos);
        }
        
        [obj runEaseMoveWithDuration:DURATION_TABLE_CARD_MOVE position:targetPos block:nil];
    }];
}

/*
 * Show the used/discarded hand card of self player
 */
- (void)showPlayedCardWithCardNodes:(NSArray *)cardNodes andClearTable:(BOOL)isClear
{
    if (isClear) [self checkCardClearableWithCount:cardNodes.count];
    
    for (id cardNode in cardNodes) {
        if ([cardNode isKindOfClass:[CCButton class]]) [cardNode setEnabled:NO];
        [self addChild:cardNode];
    }
    
    [self makePlayedCardCenterAlignment];
}

- (void)showCardWithCardIds:(NSArray *)cardIds ofPlayer:(BGPlayer *)player
{
    NSArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:nil];
    [cardButtons enumerateObjectsUsingBlock:^(CCSprite *cardSprite, NSUInteger idx, BOOL *stop) {
        cardSprite.position = CARD_MOVE_POSITION(player.centerPosition, idx, cardButtons.count);
    }];
    
    [self showPlayedCardWithCardNodes:cardButtons andClearTable:YES];
}

/*
 * Show the used/dropped/discarded hand card or equipment card of turn owner
 */
- (void)showPlayedCardWithCardIds:(NSArray *)cardIds
{
    if (ActionChoseCardToDrop != self.game.action && GameStateDeathResolving != self.game.state) {
        [self showCardWithCardIds:cardIds ofPlayer:self.game.activePlayer]; return;
    }
    
    NSMutableArray *handCardIds = [NSMutableArray array];
    NSMutableArray *equipCardIds = [NSMutableArray array];
    for (id number in cardIds) {
        if ([self.game.activePlayer.equipment.equippedCardIds containsObject:number]) {
            [equipCardIds addObject:number];
        } else {
            [handCardIds addObject:number];
        }
    }
    
    [self showCardWithCardIds:handCardIds ofPlayer:self.game.activePlayer];
    
    if (equipCardIds.count > 0) {
        self.game.activePlayer.updateReason = UpdateReasonTable;
        [self.game.activePlayer.equipment removeEquipmentWithCardIds:equipCardIds];
    }
}

/*
 * Show the removed hand card of target player by turn owner
 */
- (void)showRemovedCardWithCardIds:(NSArray *)cardIds
{
    [self showCardWithCardIds:cardIds ofPlayer:self.game.damageSource.targetPlayer];
}

- (void)showAssignedCardWithCardIds:(NSArray *)cardIds
{
    if (!cardIds) return;
    [self clearExistingCards];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.game.damageSource];
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:nil];
    [cardButtons enumerateObjectsUsingBlock:^(BGCardButton *cardButton, NSUInteger idx, BOOL *stop) {
        cardButton.heroLabel.string = [self.game.damageSource.targetPlayers[idx] heroName];
        cardButton.enabled = NO;
        [cardButton setColorWith:DISABLED_COLOR isRecursive:YES];
        cardButton.position = self.game.damageSource.centerPosition;
        [self addChild:cardButton];
    }];
    
    [self.children enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [obj runEaseMoveWithDuration:DURATION_CARD_MOVE
                            position:[self cardTargetPositionWithIndex:idx playerCount:self.game.damageSource.targetPlayers.count]
                               block:nil];
    }];
}

#pragma mark - Show card from deck
/*
 * Face down card from top deck on the table(Waiting until card flip finished)
 */
- (void)faceDownCardFromDeckWithCount:(NSUInteger)count
{
    [self runDelayWithDuration:DURATION_CARD_FLIP block:^{
        [self showFacedDownCardWithCount:count];
    }];
}

- (void)showFacedDownCardWithCount:(NSUInteger)count
{
    [self checkCardClearableWithCount:count];
    
    CCSprite *lastCardSprite = self.children.lastObject;
    CGPoint startPos = (lastCardSprite) ?
        ccpAdd(lastCardSprite.position, ccp(PLAYING_CARD_WIDTH, 0.0f)) : TABLE_AREA_CENTER;
    
    NSArray *cardBacks = [BGButtonFactory createCardBackButtonsWithCount:count forTarget:nil];
    [cardBacks enumerateObjectsUsingBlock:^(CCSprite *cardBack, NSUInteger idx, BOOL *stop) {
        cardBack.position = ccpAdd(startPos, ccp(PLAYING_CARD_WIDTH*idx, 0.0f));
        [self addChild:cardBack];
    }];
    
    [self makePlayedCardCenterAlignment];
}

/*
 * Face up the card of top deck on the table
 */
- (void)faceUpTableCardWithCardIds:(NSArray *)cardIds
{
    NSArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:nil];
    
    __block NSUInteger index = self.allCardCount - 1;
    [cardButtons enumerateObjectsUsingBlock:^(CCSprite *cardButton, NSUInteger idx, BOOL *stop) {
        CCSprite *cardBack = [self.children objectAtIndex:index--];
        
        cardButton.visible = NO;
        cardButton.position = cardBack.position;
        [self addChild:cardButton];
        
        [cardBack runFlipFromLeftWithDuration:DURATION_CARD_FLIP toNode:cardButton block:nil];
    }];
}

/*
 * Reveal one card from deck for judgement
 */
- (void)revealCardFromDeckWithCardIds:(NSArray *)cardIds
{
    [self showFacedDownCardWithCount:cardIds.count];
    [self runDelayWithDuration:DURATION_TABLE_CARD_MOVE block:^{
        [self faceUpTableCardWithCardIds:cardIds];
    }];
}

#pragma mark - Popup showing
/*
 * Show target player hand card(暗置的) and equipment card
 * Faced down(暗置) all hand cards on the table for being drew(比如贪婪)
 */
- (void)showPopupWithHandCount:(NSUInteger)count equipmentIds:(NSArray *)cardIds
{
    [self.game makeBackgroundToDark];
    
//  Popup window
    _cardPopup = (BGCardPopup *)[CCBReader load:kCcbiCardPopup];
    _cardPopup.position = TABLE_AREA_CENTER;
    CCSpriteFrameCache *frameCache = [CCSpriteFrameCache sharedSpriteFrameCache];
    if (count > 0 && cardIds.count > 0) {
        _cardPopup.spriteFrame = [frameCache spriteFrameByName:kImagePopupAllCards];
    } else {
        _cardPopup.spriteFrame = [frameCache spriteFrameByName:kImagePopupHandCard];
    }
    [self setPopupTitleLabel];
    [self showCardPopup:_cardPopup];
    
//  Add target player's hand card or equipment to popup
    if (count > 0 && cardIds.count > 0) {
        _isShowHandCard = YES;
        [self addHandCardsToPopupWithCount:count];
        [self addEquipmentToPopupWithCardIds:cardIds];
    } else if (count > 0) {
        _isShowHandCard = YES;
        [self addHandCardsToPopupWithCount:count];
    } else {
        _isShowHandCard = NO;
        [self addEquipmentToPopupWithCardIds:cardIds];
    }
    
    NSUInteger cardCount = _cardPopup.upLayoutBox.children.count;
    _cardPopup.upLayoutBox.spacing = (cardCount > COUNT_MAX_DREW_CARD) ? PLAYING_CARD_PADDING(cardCount, COUNT_MAX_DREW_CARD) : 0.0f;
    _cardPopup.downLayoutBox.spacing = 0.0f;
    
    [self scheduleRemovePopup];
}

- (void)addHandCardsToPopupWithCount:(NSUInteger)count
{
    NSArray *cardButtons = [BGButtonFactory createCardBackButtonsWithCount:count forTarget:self];
    [self addCardButtons:cardButtons toLayoutBox:_cardPopup.upLayoutBox];
}

- (void)addEquipmentToPopupWithCardIds:(NSArray *)cardIds
{
    NSArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:self];
    BGLayoutBox *layoutBox = (_cardPopup.upLayoutBox.children.count > 0) ? _cardPopup.downLayoutBox : _cardPopup.upLayoutBox;
    [self addCardButtons:cardButtons toLayoutBox:layoutBox];
}

- (void)addCardButtons:(NSArray *)cardButtons toLayoutBox:(BGLayoutBox *)layoutBox
{
    for (CCButton *cardButton in cardButtons) {
        cardButton.enabled = YES;
        cardButton.position = ccp(layoutBox.positionInPoints.x, layoutBox.positionInPoints.y+PLAYING_CARD_HEIGHT/2);
        [layoutBox addChild:cardButton animated:YES];
    }
}

- (void)setPopupTitleLabel
{
    NSString *title = (self.player.heroSkill) ? self.player.heroSkill.skillText : self.player.firstUsedCard.cardText;
    _cardPopup.titleLabel.string = (title) ? title : @"";
}

/*
 * Show assigned(待分配的) cards on the table
 */
- (void)showPopupWithAssignedCardIds:(NSArray *)cardIds
{
    _isAssigningCard = YES;
    [self.game makeBackgroundToDark];
    
//  Popup window
    NSString *imageName = FILE_FULL_NAME(kImagePopupAssignedCard, self.game.playerCount, kFileTypePNG);
    _cardPopup = (BGAssigningPopup *)[CCBReader load:kCcbiAssigningPopup owner:self];
    _cardPopup.position = (self.game.playerCount > 4) ? ASSIGNING_POPUP_POSITION : TABLE_AREA_CENTER;
    _cardPopup.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:imageName];
    [self setPopupTitleLabel];
    [self showCardPopup:_cardPopup];
    
//  Add deck card to popup, maybe card count less than alive player count (e.g. MysticSnake)
    NSMutableArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
    if (self.player.targetPlayers.count > 0) {
        [self.game.alivePlayers enumerateObjectsUsingBlock:^(BGPlayer *player, NSUInteger idx, BOOL *stop) {
            // Need a fake card to take position for pan gesture
            if (![self.player.targetPlayers containsObject:player])
                [cards insertObject:cards.firstObject atIndex:idx];
        }];
    }
    
    [self.game.alivePlayers enumerateObjectsUsingBlock:^(BGPlayer *player, NSUInteger idx, BOOL *stop) {
        BGCardSlot *cardSlot = (BGCardSlot *)[CCBReader load:kCcbiCardSlot];
        cardSlot.heroNameLabel.string = player.heroName;
        
        CCButton *cardButton = [BGButtonFactory createCardButtonWithCard:cards[idx] forTarget:self.player.handArea];
        cardButton.userInteractionEnabled = NO;     // Need disable for pan gesture
        
        // Need a fake card to take position for pan gesture
        if (self.player.targetPlayers.count > 0 && ![self.player.targetPlayers containsObject:player]) {
            [cardButton removeAllChildren];
            [cardButton setBackgroundSpriteFrame:nil forState:CCControlStateNormal];
        }
        
        CGPoint cardPos = CGPointZero;
        if (self.game.playerCount <= 4 || idx < self.game.playerCount/2) {
            [_cardPopup.upLayoutBox addChild:cardSlot];
            cardPos = ccpAdd(_cardPopup.upLayoutBox.positionInPoints, cardSlot.positionInPoints);
            cardButton.position = ccp(cardPos.x, cardPos.y-PLAYING_CARD_HEIGHT*1.32);
        } else {
            [_cardPopup.downLayoutBox addChild:cardSlot];
            cardPos = ccpAdd(_cardPopup.downLayoutBox.positionInPoints, cardSlot.positionInPoints);
            cardButton.position = ccp(cardPos.x, cardPos.y-PLAYING_CARD_HEIGHT/10);
        }
        [_cardPopup addChild:cardButton z:idx];   // Add card to popup(not cardslot) for pan guesture movement
    }];
    
    [self scheduleRemovePopup];
}

/*
 * Energy transport: assign card to each player (Send plugin message at the last loop)
 */
- (void)assignCardFinished:(CCButton *)sender
{
    [self assignCardToEachPlayer];
}

- (void)assignCardToEachPlayer
{
    NSArray *cardButtons = _cardPopup.cardButtons;
    [_cardPopup removeAllChildren];
    
//  Send assign card finish reqeust to server
    NSMutableArray *cardIds = [NSMutableArray array];
    NSMutableArray *playerNames = [NSMutableArray array];
    [cardButtons enumerateObjectsUsingBlock:^(CCButton *cardButton, NSUInteger idx, BOOL *stop) {
        if ([cardButton backgroundSpriteFrameForState:CCControlStateNormal]) {  // Not fake card. Refer to above.
            cardButton.position = [_cardPopup convertToWorldSpace:cardButton.position];
            [cardIds addObject:@(cardButton.name.integerValue)];
            
            BGPlayer *player = self.game.alivePlayers[idx];
            [playerNames addObject:player.playerName];
            if (player.isMe) {
                [player.handArea addHandCardWithCardButtons:@[cardButton]];
            } else {
                [self.game moveCard:cardButton toPlayer:player];
            }
        }
    }];
    
    [self removePopup];
    [self.game makeBackgroundToNormal];
    
    if (self.player.targetPlayers.count > 0) {
        self.player.targetPlayerNames = playerNames;    // Need assign card to specified targets, not everybody.
    }
    self.player.assignedCardIds = cardIds;
    [[BGClient sharedClient] sendAsignedCardRequest];
}

#pragma mark - Card button selector
- (void)cardSelected:(CCButton *)cardButton
{
    if ([cardButton.parent isEqual:_heroCardBox]) {
        [self selectHeroCard:cardButton];               // 待选的英雄
    } else if (cardButton.parent) {
        [self selectEquipmentCards:@[cardButton]];      // 抽取目标装备
    }
}

- (void)cardBackSelected:(CCButton *)cardBack;
{
    [self selectHandCards:@[cardBack]];   // 抽取目标手牌
}

/*
 * Select a hero card by touching hero button
 */
- (void)selectHeroCard:(CCButton *)heroButton
{
    if (heroButton.selected) {
        for (CCButton *button in _heroCardBox.children) {
            if (![button isEqual:heroButton]) button.selected = NO;
        }
        
        if (!_heroInformation) {
            _heroInformation = (BGHeroInformation *)[CCBReader load:kCcbiHeroInformation];
            [_heroInformation showFromTopInNode:self.game];
        }
        
        BGHeroCard *heroCard = [BGHeroCard cardWithCardId:heroButton.name.integerValue];
        _heroInformation.heroDesc = heroCard.cardDesc;
        return;
    }
    
    [self removePopup];
    self.player.selectedHeroId = heroButton.name.integerValue;
    heroButton.position = _heroCardBox.position;
    [self addChild:heroButton];
    
    CGPoint targetPos = ccp(self.player.character.contentSize.width*0.45, self.player.character.contentSize.height*0.6);
    [heroButton runEaseMoveScaleWithDuration:DURATION_SELECTED_HERO_MOVE
                                    position:targetPos
                                       scale:SCALE_SELECTED_HERO
                                       block:^{
                                           [self.game makeBackgroundToNormal];
                                           [heroButton removeFromParent];
                                           [[BGClient sharedClient] sendSelectHeroIdRequest];
                                       }];
}

/*
 * Greed hand card of target player
 */
- (void)selectHandCards:(NSArray *)cardBacks
{
    for (CCButton *button in _cardPopup.downLayoutBox.children) {
        button.enabled = NO;    // 选择了抽取手牌，禁用装备
    }
    [_cardPopup.downLayoutBox setColorWith:DISABLED_COLOR isRecursive:YES];
    
    for (CCButton *cardBack in cardBacks) {
        [self.player.selectedCardIdxes addObject:@(cardBack.name.integerValue)];
    }
    
    if (self.player.isGreeding) {
        [self greedCardWithCardButtons:cardBacks];
    } else {
        [self disarmCardWithCardButtons:cardBacks];
    }
}

/*
 * Greed/Disarm equipment of target player
 */
- (void)selectEquipmentCards:(NSArray *)cardButtons
{
    CCButton *cardButton = cardButtons.lastObject;
    [self.player.selectedCardIds addObject:@(cardButton.name.integerValue)];
    
//  Maybe disarm self
    self.player.updateReason = (self.player.isTarget && !self.player.isStrengthened) ? UpdateReasonTable : UpdateReasonDefault;
    [self.player.targetPlayer.equipment removeEquipmentWithCardIds:self.player.selectedCardIds];
    
    if (self.player.isGreeding) {
        self.player.selectableCardCount = 1;            // Only greed one equipment
        [self greedCardWithCardButtons:cardButtons];
    } else {
        [self disarmCardWithCardButtons:cardButtons];
    }
}

/*
 * Get a hand card of target player by touching card button
 * If only have one hand card, finish getting directly after selection.
 * (If self player is target player, set the start position with turn owner's position)
 */
- (void)greedCardWithCardButtons:(NSArray *)cardButtons
{
    [cardButtons enumerateObjectsUsingBlock:^(CCButton *cardButton, NSUInteger idx, BOOL *stop) {
        [cardButton removeFromParent];
        
        CCButton *cardBack = [BGButtonFactory createCardBackButtonsWithCount:1 forTarget:nil].lastObject;
        CGPoint pos = (self.player.isTurnOwner) ? self.player.targetPlayer.centerPosition : self.game.turnOwner.centerPosition;
        cardBack.position = CARD_MOVE_POSITION(pos, idx, cardButtons.count);
        [self.player.handArea addAndFaceDownOneCard:cardBack];
    }];
    
    [self.player.handArea makeHandCardLeftAlignment];
    
    if ([self isSelectionFinished]) {
        [[BGClient sharedClient] sendChoseCardToGetRequest];
        
        [self removePopup];
        [self.game makeBackgroundToNormal];
        
//      Maybe trigger skill "MultiCast" that make magic card resolve again. So need reset.
        [self.player.selectedCardIds removeAllObjects];
        [self.player.selectedCardIdxes removeAllObjects];
    }
}

/*
 * Get/Remove a hand card of target player by touching card button
 */
- (void)disarmCardWithCardButtons:(NSArray *)cardButtons
{
    [cardButtons enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [obj removeFromParent];
    }];
    
    if ([self isSelectionFinished]) {
        if (GameStateGetting == self.game.state) {
            [[BGClient sharedClient] sendChoseCardToGetRequest];
        } else if (GameStateRemoving == self.game.state) {
            [[BGClient sharedClient] sendChoseCardToRemoveRequest];
        }
        
        [self removePopup];
        [self.game makeBackgroundToNormal];
    }
}

// The getting/removing card count can't exceed all hand card count
- (BOOL)isSelectionFinished
{
    return ((self.player.selectedCardIdxes.count == self.player.targetPlayer.handCardCount) ||
            (self.player.selectedCardIdxes.count == self.player.selectableCardCount) ||
            (self.player.selectedCardIds.count == self.player.selectableCardCount));
}

@end
