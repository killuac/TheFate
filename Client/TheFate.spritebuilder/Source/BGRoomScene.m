//
//  BGRoomScene.m
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoomScene.h"
#import "BGClient.h"

@interface BGRoomScene ()

@property (nonatomic, strong) NSMutableArray *allSeats;
@property (nonatomic, strong) NSMutableDictionary *occupiedSeats;
@property (nonatomic, strong) BGRoomSeat *selfSeat;

@end

@implementation BGRoomScene {
    BOOL _isTouchedInCCGLView;
    EsUser *_esUser;
    EsRoom *_esRoom;
}

- (void)didLoadFromCCB
{
    self.userInteractionEnabled = YES;
    
    [self loadRoomInformation];
    [self updateRoomDisplay];
    
    [BGChatPopup initializeHistory];
}

- (void)loadRoomInformation
{
    _esUser = [BGClient sharedClient].esUser;
    _esRoom = [BGClient sharedClient].esRoom;
    
//  Clear occupied seats' user for rejoining room
    for (NSString *userName in _occupiedSeats.allKeys) {
        [self removeUserByUserName:userName];
    }
    _allSeats = [NSMutableArray arrayWithCapacity:_esRoom.capacity];
    for (NSUInteger i = 0; i < _esRoom.capacity; i++) {
        [_allSeats addObject:[self getChildByName:@(i).stringValue recursively:YES]];
    }
    _occupiedSeats = [NSMutableDictionary dictionaryWithCapacity:_esRoom.capacity];
    
//  Add users to room (Room owner takes up first seat)
    [[self sortedUsers] enumerateObjectsUsingBlock:^(EsUser *user, NSUInteger idx, BOOL *stop) {
        [self addUser:user];
    }];
}

- (NSArray *)sortedUsers
{
    NSMutableArray *mutableUsers = [NSMutableArray arrayWithCapacity:_esRoom.userCount];
    for (EsUser *user in _esRoom.users) {
        [mutableUsers addObject:user];
    }
    NSMutableIndexSet *idxSet = [NSMutableIndexSet indexSet];
    NSUInteger idx = 0;
    
    for (EsUser *user in _esRoom.users) {
        if (user.isRoomOwner) {
            [mutableUsers removeObjectsAtIndexes:idxSet];
            [mutableUsers addObjectsFromArray:[_esRoom.users objectsAtIndexes:idxSet]];
            return mutableUsers;
        } else {
            [idxSet addIndex:idx]; idx++;
        }
    }
    
    return _esRoom.users;
}

- (void)updateRoomDisplay
{
    _roomNoLabel.string = _esRoom.roomNumber;
    _playTimeLabel.string = @(_esRoom.playTime).stringValue;
    
    _noChattingMark.visible = _esRoom.isNoChatting;
    if (_noChattingMark.visible) {
        [self.userObject runAnimationsForSequenceNamed:@"NoChattingMark"];
        [[BGAudioManager sharedAudioManager] playNoChatting];
    }
    _chatButton.enabled = !_noChattingMark.visible;
    
    _roomSettingButton.enabled = _esUser.isRoomOwner;
    _startGameButton.visible = _esUser.isRoomOwner;
    _readyStartButton.visible = (!_esUser.isRoomOwner && !_esUser.isReady);
    _cancelReadyButton.visible = (!_esUser.isRoomOwner && _esUser.isReady);
}

- (void)showPickedHeroMark
{
    [_selfSeat showPickedHeroMark];
}

#pragma mark - User/Seat update
- (void)addUser:(EsUser *)user
{
    BGRoomSeat *seat = [self vacantSeat];
    if (!seat) return;
    seat.isOccupied = YES;
    
    [seat setUserVisible:YES];
    seat.user = user;
    seat.nickNameLabel.string = user.nickName;
    seat.levelLabel.string = @(user.level).stringValue;
    seat.winRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.winRate];
    seat.userAvatar = [CCSprite spriteWithImageData:user.avatar key:user.userName];
    seat.vipMark.visible = user.isVIP;
    
    _occupiedSeats[user.userName] = seat;
    if (user.isMe) _selfSeat = seat;
    
    [self updateUser:user];
    
    if (!user.isRoomOwner) [[BGAudioManager sharedAudioManager] playJoinRoom];
}

- (void)updateUser:(EsUser *)user
{
    BGRoomSeat *seat = _occupiedSeats[user.userName];
    if (user.isRoomOwner) {
        [seat setIsRoomOwner:user.isRoomOwner];
    } else {
        [seat setIsReady:user.isReady];
        if (user.isReady) [[BGAudioManager sharedAudioManager] playReadyMark];
    }
    
    if (user.pickedHeroId > HeroCardNull) [seat showPickedHeroMark];
    
    if (user.isMe && user.isRoomOwner) {
        _roomSettingButton.enabled = YES;
        _startGameButton.visible = YES;
        _readyStartButton.visible = NO;
        _cancelReadyButton.visible = NO;
    }
    
    NSUInteger userCount = [BGClient sharedClient].esRoom.userCount;
    _startGameButton.enabled = (userCount == _esRoom.capacity && [self isAllUsersReady]);
    
//    _startGameButton.enabled = YES;
}

- (void)removeUserByUserName:(NSString *)userName
{
    BGRoomSeat *seat = _occupiedSeats[userName];
    seat.isOccupied = NO;
    [seat setUserVisible:NO];
    [_occupiedSeats removeObjectForKey:userName];
    
    _startGameButton.enabled = NO;
}

