//
//  BGGameScene.m
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGGameScene.h"

@interface BGGameScene ()

@property (nonatomic) CGPoint beganTouchPos;
@property (nonatomic) CGPoint beganHisPopupPos;
@property (nonatomic) NSTimeInterval beganTimestamp;
@property (nonatomic) BOOL isTouchedInCCGLView;
@property (nonatomic) BOOL isTouchedInChatPopup;

@end

@implementation BGGameScene {
    BOOL _isNormalColor;
    NSMutableArray *_gameHistory;
}

@synthesize state = _state;

- (id)init
{
    if (self = [super init]) {
        _gameHistory = [NSMutableArray array];
    }
    return self;
}

- (void)didLoadFromCCB
{
    _backButton.enabled = NO;
    
    _table = [BGTable node];
    [self addChild:_table z:100];
}

- (void)setFateCardId:(NSInteger)fateCardId
{
    _fateCardId = fateCardId;
    if (self.playerCount <= 2) return;
    
    BGFateCard *fateCard = [BGFateCard cardWithCardId:fateCardId];
    CCLabelBMFont *fateLabel = [CCLabelBMFont labelWithString:fateCard.cardText fntFile:kFontFateTask];
    fateLabel.scale /= FONT_SCALE_FACTOR;
    fateLabel.positionType = CCPositionTypeNormalized;
    fateLabel.position = ccp(0.5f, 0.5f);
    [_fateFrame addChild:fateLabel];
}

/*
 * Adjust the user's index, put the self player name to first one.
 */
- (void)renderAllPlayers:(NSArray *)playerNames withRoleIds:(NSArray *)roleIds
{
    if (_isNoChatting) _leftArrow.visible = NO;
    
    NSMutableArray *mutableNames = [playerNames mutableCopy];
    NSMutableArray *mutableRoleIds = [roleIds mutableCopy];
    NSMutableIndexSet *idxSet = [NSMutableIndexSet indexSet];
    NSUInteger idx = 0;
    
    for (NSString *playerName in playerNames) {
        if ([playerName isEqual:[BGClient sharedClient].es.managerHelper.userManager.me.userName]) {
            [mutableNames removeObjectsAtIndexes:idxSet];
            [mutableNames addObjectsFromArray:[playerNames objectsAtIndexes:idxSet]];
            _allPlayerNames = mutableNames;
            
            if (roleIds.count > 0) {
                [mutableRoleIds removeObjectsAtIndexes:idxSet];
                [mutableRoleIds addObjectsFromArray:[roleIds objectsAtIndexes:idxSet]];
                _allRoleIds = mutableRoleIds;
            }
            break;
        }
        [idxSet addIndex:idx]; idx++;
    }
    
//  Set player sprite
    _allPlayers = [NSMutableArray arrayWithCapacity:_allPlayerNames.count];
    [_allPlayerNames enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        BGPlayer *player = (BGPlayer *)[_effectNode getChildByName:@(idx).stringValue recursively:NO];
        player.playerName = obj;
        player.nickName = [[BGClient sharedClient] userByUserName:obj].nickName;
        if (0 == idx) _selfPlayer = player;
        
        if (_allRoleIds.count > 0 && (0 == idx || 1 == idx)) {
            [player renderRoleWithRoleId:[_allRoleIds[idx] integerValue] animated:NO];    // 自己和下家身份
            // Don't need mark self and next player's role
            player.roleButton.enabled = (IS_PAD && !player.isMe && ![player isEqual:self.nextPlayer]);
        }
        
        [_allPlayers addObject:player];
    }];
    
    [self renderAllFactions];
}

- (void)renderAllFactions
{
    [_factionLayoutBox.children enumerateObjectsUsingBlock:^(BGFaction *faction, NSUInteger idx, BOOL *stop) {
        faction.roleEnum = _factionLayoutBox.children.count-idx;
        faction.aliveCount = [self countOfRole:faction.roleEnum];
        faction.totalCount = _allRoleIds.count;
    }];
}

- (NSUInteger)countOfRole:(BGRoleCardEnum)roleEnum
{
    NSUInteger count = 0;
    for (NSNumber *number in _allRoleIds) {
        BGRoleCard *roleCard = [BGRoleCard cardWithCardId:[number integerValue]];
        if (roleCard.cardEnum == roleEnum) count++;
    }
    return count;
}

