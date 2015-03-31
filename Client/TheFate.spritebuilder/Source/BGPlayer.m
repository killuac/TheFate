//
//  BGPlayer.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPlayer.h"
#import "BGGameScene.h"

@implementation BGPlayer {
    NSTimer *_timer;
    NSMutableArray *_allPlayedCardIds;
}

static NSArray *tipTexts;

@synthesize handCardCount = _handCardCount;
@synthesize lastUsedCard = _usedCard;

- (id)init
{
    if (self = [super init]) {
        _selectedHeroId = HeroCardNull;
        _playedCardIds = [NSMutableArray array];
        _allPlayedCardIds = [NSMutableArray array];
        _selectedCardIds = [NSMutableArray array];
        _selectedCardIdxes = [NSMutableArray array];
        _targetPlayerNames = [NSMutableArray array];
        _heroSkillId = HeroSkillNull;
        _equipmentId = PlayingCardNull;
        
        _gameHistory = [NSMutableArray array];
        
        if (!tipTexts) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistTextPrompt ofType:kFileTypePLIST];
            tipTexts = [NSArray arrayWithContentsOfFile:path];
        }
    }
    return self;
}

- (void)didLoadFromCCB
{
//  Adjust self player area's width for iPhone5
	if (self.isMe && IS_IPHONE5) {
        self.scaleX += self.scaleX * IPHONE5_ADDITIONAL_SCALEX;
        for (CCNode *node in self.children) {
            if ([node.name isEqual:@"playingButton"]) continue;
            
            node.scaleX = 1 / self.scaleX;
            node.scaleY = 1 / self.scaleY;
            
            if ([node.name isEqual:@"role"]) {
                node.scaleX *= 0.9f;
                node.scaleY *= 0.9f;
            }
        }
        _roleButton.position = ccp(_roleButton.position.x*0.855f, _roleButton.position.y);
    }
    
    _roleBox.zOrder = 10;
}

- (void)setNickName:(NSString *)nickName
{
    _nickNameLabel.string = _nickName = (nickName) ? nickName : self.playerName;
}

- (void)setPlayerName:(NSString *)playerName
{
    _playerName = playerName;
    _game = (BGGameScene *)self.parent.parent;
    
    _distance = 1;
    _plusDistance = 0;
    _minusDistance = 0;
    _attackRange = 1;
}

- (NSUInteger)seatIndex
{
    return [self.game.alivePlayers indexOfObject:self];
}

- (CGPoint)centerPosition
{
    if (self.isMe) {
        return ccp(CGRectGetMidX(self.boundingBox), CGRectGetMaxY(self.boundingBox)*0.7f);
    } else {
        return ccp(CGRectGetMidX(self.boundingBox), CGRectGetMidY(self.boundingBox));
    }
}

- (CGSize)bgContentSize
{
    CGFloat width = self.contentSizeInPoints.width;
    CGFloat height = self.contentSizeInPoints.height;
    return CGSizeMake(width*self.scaleX, height*self.scaleY);
}

- (BOOL)isEqual:(id)object
{
    return ([object respondsToSelector:@selector(playerName)]) ? [_playerName isEqual:[object playerName]] : NO;
}

- (BOOL)isMe
{
    return (0 == self.name.intValue);   // First index is self player
}

- (BOOL)isTurnOwner
{
    return [_game.turnOwnerName isEqualToString:_playerName];
}

- (BOOL)isDamageSource
{
    return [_game.damageSourceName isEqualToString:_playerName];
}

- (BOOL)isTarget
{
    return [_game.damageSource.targetPlayerNames containsObject:_playerName];
}

- (void)setTargetPlayerNames:(NSMutableArray *)targetPlayerNames
{
    if (targetPlayerNames.count > 0) _targetPlayerNames = targetPlayerNames;
}

- (NSArray *)targetPlayers
{
    NSMutableArray *players = [NSMutableArray arrayWithCapacity:_targetPlayerNames.count];
    for (NSString *playerName in _targetPlayerNames) {
        [players addObject:[_game playerWithName:playerName]];
    }
    
    return players;
}

- (BGPlayer *)targetPlayer
{
    return self.targetPlayers.firstObject;
}

- (NSString *)heroName
{
    return _character.heroName;
}

- (NSUInteger)attackRange
{
    return (_attackRange + abs((int)_minusDistance));
}

- (NSUInteger)selectedCardCount
{
    return self.selectedHandCardCount + self.selectedEquipCount;
}

- (NSUInteger)selectedHandCardCount
{
    return _handArea.selectedCards.count;
}

- (NSUInteger)selectedEquipCount
{
    return _equipment.selectedCards.count;
}

- (BOOL)isOkayEnabled
{
    return (_targetPlayerNames.count >= _requiredTargetCount &&
            (self.selectedCardCount >= MAX(_requiredSelCardCount, 1) || _isNoNeedCard));
}

#pragma mark - Buffer handling
- (void)resetValueAfterResolved
{
    _availableCardIds = nil;
    _availableSkillIds = nil;
    _availableEquipIds = nil;
    _preTargetPlayerNames = nil;
    [_playedCardIds removeAllObjects];
    [_allPlayedCardIds removeAllObjects];
    [self resetSelectedCardIds];
    [_targetPlayerNames removeAllObjects];
    _selectedColor = CardColorNull;
    _selectedSuits = CardSuitsNull;
    _heroSkillId = HeroSkillNull;
    _equipmentId = PlayingCardNull;
    _transformedCardId = PlayingCardNull;
    
    _selectableCardCount = 1;
    _requiredSelCardCount = 1;
    _requiredTargetCount = 0;
    _maxTargetCount = 0;
    _isStrengthened = NO;
    _isRequiredDrop = NO;
    
    _isGreeding = NO;
    _isStrengthening = NO;
    
    [_character resetHeroSkill];
    [_equipment resetSelectedEquipment];
    [_handArea resetSelectedHandCard];
}

- (void)resetSelectedCardIds
{
    [_selectedCardIds removeAllObjects];
    [_selectedCardIdxes removeAllObjects];
}

- (void)resetSelectionHaloOfTargetPlayers
{
    for (NSString *name in _targetPlayerNames) {
        BGPlayer *player = [self.game playerWithName:name];
        player.character.selectionHalo.visible = NO;
    }
}

- (void)scheduleRemovePlayingNodes
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:self.game.playTime+SCHEDULE_DALAY_TIME
                                              target:self
                                            selector:@selector(resetAndRemovePlayingNodes)
                                            userInfo:nil
                                             repeats:NO];
}