- (BGRoomSeat *)vacantSeat
{
    for (BGRoomSeat *seat in _allSeats) {
        if (!seat.isOccupied) return seat;
    }
    return nil;
}

- (BOOL)isAllUsersReady
{
    for (EsUser *user in [BGClient sharedClient].esRoom.users) {
        if (!user.isRoomOwner && !user.isReady) return NO;
    }
    return YES;
}

#pragma mark - Scene transition
- (BGStoreScene *)showStoreWithAvailableHeroIds:(NSArray *)heroIds
{
    BGStoreScene *storeScene = [BGTransitionManager transitSceneToUp:kCcbiStoreScene];
    storeScene.isPickHero = YES;
    storeScene.availableHeroIds = heroIds;
    [storeScene loadData];
    return storeScene;
}

- (BGGameScene *)showGameScene
{
    NSString *fileName = FILE_FULL_NAME(kCcbiGameScene, _esRoom.capacity, kFileTypeCCBI);
    return [BGTransitionManager transitSceneToLeft:fileName];
}

- (void)showGameResults
{
    [self addPopupMaskNode];
    
    NSString *fileName = FILE_FULL_NAME(kCcbiGameOver, _esRoom.capacity, kFileTypeCCBI);
    _gameOverPopup = (BGGameOver *)[CCBReader load:fileName];
    [_gameOverPopup showInNode:self];
    
    if ([self userIsVictory]) {
        [[BGAudioManager sharedAudioManager] playVictory];
    } else {
        [[BGAudioManager sharedAudioManager] playFailure];
    }
}

- (BOOL)userIsVictory
{
    for (BGGameResult *gameResult in self.victoryResults) {
        if ([gameResult.userName isEqual:[BGClient sharedClient].esUser.userName])
            return YES;
    }
    return NO;
}

- (void)backToZoneScene
{
    [BGChatPopup clearHistory];
    [BGTransitionManager transitSceneToRight:kCcbiZoneScene];
}

#pragma mark - Button selector
- (void)back:(CCButton *)sender
{
//    [self backToZoneScene];
    [[BGClient sharedClient] sendLeaveRoomRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)readyStart:(CCButton *)sender
{
    _readyStartButton.visible = NO;
    _cancelReadyButton.visible = YES;
    
    _esUser.isReady = YES;
    [[BGClient sharedClient] sendUpdateUserStatusVariableRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)cancelReady:(CCButton *)sender
{
    _cancelReadyButton.visible = NO;
    _readyStartButton.visible = YES;
    
    _esUser.isReady = NO;
    [[BGClient sharedClient] sendUpdateUserStatusVariableRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)startGame:(CCButton *)sender
{
    [self addLoadingMaskNode];
    [[BGClient sharedClient] sendStartGameRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

-(void)changeTable:(CCButton *)sender
{
    [self addLoadingMaskNode];
    
    _readyStartButton.visible = YES;
    _cancelReadyButton.visible = NO;
    
    _isChangeTable = YES;
    [[BGClient sharedClient] sendLeaveRoomRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)changeRoom
{
    _isChangeTable = NO;
    [[BGClient sharedClient] sendQuickJoinGameRequest];
}

- (void)rejoinRoom
{
    [self removeLoadingMaskNode];
    
    [self loadRoomInformation];
    [self updateRoomDisplay];
}

- (void)chat:(CCButton *)sender
{
    [self addPopupMaskNode];
    
    _chatPopup = (BGChatPopup *)[CCBReader load:kCcbiChatPopup];
    [_chatPopup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)showMessage:(NSString *)message ofUserName:(NSString *)userName
{    
    BGBubble *bubble = (BGBubble *)[CCBReader load:kCcbiBubble];
    [bubble setMessage:message];
    bubble.positionType = CCPositionTypeNormalized;
    bubble.position = ccp(0.5f, 0.7f);
    [bubble showInNode:_occupiedSeats[userName]];
    
    [[BGAudioManager sharedAudioManager] playMessage];
}

- (void)roomSetting:(CCButton *)sender
{
    [self addPopupMaskNode];
    
    BGPopup *popupNode = (BGPopup *)[CCBReader load:kCcbiRoomSetting];
    [popupNode show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)pickHero:(CCButton *)sender
{
    [self addLoadingMaskNode];
    [[BGClient sharedClient] sendShowPickingHerosRequest];
    
    [[BGAudioManager sharedAudioManager] playStoreClick];
}

#pragma mark - Popup delegate
- (void)didDismissPopup:(BGPopup *)popup
{
    _chatPopup = nil;
    _gameOverPopup = nil;
    [self removePopupMaskNode];
    [_effectNode clearEffect];
}

#pragma mark - Touch event
- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    _isTouchedInCCGLView = [touch.view isKindOfClass:[CCGLView class]];
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    if (touch.tapCount == 0) return;
    
    if (_chatPopup && !CGRectContainsPoint(_chatPopup.bgBoundingBox, [touch glLocationInTouchedView])) {
        if (_chatPopup.isKeyboardAppeared) {
            _chatPopup.isKeyboardAppeared = NO;
        } else {
            if (_isTouchedInCCGLView) [_chatPopup dismiss];
        }
    }
    
    if (_gameOverPopup) [_gameOverPopup dismiss];
}

#pragma mark - Transition
- (void)onExitTransitionDidStart
{
    [super onExitTransitionDidStart];
//  Remove the table view of chatting
#if __CC_PLATFORM_IOS
    [[CCDirector sharedDirector].view.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
#endif
}

@end