- (void)renderAllPlayersRoleWithRoleIds:(NSArray *)roleIds
{
    NSMutableArray *mutableRoleIds = [roleIds mutableCopy];
    NSMutableIndexSet *idxSet = [NSMutableIndexSet indexSet];
    NSUInteger idx = 0;
    
    for (NSString *playerName in self.allPlayerNames) {
        if ([playerName isEqual:_turnOwnerName]) {
            [mutableRoleIds removeObjectsAtIndexes:idxSet];
            [mutableRoleIds addObjectsFromArray:[roleIds objectsAtIndexes:idxSet]];
            _allRoleIds = mutableRoleIds;
            break;
        }
        [idxSet addIndex:idx]; idx++;
    }
    
    [self.allPlayers enumerateObjectsUsingBlock:^(BGPlayer *player, NSUInteger idx, BOOL *stop) {
        [player renderRoleWithRoleId:[_allRoleIds[idx] integerValue] animated:YES];
        player.roleButton.enabled = NO;     // Don't need mark player's role if game is not role mode
    }];
}

- (void)updateFactionWithRoleId:(NSInteger)roleId
{
    BGRoleCard *roleCard = [BGRoleCard cardWithCardId:roleId];
    BGFaction *faction = (BGFaction *)[_factionLayoutBox getChildByName:@(roleCard.cardEnum+1).stringValue recursively:NO];
    faction.aliveCount -= 1;
}

- (void)setDeckCardCount:(NSUInteger)deckCardCount
{
    _deckCardCountLabel.string = @(deckCardCount).stringValue;
}

- (void)setState:(BGGameState)state
{
    if (GameStateNull != state) _state = state;
}

- (void)resetValueAfterResolved {
    _isWaitingDispel = NO;
    _damagedPlayer = nil;
    _dyingPlayer = nil;
    
    for (BGPlayer *player in self.alivePlayers) {
        [player resetValueAfterResolved];
    }
    
    [_table removePopup];
}

#pragma mark - Player and name
- (BGPlayer *)previosPlayer
{
    NSInteger index = [self.alivePlayers indexOfObject:_selfPlayer] - 1;
    if (index < 0) index = self.alivePlayerCount - 1;
    return self.alivePlayers[index];
}

- (BGPlayer *)nextPlayer
{
    NSUInteger index = [self.alivePlayers indexOfObject:_selfPlayer] + 1;
    if (index == self.alivePlayerCount) index = 0;
    return self.alivePlayers[index];
}

- (BGPlayer *)turnOwner
{
    return [self playerWithName:_turnOwnerName];
}

- (BGPlayer *)damageSource
{
    return [self playerWithName:_damageSourceName];
}

- (BGPlayer *)activePlayer
{
    return [self playerWithName:_activePlayerName];
}

- (BGPlayer *)playerWithName:(NSString *)playerName
{
    for (BGPlayer *player in _allPlayers) {
        if ([player.playerName isEqualToString:playerName])
            return player;
    }
    return nil;
}

- (BGPlayer *)playerWithSelectedHeroId:(NSInteger)heroId
{
    for (BGPlayer *player in _allPlayers) {
        if (player.selectedHeroId == heroId)
            return player;
    }
    return nil;
}

- (NSUInteger)playerCount
{
    return _allPlayers.count;
}

- (NSUInteger)alivePlayerCount
{
    return self.alivePlayers.count;
}

- (NSArray *)alivePlayers {
    NSMutableArray *alivePlayers = [NSMutableArray array];
    for (BGPlayer *player in self.allPlayers) {
        if (!player.isDead) [alivePlayers addObject:player];
    }
    return alivePlayers;
}

- (void)setDamageSourceName:(NSString *)damageSourceName
{
    if (damageSourceName.length > 0) _damageSourceName = damageSourceName;
}

- (void)setActivePlayerName:(NSString *)activePlayerName
{
    if (activePlayerName.length > 0) {
        _preActivePlayer = [self playerWithName:_activePlayerName];
        _activePlayerName = activePlayerName;
    }
}

- (BOOL)isRoleMode
{
    return (![[BGClient sharedClient].gameType isEqual:GAMETYPE_NEWBIE] &&
            ![[BGClient sharedClient].gameType isEqual:GAMETYPE_VERSUS]);
}

#pragma mark - Progress bar and enablement
- (void)addProgressBarForOtherPlayers
{
    for (BGPlayer *player in self.alivePlayers) {
        if (![player isEqual:_selfPlayer])
            [player addProgressBar];
    }
}