- (void)resetAndRemovePlayingNodes
{
    [_timer invalidate];
    
    [self removePlayingButtons];
    [self removeProgressBar];
    [self removeTextPrompt];
    
    if (self.isMe) {
        [_handArea resetSelectedHandCard];
        [self resetSelectionAndEnablement];
    }
}

- (void)resetSelectionAndEnablement
{
    _maxTargetCount = _requiredTargetCount = 0;
    
    [_handArea disableAllHandCardsWithNormalColor];
    [_character disableHeroAvatarAndRemoveSkills];
    [_equipment disableAllEquipments];
    
    [self resetSelectionHaloOfTargetPlayers];
    [self disablePlayerAreaWithNormalColor];
    [_game disablePlayerAreaForAllPlayers];
}

#pragma mark - Player area
- (void)enablePlayerArea
{
    [_character enableHeroAvatar];
    if (!self.isMe) [self setColorWith:NORMAL_COLOR isRecursive:YES];
}

- (void)disablePlayerAreaWithNormalColor
{
    [_character disableHeroAvatar];
    if (!self.isMe) [self setColorWith:NORMAL_COLOR isRecursive:YES];
}

- (void)disablePlayerAreaWithDarkColor
{
    [_character disableHeroAvatar];
    if (!self.isMe) [self setColorWith:DARK_COLOR isRecursive:YES];
}

/*
 * Check which player can be selected as target while selecting a hand card
 */
- (void)checkTargetPlayerEnablement
{
    BGHandCard *card = (self.equipmentCard) ? self.equipmentCard : _handArea.selectedCard;
//  e.g. Greed strengthen and choose card to give target, return directly.
    if (_requiredTargetCount == 0 && GameStatePlaying != self.game.state) return;
    
    if (self.heroSkill) {
        if (!self.heroSkill.isSelected || !self.heroSkill.isTargetable) {
            [self clearSelectedTargetPlayers];
            return;
        }
    } else {
        if (!card.isSelected || !card.isTargetable) {
            [self clearSelectedTargetPlayers];      // For card Chakra
            return;
        }
    }
    
    id<BGChecker> checker = (self.heroSkill) ? self.heroSkill : card;
    
    for (BGPlayer *player in _game.alivePlayers) {
        if (card.isSelected || (self.heroSkill && self.heroSkill.isTargetable)) {
            if ([checker checkPlayerEnablement:player]) {
                [player enablePlayerArea];
            } else {
                [player disablePlayerAreaWithDarkColor];
            }
        }
        else {
            [player disablePlayerAreaWithNormalColor];
        }
    }
    
//  Auto select target player
    if (_game.alivePlayerCount == 2 && _targetPlayerNames.count == 0) {
        if (self.heroSkill) {
            if (self.heroSkill.targetCount == 1) [self selectTargetPlayer];
        } else {
            if (card.targetCount == 1) [self selectTargetPlayer];
        }
    }
}

- (void)selectTargetPlayer
{
    BGPlayer *tarPlayer = _game.alivePlayers.lastObject;
    
    if (tarPlayer.character.heroAvatar.enabled) {
        tarPlayer.character.heroAvatar.selected = YES;
        [tarPlayer.character selectTargetPlayer];
    }
}

- (void)clearSelectedTargetPlayers
{
    [self resetSelectionHaloOfTargetPlayers];
    [_targetPlayerNames removeAllObjects];
    [_game disablePlayerAreaForAllPlayers];
}

#pragma mark - Player role
- (void)renderRoleWithRoleId:(NSInteger)roleId animated:(BOOL)animated
{
    _roleCard = [BGRoleCard cardWithCardId:roleId];
    NSString *roleImage = (_roleCard) ? _roleCard.imageName : kImageRoleUnknow;
    CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:roleImage];
    [_roleButton setBackgroundSpriteFrame:spriteFrame forState:CCControlStateNormal];
    
    if (!self.isMe && animated) [self.userObject runAnimationsForSequenceNamed:@"ShowRole"];
}

- (void)showAllRolesMark:(CCButton *)sender
{
    _roleButton.visible = NO;
    _roleBox.visible = YES;
}

- (void)markRole:(CCButton *)sender
{
    _roleButton.visible = YES;
    _roleBox.visible = NO;
    [self renderRoleWithRoleId:[sender.name integerValue] animated:NO];
}

#pragma mark - Character(Hero)
- (void)renderHeroWithHeroId:(NSInteger)heroId
{
    _selectedHeroId = heroId;
    [_character renderHeroWithHeroId:heroId];
    if (!self.isMe) [self renderHandCardCountFrame];
    
    [self.game makeBackgroundToNormal];
}

// Render hand card count frame for other player's hero
- (void)renderHandCardCountFrame
{
    CCSpriteFrameCache *frameCache = [CCSpriteFrameCache sharedSpriteFrameCache];
    _handCardCountFrame.spriteFrame = [frameCache spriteFrameByName:_character.heroCard.attrImageName];
}

/*
 * Update hero health and skill point
 */
- (void)updateHeroWithHealthPoint:(NSInteger)hp skillPoint:(NSUInteger)sp
{
    if (hp > 0 && _helpMeMark.visible) {    // 被救活
        _helpMeMark.visible = NO;
        _escapedMark.visible = _isEscaped;
        [self stopAllActions];
    }
    
    if (hp < _character.healthPoint) _game.damagedPlayer = self;
    if (hp <= 0) _game.dyingPlayer = self;
    
    [_character updateHealthPointWithCount:hp];
    [_character updateSkillPointWithCount:sp];
}

- (void)enableHeroSkillWithSkillIds:(NSArray *)skillIds
{
    _availableSkillIds = skillIds;
    if (skillIds.count > 0) [_character enableHeroSkillWithSkillIds:skillIds];
    
    if (self.heroSkill && !_game.isWaitingDispel && GameStatePlayerDying != self.game.state) {
        [self.heroSkill checkHandCardEnablement];
    }
}

#pragma mark - Hand area
/*
 * Initialize hand cards with dealing cards for self player
 */
- (void)dealHandCardWithCardIds:(NSArray *)cardIds
{
    _availableCardIds = cardIds;
    [_handArea dealHandCardWithCardIds:cardIds];
}

/*
 * Update(Draw/Used) hand card for self player
 */
- (void)updateHandCardWithCardIds:(NSArray *)cardIds reason:(NSInteger)reason
{
    _updateReason = reason;
    [_handArea updateHandCardWithCardIds:cardIds];
}

/*
 * Draw card animation for turn owner that is not self player
 */
- (void)drawCardWithCardCount:(NSInteger)count
{
    CCLayoutBox *layoutBox = [BGButtonFactory createCardBackBoxWithCount:count forTarget:nil];
    layoutBox.anchorPoint = ccp(0.5f, 0.5f);
    layoutBox.position = TABLE_AREA_CENTER;
    [_game moveCard:layoutBox toPlayer:self];
}

/*
 * Get hand card from table for turn owner
 */
- (void)getCardFromTableWithCardIds:(NSArray *)cardIds
{
//  Make the got card(on the table) with dark color
    NSArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
    NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:cards forTarget:self.handArea];
    [cardButtons enumerateObjectsUsingBlock:^(CCButton *obj, NSUInteger idx, BOOL *stop) {
        obj.position = SCREEN_CENTER;   // Maybe all cards on table were cleared
        for (CCButton *cardButton in _game.table.children) {
            if ([obj.name isEqualToString:cardButton.name]) {
                [cardButton setColorWith:DISABLED_COLOR isRecursive:YES];
                obj.position = cardButton.position;
            }
        }
    }];
    
    if (self.isMe) {
        [_handArea addHandCardWithCardButtons:cardButtons];
    } else {
        [_game moveCards:cardButtons toPlayer:self];
    }
}

/*
 * Get faced down/up card from other player
 * Turn owner gets card from target player, target player gets from turn owner.
 */
- (void)getCardFromPlayerWithCardIds:(NSArray *)cardIds cardCount:(NSUInteger)count
{
//  If self player is turn owner or target, his hand card update is informed by sever(Update action)
    if (_game.selfPlayer.isTurnOwner || _game.selfPlayer.isTarget) return;
    
//  Determine card movement target according to current player
    BGPlayer *fromPlayer, *toPlayer;
    if (self.isTurnOwner) {
        fromPlayer = _game.turnOwner.targetPlayer;
        toPlayer = _game.turnOwner;
    } else {
        fromPlayer = _game.turnOwner;
        toPlayer = _game.turnOwner.targetPlayer;
    }
    
//  抽取装备
    if (cardIds.count > 0) {
        [_game.turnOwner.targetPlayer.equipment removeEquipmentWithCardIds:cardIds];
        [self moveCardWithCardIds:cardIds
                     fromPosition:fromPlayer.centerPosition
                         toPlayer:toPlayer];
    }
    
//  抽取手牌
    [self moveCardWithCardCount:count
                   fromPosition:fromPlayer.centerPosition
                       toPlayer:toPlayer];
}

/*
 * Give faced down/up hand card to target player
 */
- (void)giveCardToPlayerWithCardIds:(NSArray *)cardIds cardCount:(NSUInteger)count
{
//  If self player is turn owner or target, his hand card is updated by action kActionPlayerUpdateHandCard
    if (_game.selfPlayer.isTurnOwner || _game.selfPlayer.isTarget) return;
    
//  给牌(明置)
    [self moveCardWithCardIds:cardIds
                 fromPosition:_game.turnOwner.centerPosition
                     toPlayer:_game.turnOwner.targetPlayer];
    
//  给牌(暗置)
    [self moveCardWithCardCount:count
                   fromPosition:_game.turnOwner.centerPosition
                       toPlayer:_game.turnOwner.targetPlayer];
}

- (void)moveCardWithCardIds:(NSArray *)cardIds fromPosition:(CGPoint)fromPos toPlayer:(BGPlayer *)player
{
    if (cardIds.count > 0) {
        NSArray *cards = [BGPlayingCard playingCardsByCardIds:cardIds];
        CCLayoutBox *layoutBox = [BGButtonFactory createCardButtonBoxWithCards:cards forTarget:nil];
        layoutBox.position = fromPos;
        [_game moveCard:layoutBox toPlayer:player];
    }
}

- (void)moveCardWithCardCount:(NSUInteger)count fromPosition:(CGPoint)fromPos toPlayer:(BGPlayer *)player
{
    if (count > 0) {
        CCLayoutBox *layoutBox = [BGButtonFactory createCardBackBoxWithCount:count forTarget:nil];
        layoutBox.position = fromPos;
        [_game moveCard:layoutBox toPlayer:player];
    }
}

/*
 * Remove hand/equipment card of target player(e.g. Disarm)
 */
- (void)removeCardToTableWithCardIds:(NSArray *)cardIds
{
    if (_game.selfPlayer.isTarget) return;  // Ditto
    
    BGHandCard *equipCard = [self.targetPlayer.equipment equipmentByCardId:[cardIds.lastObject integerValue]];
    if (equipCard) {
        [self.targetPlayer.equipment removeEquipmentWithCardIds:cardIds];   // Equipment card
    } else {
        [_game.table showRemovedCardWithCardIds:cardIds];                   // Hand card
    }
}

/*
 * Make hand card can be selected to use
 */
- (void)enableHandCardWithCardIds:(NSArray *)cardIds
{
    _availableCardIds = cardIds;
    [_handArea enableHandCardWithCardIds:cardIds];
}

#pragma mark - Equipment area
- (void)updateEquipmentWithCardIds:(NSArray *)cardIds reason:(NSInteger)reason
{
    _updateReason = reason;
    if (!self.isEquipEquipmentCard) {
        [_equipment updateEquipmentWithCardIds:cardIds];
    }
}

- (void)enableEquipmentWithCardIds:(NSArray *)cardIds isUse:(BOOL)isUse
{
    NSMutableArray *equipCardIds = [cardIds mutableCopy];
    
    if (isUse) {
        _availableEquipIds = cardIds;
    } else {
        _droppableEquipIds = cardIds;
        if (self.equipmentCard && self.equipmentCard.isActiveLaunchable) {  // e.g.MysticStaff
            [equipCardIds addObject:@(self.equipmentId)];
        }
    }
    
    if (equipCardIds.count > 0) [_equipment enableEquipmentWithCardIds:equipCardIds];
}

#pragma mark - Hand card count
- (void)setHandCardCount:(NSUInteger)handCardCount
{
    _handCardCount = handCardCount;
    if (!self.isMe) _handCardCountLabel.string = @(handCardCount).stringValue;
}

- (NSUInteger)handCardCount
{
    return (self.isMe) ? _handArea.handCards.count : _handCardCount;
}

#pragma mark - Use card and skill/equipment
- (void)startTurn
{
    if ([[NSUserDefaults standardUserDefaults] boolForKey:kVibration]) {
        [[BGAudioManager sharedAudioManager] playStartTurn];
    }
}

/*
 * Turn owner(self player) use/give/drop/discard hand or equipment card
 */