- (void)removeProgressBarForOtherPlayers
{
    for (BGPlayer *player in self.alivePlayers) {
        if (![player isEqual:_selfPlayer])
            [player removeProgressBar];
    }
}

- (void)enablePlayerAreaForOtherPlayers
{
    for (BGPlayer *player in self.alivePlayers) {
        if (![player isEqual:_selfPlayer])
            [player enablePlayerArea];
    }
}

- (void)disablePlayerAreaForAllPlayers
{
    if (_isNormalColor) {
        for (BGPlayer *player in self.alivePlayers) {
            if (![player isEqual:_selfPlayer])
                [player disablePlayerAreaWithNormalColor];
        }
    }
}

#pragma mark - Node mask
- (void)makeBackgroundToDark
{
    _isNormalColor = NO;
    _backButton.enabled = NO;
    [self addPopupMaskNode];
}

- (void)makeBackgroundToNormal
{
    _isNormalColor = YES;
    _backButton.enabled = YES;
    [self removePopupMaskNode];
    
    self.userInteractionEnabled = YES;  // Enable after all players selected hero
}

- (void)setArrowVisible:(BOOL)isVisible
{
    _leftArrow.visible = _rightArrow.visible = isVisible;
}

#pragma mark - Selected heros
/*
 * Render the selected hero for other players
 */
- (void)renderOtherPlayersHeroWithHeroIds:(NSArray *)heroIds
{
    self.allHeroIds = heroIds;
    [_allPlayers enumerateObjectsUsingBlock:^(BGPlayer *player, NSUInteger idx, BOOL *stop) {
        if (![player isEqual:_selfPlayer])
            [player renderHeroWithHeroId:[_allHeroIds[idx] integerValue]];
    }];
    
    [self removeProgressBarForOtherPlayers];
}

/*
 * Adjust the hero id's index, put the hero id of self player selected as first one.
 */
- (void)setAllHeroIds:(NSArray *)allHeroIds
{
    NSMutableArray *mutableHeroIds = [allHeroIds mutableCopy];
    NSMutableIndexSet *idxSet = [NSMutableIndexSet indexSet];
    NSUInteger idx = 0;
    
    for (NSNumber *number in allHeroIds) {
        if (number.integerValue == _selfPlayer.selectedHeroId) {
            [mutableHeroIds removeObjectsAtIndexes:idxSet];
            [mutableHeroIds addObjectsFromArray:[allHeroIds objectsAtIndexes:idxSet]];
            _allHeroIds = mutableHeroIds;
            break;
        }
        [idxSet addIndex:idx]; idx++;
    }
}

#pragma mark - Card movement
/*
 * Move the selected cards to table or other player's hand
 */
- (void)moveCard:(CCNode *)cardNode toPlayer:(BGPlayer *)player
{
    [self addChild:cardNode];
    [cardNode runEaseMoveScaleWithDuration:DURATION_CARD_MOVE
                                  position:player.centerPosition
                                     scale:CARD_SCALE_DOWN
                                    object:cardNode
                                     block:^(id obj) {
                                         [obj removeFromParent];
                                     }];
}

- (void)moveCards:(NSArray *)cardNodes toPlayer:(BGPlayer *)player
{
    for (CCNode *cardNode in cardNodes) {
        [self addChild:cardNode];
        [cardNode runEaseMoveScaleWithDuration:DURATION_CARD_MOVE
                                      position:player.centerPosition
                                         scale:CARD_SCALE_DOWN
                                        object:cardNode
                                         block:^(id obj) {
                                             [obj removeFromParent];
                                         }];
    }
}

#pragma mark - Tip and game history
- (NSArray *)tipParameters
{
    BGPlayer *player = (self.isWaitingDispel) ? self.activePlayer : self.damageSource;
    BGPlayer *tarPlayer = player.targetPlayer;
    
    NSString *heroName = (player.isMe) ? HERO_NAME_YOU : player.heroName;
    NSString *tarHeroName = (tarPlayer.isMe) ? HERO_NAME_YOU : tarPlayer.heroName;
    if ([player isEqual:tarPlayer]) tarHeroName = HERO_NAME_SELF;
    
    return (tarHeroName) ? @[heroName, tarHeroName] : @[heroName];
}