- (void)playHandCard
{
    [_handArea playHandCardAnimated:[self isPlayAnimation] block:^{
        switch (_game.state) {
            case GameStateComparing:
                _comparedCardId = [_playedCardIds.lastObject integerValue];
                [[BGClient sharedClient] sendChoseCardToCompareRequest];
                break;
                
            case GameStatePlaying:
//              Use hero skill by drop hand card
                if (ActionChooseCardToDrop == _game.action) {
                    [[BGClient sharedClient] sendChoseCardToDropRequest];
                } else {
                    if (!self.isNeedTargetAgain) {
                        [[BGClient sharedClient] sendUseHandCardRequest];
                    }
                }
                break;
                
            case GameStateCardChoosing:
            case GameStateWaitingDispel:
            case GameStateDeathResolving:   // QuellingBlade
                [[BGClient sharedClient] sendChoseCardToUseRequest];
                break;
                
            case GameStatePlayerDying:
                if (ActionChooseCardToDrop == self.game.action) {
                    [[BGClient sharedClient] sendChoseCardToDropRequest];
                } else {
                    [[BGClient sharedClient] sendChoseCardToUseRequest];
                }
                break;
                
            case GameStateGiving:
                [[BGClient sharedClient] sendChoseCardToGiveRequest];
                break;
                
            case GameStateDropping:
                [[BGClient sharedClient] sendChoseCardToDropRequest];
                break;
                
            case GameStateDiscarding:
                [[BGClient sharedClient] sendOkayToDiscardRequest];
                break;
                
            default:
                break;
        }
    }];
    
    [_equipment removeSelectedEquipments];  // 有时可以弃任意牌发动技能(包括装备)
    
    if (self.isDamageSource && _handArea.selectedCard.isTargetable && !self.isNeedTargetAgain) {
        [self updateTargetAndShowLinePath];
    }
    
    [[BGAudioManager sharedAudioManager] playCardUse];
}

- (BOOL)isPlayAnimation
{
    return ((GameStatePlaying == _game.state || GameStateCardChoosing == _game.state) &&
            (1 == self.selectedHandCardCount || 1 == _playedCardIds.count) && !self.heroSkill && !self.equipmentCard);
}

- (void)updateTargetAndShowLinePath
{
    if (GameStatePlaying != self.game.state) return;
    
    if (_handArea.selectedCard.isWildAxe) {
        [self chooseTargetsOfShortestDistance];
    }
    [self showTargetLinePath];
}

- (BOOL)isNeedTargetAgain
{
    if (self.heroSkill || self.equipmentCard || !self.firstUsedCard.isWildAxe) return NO;
    
    NSUInteger index = [self.game.alivePlayers indexOfObject:self.targetPlayer];
    NSUInteger halfCount = self.game.alivePlayerCount / 2;
    return (self.game.alivePlayerCount > 2 && self.game.alivePlayerCount%2 == 0 && index == halfCount);
}

- (NSUInteger)targetIndex
{
    return [self.game.alivePlayers indexOfObject:self.targetPlayer];
}

- (void)chooseTargetsOfShortestDistance
{
    NSUInteger halfCount = self.game.alivePlayerCount / 2;
    NSUInteger startIdx = ([self targetIndex] <= halfCount) ? 1 : [self targetIndex];
    
    [self updateTargetPlayerNamesFromIndex:startIdx];
}

- (void)updateTargetPlayerNamesFromIndex:(NSUInteger)startIdx
{
    NSUInteger count = MIN([self targetIndex], self.game.alivePlayerCount-[self targetIndex]);
    
    [self.targetPlayerNames removeAllObjects];
    for (NSUInteger i = startIdx; i < startIdx+count; i++) {
        BGPlayer *tarPlayer = self.game.alivePlayers[i];
        [self.targetPlayerNames addObject:tarPlayer.playerName];
    }
}

- (BOOL)isEquipEquipmentCard
{
    return (GameStatePlaying == _game.state && 1 == _playedCardIds.count &&
            self.firstUsedCard.isEquipment && !self.heroSkill && !self.equipmentCard);
}

- (void)useHeroSkill
{
    if (self.heroSkill.isSimple) [self resetAndRemovePlayingNodes];
    [[BGClient sharedClient] sendUseHeroSkillRequest];
}

- (void)useEquipment
{
    [[BGClient sharedClient] sendUseEquipmentRequest];
}

- (void)cancelHeroSkill
{
    self.heroSkillId = HeroSkillNull;
    [[BGClient sharedClient] sendCancelHeroSkillRequest];
}

- (void)cancelEquipment
{
    self.equipmentId = PlayingCardNull;
    [[BGClient sharedClient] sendCancelEquipmentRequest];
}

- (void)chooseColorWithColor:(BGCardColor)color
{
    _selectedColor = color;
    [[BGClient sharedClient] sendChoseColorRequest];
}

- (void)chooseSuitsWithSuits:(BGCardSuits)suits
{
    _selectedSuits = suits;
    [[BGClient sharedClient] sendChoseSuitsRequest];
}

- (void)chooseTargetPlayer
{
    if (self.isNeedTargetAgain) {
        [self.playingButton addOptionsAndTextPrompt];
    } else {
        [self updateTargetAndShowLinePath];
        [[BGClient sharedClient] sendChoseTargetPlayerRequest];
    }
}

- (void)chooseViewPlayerRole
{
    [[BGClient sharedClient] choseViewPlayerRoleRequest];
}

- (void)setPlayedCardIds:(NSMutableArray *)playedCardIds
{
    _playedCardIds = playedCardIds;
    [_allPlayedCardIds addObjectsFromArray:playedCardIds];
}

- (BGHandCard *)firstUsedCard
{
    return self.usedCards.firstObject;
}

- (BGHandCard *)lastUsedCard
{
    return self.usedCards.lastObject;
}

- (NSArray *)usedCards
{
    return [BGHandCard handCardsByCardIds:_allPlayedCardIds ofPlayer:self];
}

- (void)setHeroSkillId:(NSInteger)heroSkillId
{
    _heroSkillId = heroSkillId;
}

- (void)setEquipmentId:(NSInteger)equipmentId
{
    _equipmentId = equipmentId;
}

- (BGHeroSkill *)heroSkill
{
    BGHeroSkill *skill = [_character heroSkillBySkillId:_heroSkillId];
    skill.isSelected = YES;
    return skill;
}

- (BGHandCard *)equipmentCard
{
    BGHandCard *equipCard = [_equipment equipmentByCardId:_equipmentId];
    equipCard.isSelected = YES;
    return equipCard;
}

/*
 * 选中了卡牌，但点了取消/弃牌按钮，卡牌需要重新排列
 */
- (void)chooseOkay
{
    if (GameStateBoolChoosing == _game.state ||
        GameStateDropping == _game.state) {
        _heroSkillId = HeroSkillNull;
        _equipmentId = PlayingCardNull;
    }
    
    [[BGClient sharedClient] sendOkayRequest];
}

- (void)chooseCancel
{
    if (GameStateBoolChoosing == _game.state ||
        GameStateDropping == _game.state) {
        _heroSkillId = HeroSkillNull;
        _equipmentId = PlayingCardNull;
    }
    
    if (GameStateTargetChoosing == _game.state) {
//      Trigger skill "MultiCast", the targetPlayerNames is cleared.
        if (0 == _targetPlayerNames.count) {
            _targetPlayerNames = [_preTargetPlayerNames mutableCopy];
        }
    }
    
    [[BGClient sharedClient] sendCancelRequest];
}

- (void)startDiscard
{
    [self resetValueAfterResolved];
    [[BGClient sharedClient] sendStartDiscardRequest];
}

/*
 * Active player(but not self) use hand card
 */
- (void)useHandCardWithCardIds:(NSArray *)cardIds
{
    [_playedCardIds addObjectsFromArray:cardIds];
    [_allPlayedCardIds addObjectsFromArray:cardIds];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self];
    
    if (self.isEquipEquipmentCard) {   // 穿装备
        [self.game.table clearExistingCards];
        BGPlayer *equippedPlayer = (self.targetPlayer) ? self.targetPlayer : self;
        [equippedPlayer.equipment addEquipmentWithCardIds:cardIds];
    } else {
        [_game.table showPlayedCardWithCardIds:cardIds];
        if (cardIds.count == 1) [cards.firstObject playSound];
    }
    
    if (ActionUseHandCard == self.game.action) [self showTargetLinePath];
    
    if ([self isPlayAnimation]) {
        [self runWithCard:cards.lastObject
                 ofPlayer:self
               atPosition:ccp(self.contentSizeInPoints.width/2, 0.0f)];
    }
}

- (void)launchHeroSkillWithSkillId:(NSInteger)skillId
{
    if (HeroSkillNull == skillId) return;
    BGHeroSkill *heroSkill = [_character heroSkillBySkillId:skillId];
    
    if (ActionUseHeroSkill == self.game.action &&
        (heroSkill.targetCount > 0 || heroSkill.minHandCardCount > 0)) {
        return;
    }
    
    if (!heroSkill.isSoundPlayed) [heroSkill playSound];
}

- (void)launchEquipmentWithCardId:(NSInteger)cardId
{
    if (PlayingCardNull == cardId) return;
    BGHandCard *equipCard = [_equipment equipmentByCardId:cardId];
    
    NSString *animationName = (EquipmentTypeWeapon == equipCard.equipmentType) ? @"LaunchWeapon" : @"LaunchArmor";
    [_equipment.userObject runAnimationsForSequenceNamed:animationName];
    equipCard.isAnimationRan = YES;
}

#pragma mark - Playing button
/*
 * Add playing buttons according to different action
 */
- (void)addPlayingButtons
{
    [self removePlayingButtons];
    [self scheduleRemovePlayingNodes];
    
    switch (_game.action) {
        case ActionPlayCard:                // 主动使用
            if (self.heroSkill || self.equipmentCard) {
                [_playingButton addOkayButtonWithEnabled:NO];
            } else {
                [_playingButton addOkayAndDiscardButton];
            }
            break;
            
        case ActionChooseCardToUse:         // 被动使用
            [_playingButton addOkayAndCancelButtonWithOkayEnabled:NO];
            break;
            
        case ActionChooseOkayOrCancel:      // 是否发动英雄或装备技能
            [_playingButton addOkayAndCancelButtonWithOkayEnabled:YES];
            [_handArea disableAllHandCardsWithDarkColor];
            break;
            
        case ActionChooseCardToCompare:     // 拼点
            [_handArea enableAllHandCards];
        case ActionChooseCardToGive:        // 交给其他玩家
        case ActionDiscardCard:             // 弃牌阶段的弃牌
            [_playingButton addOkayButtonWithEnabled:NO];
            break;
            
        case ActionChooseCardToDrop:        // 弃/弃置
            if (_isRequiredDrop) {
                [_playingButton addOkayButtonWithEnabled:NO];
            } else {
                [_playingButton addOkayAndCancelButtonWithOkayEnabled:NO];
            }
            break;
            
        case ActionChooseColor:             // 选择卡牌颜色
            [_playingButton addColorButtons];
            break;
            
        case ActionChooseSuits:             // 选择卡牌花色
            [_playingButton addSuitsButtons];
            break;
            
        case ActionTableShowAssignedCard:
            [_playingButton addOkayButtonWithEnabled:YES];
            break;
            
        case ActionChooseTargetPlayer:
        case ActionChooseDrawCardOrViewRole:
            if (_isRequiredTarget) {
                [_playingButton addOkayButtonWithEnabled:NO];
            } else {
                [_playingButton addOkayAndCancelButtonWithOkayEnabled:NO];
            }
            [self.character checkTargetPlayerEnablement];
            break;
            
        default:
            break;
    }
}

- (void)removePlayingButtons
{
    [_playingButton removeAllChildren];
}

/*
 * Check playing button item enablement while selecting a hand card
 */
- (void)checkPlayingButtonEnablementWithSelectedCard:(BGHandCard *)selCard
{
    if (!selCard.isSelected) {
        [self.playingButton setOkayEnabled:NO];
        return;
    }
    
    if (GameStatePlaying == self.game.state) {
        if (self.heroSkill) {
            _requiredTargetCount = self.heroSkill.targetCount;
        } else if (self.equipmentCard) {
            _requiredTargetCount = self.equipmentCard.targetCount;
        } else {
            _requiredTargetCount = (selCard.isEquipment) ? 0 : selCard.targetCount;     // Don't need target when equip card
            _maxTargetCount = _requiredTargetCount;
        }
    }
    
    [self.playingButton setOkayEnabled:self.isOkayEnabled];
}

- (void)showTargetLinePath
{    
    CCNode *lineBox = [CCNode node];
    [self.game addChild:lineBox z:300 name:@"lineBox"];
    
    if (!self.heroSkill && !self.equipmentCard && self.firstUsedCard.isMislead) {
        [self showTargetLinePathFrom:self.centerPosition to:[self.targetPlayers.firstObject centerPosition]];
        [self runDelayWithDuration:DURATION_LINE_PATH_SCALE*4 block:^{
            [lineBox removeAllChildren];
            [self showTargetLinePathFrom:[self.targetPlayers.firstObject centerPosition]
                                      to:[self.targetPlayers.lastObject centerPosition]];
        }];
    }
    else {
        for (BGPlayer *tarPlayer in self.targetPlayers) {
            [self showTargetLinePathFrom:self.centerPosition to:tarPlayer.centerPosition];
        }
    }
    
    [lineBox runDelayWithDuration:DELAY_LINE_PATH_DISMISS block:^{
        [lineBox removeFromParent];
    }];
}