- (void)addHistory
{
    BGHistory *history = [[BGHistory alloc] init];
    [_gameHistory addObject:history];
}

- (void)addHistoryOfPlayerWithEsObject:(EsObject *)esObj
{
    for (BGPlayer *player in self.allPlayers) {
        [player addGameHistoryWithEsObject:esObj];
    }
}

- (NSDictionary *)allGameHistories
{
    NSMutableArray *allPlayersHistory = [NSMutableArray array];
    for (BGPlayer *player in _allPlayers) {
        [allPlayersHistory addObjectsFromArray:player.gameHistory];
    }
    
    [allPlayersHistory sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        return [[obj1 dateTime] compare:[obj2 dateTime]];
    }];
    
//  1. Get game's history (e.g. StartRound)
    NSMutableDictionary *histories = [NSMutableDictionary dictionary];
    NSString *key = [NSString string];
    for (BGHistory *history in _gameHistory) {
        key = [NSString stringWithFormat:@"%@%@%@", history.dateTime, CONNECTOR_SIGN, history.text];
        histories[key] = [NSMutableArray array];
    }
    
//  2. Get each player's history
    for (BGHistory *history in allPlayersHistory) {
        if (history.isStartTurn) {
            key = [NSString stringWithFormat:@"%@%@%@%@", history.dateTime, CONNECTOR_SIGN, history.heroName, history.text];
            histories[key] = [NSMutableArray array];
        }
        [histories[key] addObject:history];
    }
    
    return histories;
}

#pragma mark - Chat and game history popup
- (BOOL)isTouchFromLeftEdge:(CGPoint)touchPos
{
    CGPoint offsetPos = ccpSub(touchPos, _beganTouchPos);
    CGFloat maxX = SCREEN_WIDTH/20;
    return (self.isTouchedInCCGLView && _beganTouchPos.x < maxX && abs(offsetPos.x) > 10);
}

- (BOOL)isTouchFromRightEdge:(CGPoint)touchPos
{
    CGPoint offsetPos = ccpSub(touchPos, _beganTouchPos);
    CGFloat minX = (SCREEN_WIDTH-SCREEN_WIDTH/20) * SCALE_MULTIPLIER;
    return (self.isTouchedInCCGLView && _beganTouchPos.x >= minX && abs(offsetPos.x) > 10);
}

- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    _beganTouchPos = [touch locationInView:touch.view];
    _beganTimestamp = touch.timestamp;
    
    self.isTouchedInCCGLView = [touch.view isKindOfClass:[CCGLView class]];
    self.isTouchedInChatPopup = CGRectContainsPoint(_chatPopup.bgBoundingBox, [touch glLocationInTouchedView]);
    
    if (_historyPopup) _beganHisPopupPos = _historyPopup.position;
}

- (void)touchMoved:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    CGPoint preTouchPos = [touch previousLocationInView:touch.view];
    CGPoint touchPos = [touch locationInView:touch.view];
    CGPoint offsetPos = ccpSub(touchPos, _beganTouchPos);
    
    if ([self isTouchFromLeftEdge:touchPos] && !_chatPopup && !_isNoChatting) {
        if (![self isShowingPopup]) _chatPopup = [self showPopupWith:kCcbiChatPopup];
    }
    else if ([self isTouchFromRightEdge:touchPos] && !_historyPopup) {
        if (![self isShowingPopup]) _historyPopup = [self showPopupWith:kCcbiHistoryPopup];
    }
    
    if (_historyPopup && abs(offsetPos.x) > abs(offsetPos.y) && !self.isTouchedInChatPopup) {
        CGPoint offsetPos = ccpSub(touchPos, preTouchPos);
        
        CGFloat minX = SCREEN_WIDTH - _historyPopup.bgSize.width;
        if (_historyPopup.position.x+offsetPos.x <= minX) offsetPos.x = 0.0f;
        _historyPopup.position = ccpAdd(_historyPopup.position, ccp(offsetPos.x, 0.0f));
    }
}

- (id)showPopupWith:(NSString*)file
{
    [self addPopupMaskNode];
    
    BGPopup *popupNode = (BGPopup *)[CCBReader load:file];
    [popupNode show];
    return popupNode;
}

- (BOOL)isShowingPopup
{
    CCNode *playerInfo = [self getChildByName:@"playerInfo" recursively:NO];
    CCNode *alertPopup = [self getChildByName:@"alertPopup" recursively:NO];
    return (playerInfo || alertPopup);
}

- (void)removePopup
{
    [self removeChildByName:@"cardInfo"];
    
    BGPopup *playerInfo = (BGPopup *)[self getChildByName:@"playerInfo" recursively:NO];
    [playerInfo dismiss];
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    CGPoint preTouchPos = [touch previousLocationInView:touch.view];
    CGPoint touchPos = [touch locationInView:touch.view];
    CGPoint offsetPos = ccpSub(touchPos, preTouchPos);
    CGFloat xMovement = abs(ccpSub(touchPos, _beganTouchPos).x);
    CGFloat yMovement = abs(ccpSub(touchPos, _beganTouchPos).y);
    
//  Tap guesture
    if (touch.tapCount > 0) {
        [self removePopup];
        
        if (self.isTouchedInCCGLView) {
            if (_chatPopup.isKeyboardAppeared) {
                _chatPopup.isKeyboardAppeared = NO; return;
            }
            if (!self.isTouchedInChatPopup) {
                [_chatPopup dismiss];
                [_historyPopup dismiss];
            }
        }
        return;
    }
    
//  Swipe gesture
    CGFloat xOffset = abs(_historyPopup.position.x - _beganHisPopupPos.x);
    NSTimeInterval duration = (touch.timestamp-_beganTimestamp) + DURATION_SWIPE_MINIMUM;
    
    if (offsetPos.x >= 0) {
        if (xMovement > yMovement || xOffset > yMovement) {
            [_historyPopup dismissWithDuration:duration];
        } else {
            [_historyPopup showWithDuration:duration];
        }
    }
    else {
        if (xMovement > SCREEN_WIDTH/50 || xOffset > SCREEN_WIDTH/50) {
            if (self.isTouchedInCCGLView) [_chatPopup dismiss];
            [_historyPopup showWithDuration:duration];
        } else {
            [_historyPopup dismissWithDuration:duration];
        }
    }
}

- (void)showMessage:(NSString *)message ofUserName:(NSString *)userName
{
    BGPlayer *player = [self playerWithName:userName];
    
    BGBubble *bubble = (BGBubble *)[CCBReader load:kCcbiBubble];
    [bubble setMessage:message];
    bubble.positionType = CCPositionTypeNormalized;
    bubble.position = (player.isMe) ? ccp(0.05f, 0.85f) :ccp(0.2f, 0.2f);
    bubble.scaleX /= player.scaleX;
    bubble.scaleY /= player.scaleY;
    [bubble showInNode:player];
    
    [[BGAudioManager sharedAudioManager] playMessage];
}

#pragma mark - Popup delegate
- (void)popupOkay:(BGPopup *)popup
{
    if ([popup isKindOfClass:[BGAlertPopup class]]) {
        [self backToZoneScene];
        [[BGClient sharedClient] sendExitGameRequest];
    }
}

- (void)didDismissPopup:(BGPopup *)popup
{
    if ([popup isEqual:_chatPopup]) _chatPopup = nil;
    if ([popup isEqual:_historyPopup]) _historyPopup = nil;
    
    if (!_chatPopup && !_historyPopup) {
        if (_table.cardPopup) {
            [self setAllChildButtonsInteractionEnabled:YES];
        } else {
            [self removePopupMaskNode];
        }
    }
    
    [_effectNode clearEffect];
}

#pragma mark - Button selector
- (void)exitGame:(CCButton *)sender
{
    [self addPopupMaskNode];
    
    NSString *message = (_selfPlayer.isDead) ? ALERT_EXIT_GAME : ALERT_ESCAPE;
    [BGUtil showAlertPopupWithMessage:message];
    
    [[BGAudioManager sharedAudioManager] playGameExit];
}

- (void)backToZoneScene
{
    [BGChatPopup clearHistory];
    [BGTransitionManager transitSceneToRight:kCcbiZoneScene];
}

- (id)backToRoomScene
{
    NSString *fileName = FILE_FULL_NAME(kCcbiRoomScene, [BGClient sharedClient].esRoom.capacity, kFileTypeCCBI);
    return [BGTransitionManager transitSceneToRight:fileName];
}

#pragma mark - Transition
- (void)onExitTransitionDidStart
{
    [super onExitTransitionDidStart];
//  Remove the table view of chatting and game history
#if __CC_PLATFORM_IOS
    [[CCDirector sharedDirector].view.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
#endif
}

@end