- (void)showTargetLinePathFrom:(CGPoint)fromPos to:(CGPoint)toPos
{
    CCNode *lineBox = [self.game getChildByName:@"lineBox" recursively:NO];
    
    CCSprite *linePath = [CCSprite spriteWithImageNamed:kImageTargetLinePath];
    linePath.anchorPoint = ccp(0.5, 0);
    linePath.position = fromPos;
    linePath.scaleY = 0;
    double radians = atan2f(toPos.x-fromPos.x, toPos.y-fromPos.y);
    linePath.rotation = CC_RADIANS_TO_DEGREES(radians);
    [lineBox addChild:linePath z:300];
    
    float scaleY = ccpDistance(fromPos, toPos) / linePath.contentSize.height;
    [linePath runTweenWithDuration:DURATION_LINE_PATH_SCALE key:@"scaleY" from:0 to:scaleY block:nil];
}

#pragma mark - Progress bar
- (void)addProgressBarWithDuration:(CCTime)t position:(CGPoint)position
{
    [self removeProgressBar];
    
    _progressBar = [CCSprite spriteWithImageNamed:kImageProgressBarFrame];
    _progressBar.position = position;
    if (!self.isMe) _progressBar.scaleX = PROGRESS_BAR_SCALEX;
    [self addChild:_progressBar];
    
	if (self.isMe && IS_PHONE) {
        if (IS_IPHONE5) _progressBar.scaleX += _progressBar.scaleX * 0.09f;
        CGFloat xPos = (IS_IPHONE5) ? _progressBar.position.x*0.99f : _progressBar.position.x;
        _progressBar.position = ccp(xPos, _progressBar.position.y*0.98);
    }
    
    CCSprite *bar = [CCSprite spriteWithImageNamed:kImageProgressBar];
    CCProgressNode *timer = [CCProgressNode progressWithSprite:bar];
    timer.type = CCProgressNodeTypeBar;
    timer.midpoint = ccp(0.0f, 0.0f);       // Setup for a bar starting from the left since the midpoint is 0 for the x
    timer.barChangeRate = ccp(1.0f, 0.0f);  // Setup for a horizontal bar since the bar change rate is 0 for y meaning no vertical change
    timer.anchorPoint = CGPointZero;
//    timer.scaleX = MIN(t/self.game.playTime, 1.0f);
    [_progressBar addChild:timer];
    
//  Run progress bar. If time is up, execute corresponding operation.
    [timer runProgressBarWithDuration:self.game.playTime block:^{
        // 1. Disable all hand cards
        // 2. Time up handling until selected hand card movement is finished
        [self resetAndRemovePlayingNodes];
        [_handArea disableAllHandCardsWithNormalColor];
        
        if (self.isMe) {
            [self handlingAfterTimeIsUp];
            [self.game.table removePopup];
        }
    }];
}

- (void)addProgressBar
{
    [self addProgressBarWithDuration:self.game.playTime];
}

- (void)addProgressBarWithDuration:(CCTime)t
{
    CGPoint barPosition = (self.isMe) ? ccp(_contentSize.width*0.532f, _contentSize.height*0.805f) : ccp(_contentSize.width/2, 0.0f);
    [self addProgressBarWithDuration:t position:barPosition];
    
    _nickNameLabel.visible = NO;
}

- (void)removeProgressBar
{
    [_progressBar removeFromParent];
    
    _nickNameLabel.visible = [[NSUserDefaults standardUserDefaults] boolForKey:kShowNick];
}

#pragma mark - Time is up
- (void)handlingAfterTimeIsUp
{
    switch (_game.state) {
        case GameStateHeroChoosing:
            [_game.table selectHeroCard:_game.table.heroCardBox.children.firstObject];
            break;
            
        case GameStateComparing: {
            [self chooseCardToCompare];
            break;
        }
            
        case GameStatePlaying:
            if (self.isNeedTargetAgain) {
                [self.playingButton chooseLeftOrRight:nil];     // Choose cancel(right) by default
            } else {
                [self startDiscard];
            }
            break;
            
        case GameStateDiscarding:
            [self okayToDiscard];
            break;
            
        case GameStateBoolChoosing:
        case GameStateDeathResolving:
            [self chooseCancel];
            break;
            
        case GameStateColorChoosing:
            [self chooseColorWithColor:CardColorRed];
            break;
            
        case GameStateSuitsChoosing:
            [self chooseSuitsWithSuits:CardSuitsHearts];
            break;
            
        case GameStateGetting:
            [self getCardFromTargetPlayer];
            break;
            
        case GameStateGiving:
            [self chooseCardToGive];
            break;
            
        case GameStateRemoving:
            [self removeTargetPlayerCardToTable];
            break;
            
        case GameStateAssigning:
            [_game.table assignCardToEachPlayer];
            break;
            
        case GameStateCardChoosing:
        case GameStateWaitingDispel:
            if (self.isTarget && _availableCardIds.count >= _selectableCardCount) {
                [self chooseCardToUse];
            } else {
                [self chooseCancel];
            }
            break;
            
        case GameStateDropping:
            if (_isRequiredDrop) {
                [self chooseCardToDrop];
            } else {
                [self chooseCancel];
            }
            break;
            
        case GameStateTargetChoosing:
            if (_isRequiredTarget) {    // 发动了技能"战意"
                [_targetPlayerNames addObject:_game.turnOwnerName];
                [self chooseTargetPlayer];
            } else {
                [self chooseCancel];
            }
            break;
            
        case GameStatePlayerDying:
            if (self.isDying && _availableCardIds.count >= _selectableCardCount) {
                if (ActionChooseCardToDrop == self.game.action) {
                    [self chooseCardToDrop];
                } else {
                    [self chooseCardToUse];
                }
            } else if (self.isDying && ActionChooseOkayOrCancel == self.game.action) {
                [self chooseOkay];
            } else {
                [self chooseCancel];
            }
            break;
            
        default:
            break;
    }
}

- (void)chooseCardToCompare
{
    [_handArea playHandCardAfterTimeIsUpWithBlock:^{
        _comparedCardId = [_playedCardIds.lastObject integerValue];
        [[BGClient sharedClient] sendChoseCardToCompareRequest];
    }];
}

- (void)chooseCardToUse
{
    [_handArea playHandCardAfterTimeIsUpWithBlock:^{
        [[BGClient sharedClient] sendChoseCardToUseRequest];
    }];
}

- (void)chooseCardToGive
{
    [_handArea playHandCardAfterTimeIsUpWithBlock:^{
        [[BGClient sharedClient] sendChoseCardToGiveRequest];
    }];
}

- (void)chooseCardToDrop
{
    [_handArea playHandCardAfterTimeIsUpWithBlock:^{
        [[BGClient sharedClient] sendChoseCardToDropRequest];
    }];
}

- (void)getCardFromTargetPlayer
{
    if (0 == _selectableCardCount) return;
    
    NSMutableArray *cardButtons = [NSMutableArray array];
    BGPlayer *tarPlayer = (self.isTurnOwner) ? self.targetPlayer : _game.turnOwner;
    
    if (tarPlayer.handCardCount > 0) {
        for (NSUInteger i = 0; i < _selectableCardCount-_selectedCardIdxes.count; i++) {
            [cardButtons addObject:_game.table.cardPopup.children[i]];
        }
        [_game.table selectHandCards:cardButtons];
    } else {
        [cardButtons addObject:_game.table.cardPopup.children.firstObject];
        [_game.table selectEquipmentCards:cardButtons];
    }
}

- (void)removeTargetPlayerCardToTable
{
    NSMutableArray *cardButtons = [NSMutableArray array];
    
    if (_game.table.isShowHandCard) {
        for (NSUInteger i = 0; i < _selectableCardCount-_selectedCardIdxes.count; i++) {
            [cardButtons addObject:_game.table.cardPopup.children[i]];
        }
        [_game.table selectHandCards:cardButtons];
    } else {
        [cardButtons addObject:_game.table.cardPopup.upLayoutBox.children.firstObject];
        [_game.table selectEquipmentCards:cardButtons];
    }
}

- (void)okayToDiscard
{
    [_handArea playHandCardAfterTimeIsUpWithBlock:^{
        [[BGClient sharedClient] sendOkayToDiscardRequest];
    }];
}

#pragma mark - Text prompt
/*
 * 1. Add text prompt for selected card while playing
 * 2. Add text prompt according to different game state(action)
 * (If "GameStateChoosingCard", add text according to the used card/skill by turn owner)
 */
- (void)addTextPrompt
{
    switch (_game.state) {
        case GameStateHeroChoosing:
            if (self.game.isRoleMode) [self addTextPromptForPlayersRole];
            break;
            
        case GameStatePlaying:
            if (self.selectedHandCardCount > 0) {
                if (_game.damageSource.heroSkill) {
                    [self addTextPromptAccordingToUsedSkill];   // e.g.两张牌当攻击
                } else {
                    [self addTextPromptForSelectedCard];
                }
            } else {
                [self addTextPromptLabelWithString:tipTexts[_game.state]];
            }
            break;
            
        case GameStateCardChoosing:
            if (_game.damageSource.heroSkill) {
                [self addTextPromptAccordingToUsedSkill];
            } else {
                [self addTextPromptAccordingToUsedCard];
            }
            break;
            
        case GameStateWaitingDispel:
            if (ActionChooseOkayOrCancel == _game.action) {
                [self addTextPromptForTriggering];
            } else {
                if (_game.activePlayer.heroSkill) {
                    [self addTextPromptAccordingToUsedSkill];
                } else {
                    [self addTextPromptAccordingToUsedCard];
                }
            }
            break;
            
        case GameStateDropping:
            if (self.heroSkill) {
                [self addTextPromptLabelWithString:self.heroSkill.triggerTipText];
            } else {
                if (_game.damageSource.heroSkill) {
                    [self addTextPromptAccordingToUsedSkill];
                } else {
                    [self addTextPromptAccordingToUsedCard];
                }
            }
            break;
            
        case GameStateGiving: {
            NSString *text = [BGUtil textWith:tipTexts[_game.state] parameter:@(_selectableCardCount).stringValue];
            [self addTextPromptLabelWithString:text];
            break;
        }
        
        case GameStateBoolChoosing:
            [self addTextPromptForTriggering];
            break;
            
        case GameStateTargetChoosing:
            [self addTextPromptLabelWithString:self.heroSkill.yesTipText];
            break;
            
        case GameStateDiscarding: {
            NSString *text = [BGUtil textWith:tipTexts[_game.state] parameter:@(_requiredSelCardCount).stringValue];
            [self addTextPromptLabelWithString:text];
            break;
        }
            
        case GameStatePlayerDying: {
            if (self.heroSkill && self.isDyingTriggered) {
                [self addTextPromptLabelWithString:self.heroSkill.triggerTipText];
            } else {
                BGPlayer *player = _game.dyingPlayer;
                NSUInteger count = abs((int)player.character.healthPoint)+1; // 需要几张治疗药膏
                NSArray *parameters = @[player.heroName, @(count).stringValue,];
                [self addTextPromptLabelWithString:[BGUtil textWith:tipTexts[_game.state] parameters:parameters]];
            }
            break;
        }
            
        case GameStateDeathResolving:
            if (self.heroSkill) {
                [self addTextPromptLabelWithString:self.heroSkill.triggerTipText];
            } else if (self.equipmentCard) {
                [self addTextPromptLabelWithString:self.equipmentCard.triggerTipText];
            } else {
                [self addTextPromptLabelWithString:tipTexts[_game.state]];
            }
            break;
            
        default:
            [self addTextPromptLabelWithString:tipTexts[_game.state]];
            break;
    }
}

- (void)addTextPromptForPlayersRole
{
    NSString *string = tipTexts[GameStateHeroChoosing];
    NSArray *parameters = @[self.roleCard.cardText, self.game.nextPlayer.roleCard.cardText];
    string = [BGUtil textWith:string parameters:parameters];
    
    _textPromptBMFont = [CCLabelBMFont labelWithString:string fntFile:kFontRoleTextTip];
    _textPromptBMFont.scale /= FONT_SCALE_FACTOR;
    _textPromptBMFont.position = ccp(SCREEN_WIDTH/2, self.bgContentSize.height/2);
    [self.game addChild:_textPromptBMFont z:300];   // Need display on the top
    
//  Set self role font color with yellow
    NSRange range = [string rangeOfString:self.roleCard.cardText];
    for (NSUInteger i = range.location-1; i < range.location+range.length+1; i++) {   // 包括两个引号(“”)
        ((CCSprite *)_textPromptBMFont.children[i]).color = [CCColor yellowColor];
    }
    
//  Set next player's role text with green
    range = [string rangeOfString:self.game.nextPlayer.roleCard.cardText];
    for (NSUInteger i = range.location-1; i < range.location+range.length+1; i++) {   // 包括两个引号(“”)
        ((CCSprite *)_textPromptBMFont.children[i]).color = [CCColor greenColor];
    }
}

- (void)addTextPromptLabelWithString:(NSString *)string
{
    [self removeTextPrompt];
    
    if (!_textPrompt && string) {
        _textPrompt = [CCLabelTTF labelWithString:string fontName:@"Helvetica" fontSize:14];
        _textPrompt.fontColor = PROMPT_TEXT_COLOR;
        _textPrompt.position = ccp(SCREEN_WIDTH/2, self.bgContentSize.height*0.91f);
        if (IS_PHONE) _textPrompt.scale *= 0.8f;
        [self.game addChild:_textPrompt z:300];   // Need display on the top
        
//      Assign card popup too big for iPhone, need adjust text prompt position
        if (IS_PHONE && self.game.table.isAssigningCard && self.game.alivePlayerCount > 2) {
            _textPrompt.position = ccp(_textPrompt.position.x, _textPrompt.position.y*0.75f);
        }
    } else if (string) {
        _textPrompt.string = string;
    }
}

/*
 * Add text prompt while player selecting a card
 */
- (void)addTextPromptForSelectedCard
{
    BGHandCard *card = _handArea.selectedCards.firstObject;
    [self addTextPromptLabelWithString:card.tipText];
}

/*
 * Add text prompt while the player is specified as target(by attack/magic)
 * According to the used card/equipment/skill by active player(最后出牌的玩家)
 */
- (void)addTextPromptAccordingToUsedCard
{
    BGPlayer *player = (_game.isWaitingDispel) ? _game.activePlayer : _game.damageSource;
    
    BGHandCard *handCard = (_game.isWaitingDispel) ? player.lastUsedCard : player.firstUsedCard;
    BGHandCard *usedCard = (player.equipmentCard) ? player.equipmentCard : handCard;
    NSString *tipText = (_game.isWaitingDispel) ? usedCard.dispelTipText : usedCard.targetTipText;
    [self addTextPromptLabelWithString:tipText];
}

- (void)addTextPromptAccordingToUsedSkill
{
    BGPlayer *damageSource = _game.damageSource;
    BGPlayer *activePlayer = _game.activePlayer;
    
    BGHeroSkill *usedSkill = (_game.isWaitingDispel) ? activePlayer.heroSkill : damageSource.heroSkill;
    NSString *tipText = (_game.isWaitingDispel) ? usedSkill.dispelTipText : usedSkill.targetTipText;
    [self addTextPromptLabelWithString:tipText];
}

/*
 * Add text prompt for trigger hero skill, equipment or strengthen
 */
- (void)addTextPromptForTriggering
{
    if (self.heroSkill) {
        [self addTextPromptLabelWithString:self.heroSkill.triggerTipText];
    } else if (self.equipmentCard) {
        [self addTextPromptLabelWithString:self.equipmentCard.triggerTipText];
    } else {
        if (self.isStrengthening) {
            [self addTextPromptLabelWithString:self.firstUsedCard.strenTipText];
        } else {
            [self addTextPromptLabelWithString:self.firstUsedCard.triggerTipText];  // e.g. WildAxe
        }
    }
}

- (void)addTextPromptForSelectedEquipment
{
    [self addTextPromptLabelWithString:self.equipmentCard.useTipText];
}

- (void)addTextPromptForSelectedHeroSkill
{
    [self addTextPromptLabelWithString:self.heroSkill.useTipText];
}

- (void)removeTextPrompt
{
    [_textPrompt removeFromParent];
    [_textPromptBMFont removeFromParent];
    _textPrompt = nil;
    _textPromptBMFont = nil;
}

#pragma mark - Game history
- (void)addGameHistoryWithEsObject:(EsObject *)esObj
{
    BGHistory *history = [BGHistory historyOfPlayer:self withEsObject:esObj];
    if (history.text.length <= 0) return;
    
    [_gameHistory addObject:history];
    [self.game.historyPopup reloadHistoryData];
    
    [self addHistoryTip:history];
}

//  Add history information tip help user know what happened
- (void)addHistoryTip:(BGHistory *)history
{
    if (self.isMe || !history.isShowTip) return;
    
    [self removeHistoryTip];
    
    NSString *text = [history.heroName stringByAppendingString:history.text];
    CCLabelTTF *label = [CCLabelTTF labelWithString:text fontName:@"Helvetica" fontSize:14 dimensions:CGSizeMake(400, 0)];
    label.fontColor = PROMPT_TEXT_COLOR;
    label.position = GAME_HISTORY_POSITION;
    label.horizontalAlignment = CCTextAlignmentCenter;
    [self.game addChild:label z:0 name:@"history"];
    
    [label runDelayWithDuration:DELAY_HISTORY_DISMISS block:^{
        [label runFadeOutWithDuration:DELAY_HISTORY_DISMISS block:^{
            [label removeFromParent];
        }];
    }];
}

- (void)removeHistoryTip
{
    CCNode *node = [self.game getChildByName:@"history" recursively:NO];
    [node removeFromParent];
}

#pragma mark - Player dying/dead/escape
- (BOOL)isDying
{
    return (_character.healthPoint <= 0);
}

- (void)askForHelp
{
    _escapedMark.visible = NO;
    _helpMeMark.visible = YES;
    [self.userObject runAnimationsForSequenceNamed:@"HelpMe"];
}

- (void)diedAndShowRole:(NSInteger)roleId
{
    if (_isDead) return;    // Already dead, don't need show again.
    
    _isDead = YES;
    _roleButton.enabled = _roleBox.visible = NO;
    [self.game.table clearExistingCards];
    [self renderRoleWithRoleId:roleId animated:NO];
    [self useGrayscaleShaderByRecursively:YES];
    
    _helpMeMark.visible = _escapedMark.visible = NO;
    _deadMark.visible = YES;
    _deadMark.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:_roleCard.deathImageName];
    [self.userObject runAnimationsForSequenceNamed:@"DeadMark"];
    
    [self.character.heroCard playSound];
    if (self.isFirstBlood) [[BGAudioManager sharedAudioManager] playFirstBlood];
}

- (void)escapedFromGame
{
    _isEscaped = YES;
    _helpMeMark.visible = NO;
    _escapedMark.visible = YES;
    if (!self.isMe) [self.userObject runAnimationsForSequenceNamed:@"EscapedMark"];
}

@end
