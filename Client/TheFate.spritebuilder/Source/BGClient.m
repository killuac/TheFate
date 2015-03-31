//
//  BGClient.m
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGClient.h"
#import "BGLoginScene.h"


@interface BGClient ()

@property (nonatomic, weak) BGLoginScene *loginScene;
@property (nonatomic, weak) BGZoneScene *zoneScene;
@property (nonatomic, weak) BGRoomScene *roomScene;
@property (nonatomic, weak) BGGameScene *gameScene;
@property (nonatomic, weak) BGStoreScene *storeScene;

@property (nonatomic, weak) BGTable *table;
@property (nonatomic, weak) BGPlayer *player;

@end

@implementation BGClient {
    NSTimer *_timer;
    NSUInteger _connectionAttempts;
    
    BOOL _isLoginError;
    BOOL _isUserAlreadyLogout;
    NSUInteger _loginAttempts;
    
    NSString *_userName, *_password;
    int _zoneId, _roomId;
}

static BGClient *instanceOfClient = nil;

+ (BGClient *)sharedClient
{
    if (!instanceOfClient) {
        instanceOfClient = [[self alloc] init];
    }
	return instanceOfClient;
}

- (EsUser *)esUser
{
    return _es.managerHelper.userManager.me;
}

- (EsRoom *)esRoom
{
    return [[_es.managerHelper.zoneManager zoneById:_zoneId] roomById:_roomId];
}

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

- (BGLoginScene *)loginScene
{
    id node = ([[self runningSceneNode] isKindOfClass:[BGLoginScene class]]) ? [self runningSceneNode] : nil;
    return (_loginScene) ? _loginScene : node;
}

- (BGZoneScene *)zoneScene
{
    id node = ([[self runningSceneNode] isKindOfClass:[BGZoneScene class]]) ? [self runningSceneNode] : nil;
    return (_zoneScene) ? _zoneScene : node;
}

- (BGStoreScene *)storeScene
{
    id node = ([[self runningSceneNode] isKindOfClass:[BGStoreScene class]]) ? [self runningSceneNode] : nil;
    return (_storeScene) ? _storeScene : node;
}

- (BGRoomScene *)roomScene
{
    id node = ([[self runningSceneNode] isKindOfClass:[BGRoomScene class]]) ? [self runningSceneNode] : nil;
    return (_roomScene) ? _roomScene : node;
}

- (BGGameScene *)gameScene
{
    id node = ([[self runningSceneNode] isKindOfClass:[BGGameScene class]]) ? [self runningSceneNode] : nil;
    return (_gameScene) ? _gameScene : node;
}

- (BGTable *)table
{
    return self.gameScene.table;
}

- (BGPlayer *)player
{
    return self.gameScene.selfPlayer;
}

#pragma mark - Server connection
- (void)conntectServer
{    
    _es = [[ElectroServer alloc] init];
    NSString *path = [[CCFileUtils sharedFileUtils] fullPathFromRelativePath:kXmlSettings];
    [_es loadAndConnect:path];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onConnectionResponse:)
                           eventIdentifier:EsMessageType_ConnectionResponse];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onConnectionClosedEvent:)
                           eventIdentifier:EsMessageType_ConnectionClosedEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onGenericErrorResponse:)
                           eventIdentifier:EsMessageType_GenericErrorResponse];
}

- (void)onConnectionResponse:(EsConnectionResponse *)e
{
    if (e.successful) {
        _connectionAttempts = 0;
        if (_isUserAlreadyLogout || _isLoginError) return;
        
        if (self.loginScene.isAutoLogin) {
            [self.loginScene login];
        } else if (_userName.length > 0) {
            [self relogin];
        }
    }
    else {
        [self.runningSceneNode removeLoadingMaskNode];
        [BGUtil showInformationWithMessage:INFO_CONNECTION_FAILED];
        
        _connectionAttempts++;
        if (_connectionAttempts > MAX_CONNECTION_ATTEMPTS) return;
        
        _timer = [NSTimer scheduledTimerWithTimeInterval:DELAY_REMOVE_LOADING
                                                  target:self
                                                selector:@selector(conntectServer)
                                                userInfo:nil
                                                 repeats:NO];
    }
}

- (void)onConnectionClosedEvent:(EsConnectionClosedEvent *)e
{
    _loginAttempts = 0;
    [self conntectServer];
}

- (void)onGenericErrorResponse:(EsGenericErrorResponse *)e
{
//  Rejoin room, but the room was destroyed
    if (EsErrorType_ZoneNotFound == e.errorType) {
        if ([self.runningSceneNode respondsToSelector:@selector(backToZoneScene)])
            [self.runningSceneNode backToZoneScene];
    }
    else if (EsErrorType_LoginEventHandlerFailure == e.errorType) {
        [self.runningSceneNode removeLoadingMaskNode];
        [BGChatPopup clearHistory];
        [BGTransitionManager transitSceneToRight:kCcbiLoginScene];
    }
}

- (void)loginWithUserName:(NSString *)userName password:(NSString *)password isRegister:(BOOL)isRegister
{
    [self registerEventListener];
    
    EsLoginRequest *lr = [[EsLoginRequest alloc] init];
    lr.userName = _userName = userName;
    lr.password = _password = password;
    lr.esObject = [[EsObject alloc] init];
    [lr.esObject setBool:isRegister forKey:kParamIsRegister];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [lr.esObject setString:[BGUtil getPublicIPAddress] forKey:kParamIPAddress];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [_es.engine sendMessage:lr];
        });
    });
}

- (void)relogin
{
    [[self runningSceneNode] addLoadingMaskNode];
    [self loginWithUserName:_userName password:_password isRegister:NO];
}

- (void)onLoginResponse:(EsLoginResponse *)e
{
//  Disconnected(Due to lock screen) and login again
    if (self.roomScene || self.gameScene) {
        [self sendRejoinRoomRequest]; return;
    }
    
    if (self.zoneScene && e.successful) {
        [[self runningSceneNode] removeLoadingMaskNode]; return;
    }
    
    if (self.loginScene) {
        if (e.successful || EsErrorType_UserAlreadyLoggedIn == e.error) {
            _loginAttempts = 0;
            _isLoginError = _isUserAlreadyLogout = NO;
            
            self.loginScene.userNameText.string = _userName;
            self.loginScene.passwordText.string = _password;
            _zoneScene = [self.loginScene showZoneScene];
        }
        else {
            if (e.esObject) {
                _isLoginError = YES;
                [self.loginScene showWrongPrompt:e.esObject];
            } else {
                [self doSecondLogin];
            }
        }
    }
}

- (void)doSecondLogin
{
    if (_loginAttempts < MAX_LOGIN_ATTEMPTS) {
        _loginAttempts++;
        [self relogin];
    }
}

- (void)sendLogoutRequest
{
    _isUserAlreadyLogout = YES;
    
    EsLogOutRequest *lor = [[EsLogOutRequest alloc] init];
    lor.dropConnection = YES;
    lor.dropAllConnections = YES;
    [_es.engine sendMessage:lor];
}

- (void)registerEventListener
{
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onLoginResponse:)
                           eventIdentifier:EsMessageType_LoginResponse];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onZonePluginMessageEvent:)
                           eventIdentifier:EsMessageType_PluginMessageEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onStorePluginMessageEvent:)
                           eventIdentifier:EsMessageType_PluginMessageEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onCreateOrJoinGameResponse:)
                           eventIdentifier:EsMessageType_CreateOrJoinGameResponse];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onJoinRoomEvent:)
                           eventIdentifier:EsMessageType_JoinRoomEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onLeaveRoomEvent:)
                           eventIdentifier:EsMessageType_LeaveRoomEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onRoomVariableUpdateEvent:)
                           eventIdentifier:EsMessageType_RoomVariableUpdateEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onUserUpdateEvent:)
                           eventIdentifier:EsMessageType_UserUpdateEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onUserEvictedFromRoomEvent:)
                           eventIdentifier:EsMessageType_UserEvictedFromRoomEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onServerKickUserEvent:)
                           eventIdentifier:EsMessageType_ServerKickUserEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onUserVariableUpdateEvent:)
                           eventIdentifier:EsMessageType_UserVariableUpdateEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onRoomPluginMessageEvent:)
                           eventIdentifier:EsMessageType_PluginMessageEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onGamePluginMessageEvent:)
                           eventIdentifier:EsMessageType_PluginMessageEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onPublicMessageEvent:)
                           eventIdentifier:EsMessageType_PublicMessageEvent];
    
    [_es.engine addEventListenerWithTarget:self
                                    action:@selector(onPrivateMessageEvent:)
                           eventIdentifier:EsMessageType_PrivateMessageEvent];
}

#pragma mark - Store plugin request/response/event
- (void)sendServerPluginRequestWithPluginName:(NSString *)pluginName esObject:(EsObject *)esObj
{
    EsPluginRequest *pr = [[EsPluginRequest alloc] init];
    pr.pluginName = pluginName;
    pr.parameters = esObj;
    [_es.engine sendMessage:pr];
}

- (void)sendStorePluginRequestWithObject:(EsObject *)esObj
{
    [self sendServerPluginRequestWithPluginName:PLUGIN_STORE esObject:esObj];
    NSLog(@"Send STORE plugin request: %@", esObj);
}

- (void)sendShowMerchandisesRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionShowMerchandises forKey:kAction];
    [self sendStorePluginRequestWithObject:obj];
}

- (void)sendShowPickingHerosRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionShowPickingHeros forKey:kAction];
    [self sendStorePluginRequestWithObject:obj];
}

- (void)sendBuyGoldCoinRequestWithReceipt:(NSString *)receiptStr
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionBuyGoldCoin forKey:kAction];
    [obj setString:self.storeScene.boughtProdcutId forKey:kBoughtProductId];
    [obj setString:receiptStr forKey:kReceiptString];
    [self sendStorePluginRequestWithObject:obj];
}

- (void)sendBuyHeroCardRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionBuyHeroCard forKey:kAction];
    [obj setInt:(int32_t)self.storeScene.boughtHeroId forKey:kBoughtHeroId];
    [self sendStorePluginRequestWithObject:obj];
}

- (void)sendBuyVIPCardRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionBuyVIPCard forKey:kAction];
    [obj setInt:(int32_t)self.storeScene.boughtVIPTypeId forKey:kBoughtVIPTypeId];
    [self sendStorePluginRequestWithObject:obj];
}

- (void)sendPickOneHeroStoreRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionPickOneHero forKey:kAction];
    [obj setInt:(int32_t)self.storeScene.pickedHeroId forKey:kParamPickedHeroId];
    [self sendStorePluginRequestWithObject:obj];
}

/*
 * Receive store plugin(Server Level) message event
 */
- (void)onStorePluginMessageEvent:(EsPluginMessageEvent *)e
{
    EsObject *esObj = e.parameters;
    NSInteger action = [esObj intWithKey:kAction];
    
    switch (action) {
        case ActionShowMerchandises:
            _storeScene = [self.zoneScene showStoreScene];
            _storeScene.goldCoinIds = [esObj stringArrayWithKey:kGoldCoinIds];
            _storeScene.vipTypeIds = [esObj intArrayWithKey:kVIPTypeIds];
            break;
            
        case ActionShowPickingHeros:
            _storeScene = [self.roomScene showStoreWithAvailableHeroIds:[esObj intArrayWithKey:kParamCardIdList]];
            break;
            
        case ActionBuyGoldCoin:
            [self.storeScene removeLoadingMaskNode];
            [self.storeScene updateGoldCoin:YES];
            break;
            
        case ActionBuyHeroCard: {
            BGHeroCard *heroCard = [BGHeroCard cardWithCardId:[esObj intWithKey:kBoughtHeroId]];
            NSString *message = [BGUtil textWith:INFO_BOUGHT_HERO parameter:heroCard.cardText];
            [BGUtil showInformationWithMessage:message];
            [self.storeScene updateGoldCoin:NO];
            break;
        }
            
        case ActionBuyVIPCard:
            [BGUtil showInformationWithMessage:INFO_BECAME_VIP];
            [self.storeScene updateGoldCoin:NO];
            break;
            
        default:
            break;
    }
}

#pragma mark - Zone plugin request/response/event
- (void)sendZonePluginRequestWithObject:(EsObject *)esObj
{
    [self sendServerPluginRequestWithPluginName:PLUGIN_ZONE esObject:esObj];
    NSLog(@"Send ZONE plugin request: %@", esObj);
}

- (void)sendRejoinRoomRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionRejoinRoom forKey:kAction];
    [obj setInt:_zoneId forKey:kParamZoneId];
    [obj setInt:_roomId forKey:kParamRoomId];
    [obj setString:self.esRoom.roomPassword forKey:kParamRoomPassword];
    [self sendZonePluginRequestWithObject:obj];
}

- (void)sendFindRoomRequestWithRoomNumber:(NSString *)roomNumber password:(NSString *)password
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionFindRoom forKey:kAction];
    [obj setString:roomNumber forKey:kParamRoomNumber];
    [obj setString:password forKey:kParamRoomPassword];
    [self sendZonePluginRequestWithObject:obj];
}

- (void)sendFeedbackRequestWithTitle:(NSString *)title content:(NSString *)content
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionFeedback forKey:kAction];
    [obj setString:title forKey:kParamIssueTitle];
    [obj setString:content forKey:kParamIssueContent];
    [self sendZonePluginRequestWithObject:obj];
}

- (void)joinRoom:(EsObject *)esObj
{
    EsJoinRoomRequest *jrr = [[EsJoinRoomRequest alloc] init];
    jrr.zoneId = [esObj intWithKey:kParamZoneId];
    jrr.roomId = [esObj intWithKey:kParamRoomId];
    jrr.password = [esObj stringWithKey:kParamRoomPassword];
    [_es.engine sendMessage:jrr];
}

/*
 * Receive zone plugin(Server Level) message event
 */
- (void)onZonePluginMessageEvent:(EsPluginMessageEvent *)e
{
    EsObject *esObj = e.parameters;
    NSInteger action = [esObj intWithKey:kAction];
    
    switch (action) {
        case ActionRejoinRoom: {        // Maybe rejoin after disconnection
            if (RoomFindResultFound == [esObj intWithKey:kRoomFindResult]) {
                [self joinRoom:esObj];
            } else {
                if ([[self runningSceneNode] respondsToSelector:@selector(backToZoneScene)]) {
                    [[self runningSceneNode] backToZoneScene];
                }
            }
            break;
        }
        
        case ActionFindRoom:
            if (RoomFindResultFound == [esObj intWithKey:kRoomFindResult]) {
                [self joinRoom:esObj];
            } else {
                if (self.roomScene) {   // Rejoin, but can't found the room
                    [self.roomScene backToZoneScene];
                } else {
                    [self.zoneScene showWrongPrompt:esObj];
                }
            }
            break;
            
        case ActionFeedback:
            [BGUtil showInformationWithMessage:INFO_FEEDBACK_SUCCESS];
            break;
            
        default:
            break;
    }
}

#pragma mark - Create/Quick Join room
/*
 * Create game or quick join existing game room
 */
- (void)sendQuickJoinGameRequest
{
    EsQuickJoinGameRequest *qjgr = [[EsQuickJoinGameRequest alloc] init];
    qjgr.gameType = _gameType;
    qjgr.zoneName = _gameType;
    qjgr.hidden = NO;
    qjgr.locked = NO;
    qjgr.createOnly = NO;
    [_es.engine sendMessage:qjgr];
}

- (void)onCreateOrJoinGameResponse:(EsCreateOrJoinGameResponse *)e
{
    
}

/*
 * Send create room message with all plugins to server
 */
- (void)sendCreateRoomRequestWithPassword:(NSString *)password gameDetails:(EsObject *)gameDetails
{
    EsQuickJoinGameRequest *qjgr = [[EsQuickJoinGameRequest alloc] init];
    qjgr.gameType = _gameType;
    qjgr.zoneName = _gameType;
    qjgr.password = password;
    qjgr.hidden = NO;
    qjgr.locked = NO;
    qjgr.createOnly = YES;
    qjgr.gameDetails = gameDetails;
    [_es.engine sendMessage:qjgr];
}

- (void)onJoinRoomEvent:(EsJoinRoomEvent *)e
{
    [[self runningSceneNode] removeLoadingMaskNode];
    
    _zoneId = e.zoneId;
    _roomId = e.roomId;
    
    if (self.zoneScene) {
        _roomScene = [self.zoneScene showRoomScene];
    } else if (self.roomScene) {
        [self.roomScene rejoinRoom];
    }
    
    if (1 == e.users.count) [self sendUpdateRoomDetailsRequest];
}

/*
 * Update room detail info. If no value changed, won't receive notification.
 */
- (void)sendUpdateRoomDetailsRequest
{
    EsUpdateRoomDetailsRequest *urdr = [[EsUpdateRoomDetailsRequest alloc] init];
    urdr.roomId = _roomId;
    urdr.zoneId = _zoneId;
    urdr.roomNameUpdate = YES;
    urdr.roomName = self.esRoom.roomNumber;
    urdr.passwordUpdate = YES;
    urdr.password = self.esRoom.roomPassword;
    [_es.engine sendMessage:urdr];
}

/*
 * Update room variable
 */
- (void)sendUpdateRoomVariableRequest
{
    EsUpdateRoomVariableRequest *urvr = [[EsUpdateRoomVariableRequest alloc] init];
    urvr.zoneId = _zoneId;
    urvr.roomId = _roomId;
    urvr.name = self.esRoom.roomSetting.name;
    urvr.valueChanged = YES;
    urvr.value = self.esRoom.roomSetting.value;
    [_es.engine sendMessage:urvr];
}

- (void)onRoomVariableUpdateEvent:(EsRoomVariableUpdateEvent *)e
{
    switch (e.action) {
        case EsRoomVariableUpdateAction_VariableUpdated:
            [self.roomScene updateRoomDisplay];
            break;
    }
}

/*
 * Send leave room request(Called when user exit room)
 */
- (void)sendLeaveRoomRequest
{
    EsLeaveRoomRequest *lrr = [[EsLeaveRoomRequest alloc] init];
    lrr.zoneId = _zoneId;
    lrr.roomId = _roomId;
    [_es.engine sendMessage:lrr];
}

- (void)onLeaveRoomEvent:(EsLeaveRoomEvent *)e
{
    if (self.roomScene) {
        if (self.roomScene.isChangeTable) {
            [self.roomScene changeRoom];
        } else {
            [self.roomScene backToZoneScene];
        }
    } else if (self.gameScene) {
        [self.gameScene backToZoneScene];
    }
}

#pragma mark - User update in room
- (void)onUserUpdateEvent:(EsUserUpdateEvent *)e
{
    switch (e.action) {
        case EsUserUpdateAction_AddUser:
            [self.roomScene addUser:[self userByUserName:e.userName]];
            break;
        
        case EsUserUpdateAction_DeleteUser:
            [self.roomScene removeUserByUserName:e.userName];
            break;
    }
}

- (EsUser *)userByUserName:(NSString *)userName
{
    for (EsUser *user in self.esRoom.users) {
        if ([user.userName isEqualToString:userName])
            return user;
    }
    return nil;
}

- (void)sendKickUserRequest:(NSString *)userName
{
    EsEvictUserFromRoomRequest *eur = [[EsEvictUserFromRoomRequest alloc] init];
    eur.userName = userName;
    eur.ban = NO;
    eur.duration = 0;
    eur.zoneId = _zoneId;
    eur.roomId = _roomId;
    eur.reason = @"Shit";
    [_es.engine sendMessage:eur];
}

- (void)onUserEvictedFromRoomEvent:(EsUserEvictedFromRoomEvent *)e
{
    if (e.reason.length > 0) {
        EsUser *user = [self userByUserName:e.userName];
        [self performSelector:@selector(showInformation:)
                   withObject:user.nickName
                   afterDelay:SCHEDULE_DALAY_TIME];
    }
}

- (void)showInformation:(NSString *)userName
{
    NSString *message = [BGUtil textWith:INFO_USER_WAS_KICKED parameter:userName];
    [BGUtil showInformationWithMessage:message];
}

- (void)onServerKickUserEvent:(EsServerKickUserEvent *)e
{
//  Logout and same user login will receive this event
    if (!_isUserAlreadyLogout) {
        _isUserAlreadyLogout = YES;
        [BGTransitionManager transitSceneToRight:kCcbiLoginScene];
    }
}

- (void)sendUpdateUserInfoVariableRequest
{
    EsUpdateUserVariableRequest *uuvr = [[EsUpdateUserVariableRequest alloc] init];
    uuvr.name = self.esUser.userInfo.name;
    uuvr.value = self.esUser.userInfo.value;
    [_es.engine sendMessage:uuvr];
}

- (void)sendUpdateUserStatusVariableRequest
{
    EsUpdateUserVariableRequest *uuvr = [[EsUpdateUserVariableRequest alloc] init];
    uuvr.name = self.esUser.userStatus.name;
    uuvr.value = self.esUser.userStatus.value;
    [_es.engine sendMessage:uuvr];
}

- (void)onUserVariableUpdateEvent:(EsUserVariableUpdateEvent *)e
{
    switch (e.action) {
        case EsUserVariableUpdateAction_VariableUpdated: {
            if ([e.variable.name isEqualToString:kVarUserStatus] &&
                [self.roomScene respondsToSelector:@selector(updateUser:)]) {
                [self.roomScene updateUser:[self userByUserName:e.userName]];
            }
            break;
        }
    }
}

#pragma mark - Room plugin reqeust/response/event
- (void)sendPluginRequestWithPluginName:(NSString *)pluginName esObject:(EsObject *)esObj
{
    EsPluginRequest *pr = [[EsPluginRequest alloc] init];
    pr.pluginName = pluginName;
    pr.zoneId = _zoneId;
    pr.roomId = _roomId;
    pr.parameters = esObj;
    [_es.engine sendMessage:pr];
}

- (void)sendRoomPluginRequestWithObject:(EsObject *)esObj
{
    [self sendPluginRequestWithPluginName:PLUGIN_ROOM esObject:esObj];
    NSLog(@"Send ROOM plugin request: %@", esObj);
}

- (void)sendPickOneHeroRoomRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionPickOneHero forKey:kAction];
    [obj setInt:(int32_t)self.storeScene.pickedHeroId forKey:kParamPickedHeroId];
    [self sendRoomPluginRequestWithObject:obj];
}

- (void)onRoomPluginMessageEvent:(EsPluginMessageEvent *)e
{
    if (![e.pluginName isEqualToString:PLUGIN_ROOM]) return;
    NSLog(@"Receive room PLUGIN message: %@", e.parameters);
    
    EsObject *esObj = e.parameters;
    NSInteger action = [esObj intWithKey:kAction];
    
//  Action handling
    switch (action) {
        case ActionPickOneHero:
            if ([esObj boolWithKey:kParamHeroIsPicked]) {
                BGHeroCard *heroCard = [BGHeroCard cardWithCardId:[esObj intWithKey:kParamPickedHeroId]];
                NSString *message = [BGUtil textWith:INFO_HERO_WAS_PICKED parameter:heroCard.cardText];
                [BGUtil showInformationWithMessage:message];
            } else {
                [self sendPickOneHeroStoreRequest];
                self.roomScene = [self.storeScene backToRoomScene];
                [self.roomScene showPickedHeroMark];
            }
            break;
            
        default:
            break;
    }
}

#pragma mark - Game plugin reqeust/response/event
- (void)sendGamePluginRequestWithObject:(EsObject *)esObj
{
    [self sendPluginRequestWithPluginName:PLUGIN_GAME esObject:esObj];
    NSLog(@"Send game PLUGIN request: %@", esObj);
}

- (void)sendStartGameRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionStartGame forKey:kAction];
    [esObj setInt:(int32_t)self.esRoom.capacity forKey:kParamPlayerCount];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendStartRoundRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionStartRound forKey:kAction];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendExitGameRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionExitGame forKey:kAction];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendSelectHeroIdRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionSelectHero forKey:kAction];
    [esObj setInt:(int32_t)self.player.selectedHeroId forKey:kParamSelectedHeroId];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToCompareRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToCompare forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)setPlayedCardIdList:(EsObject *)esObj {
    [esObj setIntArray:self.player.playedCardIds forKey:kParamCardIdList];
}

- (void)setSelectedCardIdList:(EsObject *)esObj {
    [esObj setIntArray:self.player.selectedCardIds forKey:kParamCardIdList];
}

- (void)setCardIndexList:(EsObject *)esObj {
    [esObj setIntArray:self.player.selectedCardIdxes forKey:kParamCardIndexList];
}

- (void)setTargetPlayerNames:(EsObject *)esObj {
    [esObj setStringArray:self.player.targetPlayerNames forKey:kParamTargetPlayerNames];
}

- (void)sendUseHandCardRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionUseHandCard forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendUseHeroSkillRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionUseHeroSkill forKey:kAction];
    [esObj setInt:(int32_t)self.player.heroSkillId forKey:kParamHeroSkillId];
    [esObj setInt:(int32_t)self.player.transformedCardId forKey:kParamTransformedCardId];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendUseEquipmentRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionUseEquipment forKey:kAction];
    [esObj setInt:(int32_t)self.player.equipmentId forKey:kParamEquipmentId];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendCancelHeroSkillRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionCancelHeroSkill forKey:kAction];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendCancelEquipmentRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionCancelEquipment forKey:kAction];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendStartDiscardRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionStartDiscard forKey:kAction];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendOkayToDiscardRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionOkayToDiscard forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToUseRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToUse forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToGetRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToGet forKey:kAction];
    if (self.player.selectedCardIdxes.count > 0) {
        [self setCardIndexList:esObj];
    }
    if (self.player.selectedCardIds.count > 0) {
        [self setSelectedCardIdList:esObj];
    }
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToGiveRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToGive forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToRemoveRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToRemove forKey:kAction];
    if (self.player.selectedCardIdxes.count > 0) {
        [self setCardIndexList:esObj];
    }
    if (self.player.selectedCardIds.count > 0) {
        [self setSelectedCardIdList:esObj];
    }
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseCardToDropRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseCardToDrop forKey:kAction];
    [self setPlayedCardIdList:esObj];
    [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseColorRequest
{
    EsObject *obj = [[EsObject alloc] init];
    [obj setInt:ActionChoseColor forKey:kAction];
    [obj setInt:self.player.selectedColor forKey:kParamSelectedColor];
    [self sendGamePluginRequestWithObject:obj];
}

- (void)sendChoseSuitsRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseSuits forKey:kAction];
    [esObj setInt:self.player.selectedSuits forKey:kParamSelectedSuits];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendAsignedCardRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionAssignedCard forKey:kAction];
    [esObj setIntArray:self.player.assignedCardIds forKey:kParamCardIdList];
    [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendChoseTargetPlayerRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseTargetPlayer forKey:kAction];
    [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)choseViewPlayerRoleRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionChoseViewPlayerRole forKey:kAction];
    [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendOkayRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionOkay forKey:kAction];
    if (self.player.isNeedTargetAgain) [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (void)sendCancelRequest
{
    EsObject *esObj = [[EsObject alloc] init];
    [esObj setInt:ActionCancel forKey:kAction];
    if (self.player.isNeedTargetAgain) [self setTargetPlayerNames:esObj];
    [self sendGamePluginRequestWithObject:esObj];
}

- (NSUInteger)playTime:(EsObject *)esObj {
    return [esObj intWithKey:kParamPlayTime];
}

- (NSString *)damageSourceName:(EsObject *)esObj {
    return [esObj stringWithKey:kParamDamageSourceName];
}

- (NSArray *)targetPlayerNames:(EsObject *)esObj {
    return [esObj stringArrayWithKey:kParamTargetPlayerNames];
}

- (NSArray *)cardIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamCardIdList];
}

- (NSArray *)equipmentIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamEquipmentIdList];
}

- (NSArray *)availableCardIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamAvailableCardIdList];
}

- (NSArray *)availableSkillIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamAvailableSkillIdList];
}

- (NSArray *)availableEquipIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamAvailableEquipIdList];
}

- (NSArray *)droppableEquipIdList:(EsObject *)esObj {
    return [esObj intArrayWithKey:kParamDroppableEquipIdList];
}

- (NSUInteger)cardCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamCardCount];
}

- (NSUInteger)handCardCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamHandCardCount];
}

- (NSUInteger)selectableCardCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamSelectableCardCount];
}

- (NSUInteger)requiredSelCardCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamRequiredSelCardCount];
}

- (NSUInteger)requiredTargetCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamRequiredTargetCount];
}

- (NSUInteger)maxTargetCount:(EsObject *)esObj {
    return [esObj intWithKey:kParamMaxTargetCount];
}

- (BGUpdateReason)playerUpdateReason:(EsObject *)esObj {
    return [esObj intWithKey:kPlayerUpdateReason];
}

/*
 * Receive game plugin message event. Handle message according to different actions.
 */
- (void)onGamePluginMessageEvent:(EsPluginMessageEvent *)e
{
    if (![e.pluginName isEqualToString:PLUGIN_GAME]) return;
    NSLog(@"Receive game PLUGIN message: %@", e.parameters);
    
    EsObject *esObj = e.parameters;
    NSInteger action = [esObj intWithKey:kAction];
    if (ActionStartGame == action) {
        if (self.gameScene) {
            self.gameScene = [[self.gameScene backToRoomScene] showGameScene];
        } else {
            self.gameScene = [self.roomScene showGameScene];
        }
    }
    
    self.gameScene.action = action;
    self.gameScene.state = [esObj intWithKey:kGameState];
    NSAssert(ActionNull != action, @"Invalid action, may someone cheating - Crash");
    
//  Action handling
    switch (action) {
        case ActionTableDeckCardCount:
            self.gameScene.deckCardCount = [esObj intWithKey:kParamDeckCardCount];
            break;
            
        case ActionStartGame:
            self.gameScene.playTime = self.esRoom.playTime;
            self.gameScene.isNoChatting = self.esRoom.isNoChatting;
            [self.gameScene renderAllPlayers:[esObj stringArrayWithKey:kParamAllPlayerNames] withRoleIds:[self cardIdList:esObj]];
            self.gameScene.fateCardId = [esObj intWithKey:kParamCardId];
            self.table = self.gameScene.table;
            self.player = self.gameScene.selfPlayer;
            break;
            
        case ActionStartRound:
            [self.gameScene addHistory];
            break;
            
        case ActionShowAllPlayersRole:
            self.gameScene.turnOwnerName = [esObj stringWithKey:kParamTurnOwnerName];
            [self.gameScene renderAllPlayersRoleWithRoleIds:[self cardIdList:esObj]];
            break;
            
        case ActionTableDealCandidateHero:
            [self.player addTextPrompt];
            [self.player addProgressBarWithDuration:[self playTime:esObj]];
            [self.table showCandidateHerosWithHeroIds:[self cardIdList:esObj]];
            [self.gameScene addProgressBarForOtherPlayers];
            break;
            
        case ActionPlayerSelectedHero:
            if ([esObj boolWithKey:kParamIsPickedHero]) {
                [self.gameScene addProgressBarForOtherPlayers];
            }
            [self.player renderHeroWithHeroId:[esObj intWithKey:kParamSelectedHeroId]];
            break;
            
        case ActionTableShowAllSelectedHeros:
            [self.gameScene renderOtherPlayersHeroWithHeroIds:[self cardIdList:esObj]];
            break;
            
        case ActionPlayerDealHandCard:
            [self.player dealHandCardWithCardIds:[esObj intArrayWithKey:kParamCardIdList]];
            break;
            
        case ActionChooseCardToCompare:
            self.player.selectableCardCount = [self selectableCardCount:esObj];
            self.player.requiredSelCardCount = [self requiredSelCardCount:esObj];
            [self.player addProgressBar];
            [self.player addTextPrompt];
            [self.player addPlayingButtons];
            [self.gameScene addProgressBarForOtherPlayers];
            break;
            
        case ActionTableShowAllComparedCards:
            [self.gameScene removeProgressBarForOtherPlayers];
            [self.table showComparedCardWithCardIds:[self cardIdList:esObj] maxCardId:[esObj intWithKey:kParamMaxFigureCardId]];
            [self.gameScene addHistoryOfPlayerWithEsObject:esObj];  // Add game history(牌局历史记录)
            break;
            
        case ActionTableShowPlayerAllCards:
            [self.table showPopupWithHandCount:[self handCardCount:esObj] equipmentIds:[self equipmentIdList:esObj]];
            break;
            
        case ActionTableShowPlayerHandCard:
            [self.table showPopupWithHandCount:[self handCardCount:esObj] equipmentIds:nil];
            break;
            
        case ActionTableShowPlayerEquipment:
            [self.table showPopupWithHandCount:0 equipmentIds:[self equipmentIdList:esObj]];
            break;
            
        case ActionTableShowAssignedCard:
            self.player.targetPlayerNames = [[self targetPlayerNames:esObj] mutableCopy];
            [self.table showPopupWithAssignedCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionPlayerUpdateHandCard:
            [self.player updateHandCardWithCardIds:[self cardIdList:esObj] reason:[self playerUpdateReason:esObj]];
            break;
            
        case ActionPlayerUpdateEquipment:
            [self.player updateEquipmentWithCardIds:[self equipmentIdList:esObj] reason:[self playerUpdateReason:esObj]];
            break;
            
        case ActionStartTurn:
            [self.player startTurn];
            break;
        
        case ActionPlayCard:
        case ActionChooseCardToUse:
            self.gameScene.turnOwner.targetPlayerNames = [[self targetPlayerNames:esObj] mutableCopy];
        case ActionChooseColor:
        case ActionChooseSuits:
        case ActionChooseCardToGive:
        case ActionChooseCardToDrop:
        case ActionChooseTargetPlayer:
            self.player.selectableCardCount = [self selectableCardCount:esObj];
            self.player.requiredSelCardCount = [self requiredSelCardCount:esObj];
            self.player.requiredTargetCount = [self requiredTargetCount:esObj];
            self.player.maxTargetCount = [self maxTargetCount:esObj];
            self.player.isRequiredDrop = [esObj boolWithKey:kParamIsRequiredDrop];
            self.player.isRequiredTarget = [esObj boolWithKey:kParamIsRequiredTarget];
            self.player.isNoNeedCard = [esObj boolWithKey:kParamIsNoNeedCard];
            self.player.isDyingTriggered = [esObj boolWithKey:kParamIsDyingTriggered];
            self.player.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            self.player.equipmentId = [esObj intWithKey:kParamEquipmentId];
            self.player.transformedCardId = [esObj intWithKey:kParamTransformedCardId];
            if ([esObj boolWithKey:kParamIsNewWaiting]) {
                [self.player addProgressBar];
                [self.player addTextPrompt];
                [self.player addPlayingButtons];
            }
            if (self.player.requiredTargetCount > 0) [self.player checkTargetPlayerEnablement];
            [self.player enableHandCardWithCardIds:[self availableCardIdList:esObj]];
            [self.player enableEquipmentWithCardIds:[self availableEquipIdList:esObj] isUse:YES];
            [self.player enableEquipmentWithCardIds:[self droppableEquipIdList:esObj] isUse:NO];
            [self.player enableHeroSkillWithSkillIds:[self availableSkillIdList:esObj]];    // Must be at last
            break;
            
        case ActionChooseCardToGet:
        case ActionChooseCardToRemove:
            [self.player resetSelectedCardIds];     // For skill Multicast
            self.player.isGreeding = [esObj boolWithKey:kParamIsGreeding];
            self.player.selectableCardCount = [self selectableCardCount:esObj];
            self.player.requiredSelCardCount = [self requiredSelCardCount:esObj];
            [self.player addProgressBar];
            [self.player addTextPrompt];
            break;
            
        case ActionChooseCardToAssign:
            [self.player addProgressBarWithDuration:[self playTime:esObj]];
            [self.player addTextPrompt];
            
        case ActionDiscardCard:
            self.player.selectableCardCount = [self selectableCardCount:esObj];
            self.player.requiredSelCardCount = [self requiredSelCardCount:esObj];
            [self.player addProgressBar];
            [self.player addTextPrompt];
            [self.player addPlayingButtons];
            [self.player enableHandCardWithCardIds:[self availableCardIdList:esObj]];
            break;
            
        case ActionWaitingDispel:
            [self.player resetAndRemovePlayingNodes];   // Unschedule timer
            self.gameScene.isWaitingDispel = YES;
            [self.gameScene addProgressBarForOtherPlayers];
            break;
            
        case ActionCancelWaiting:
            [self.player resetAndRemovePlayingNodes];
            self.gameScene.isWaitingDispel = NO;
            [self.gameScene removeProgressBarForOtherPlayers];
            break;
            
        case ActionChooseDrawCardOrViewRole:
            [self.player.targetPlayerNames removeAllObjects];   // Need clear for view target's role
            self.player.requiredTargetCount = [self requiredTargetCount:esObj];
            self.player.maxTargetCount = [self maxTargetCount:esObj];
        case ActionChooseOkayOrCancel:
            self.player.isStrengthening = [esObj boolWithKey:kParamIsStrengthening];
            self.player.isNoNeedCard = [esObj boolWithKey:kParamIsNoNeedCard];
            self.player.isDyingTriggered = [esObj boolWithKey:kParamIsDyingTriggered];
            self.player.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            self.player.equipmentId = [esObj intWithKey:kParamEquipmentId];
            [self.player addProgressBar];
            [self.player addTextPrompt];
            [self.player addPlayingButtons];
            break;
            
        case ActionPlayerShowRoleCard:
            [self.player.targetPlayer renderRoleWithRoleId:[esObj intWithKey:kParamCardId] animated:YES];
            break;
            
        case ActionGameOver: {      // Game over and show game results
            BGRoomScene *roomScene = [self.gameScene backToRoomScene];
            roomScene.victoryResults = [self gameResultsFromEsObjects:[esObj esObjectArrayWithKey:kParamVictoryResults]];
            roomScene.failureResults = [self gameResultsFromEsObjects:[esObj esObjectArrayWithKey:kParamFailureResults]];
            [roomScene showGameResults];
            _gameScene = nil;   // Must clear to avoid call [self rejoinRoom]
            break;
        }
            
        case ActionRejoinGame:
            [self.player updateHeroWithHealthPoint:[esObj intWithKey:kParamHeroHealthPoint] skillPoint:[esObj intWithKey:kParamHeroSkillPoint]];
            [self.player updateHandCardWithCardIds:[self cardIdList:esObj] reason:[self playerUpdateReason:esObj]];
            [self.player updateEquipmentWithCardIds:[self equipmentIdList:esObj] reason:[self playerUpdateReason:esObj]];
            [self.player enableHandCardWithCardIds:[self availableCardIdList:esObj]];
            [self.player enableEquipmentWithCardIds:[self availableEquipIdList:esObj] isUse:YES];
            [self.player enableEquipmentWithCardIds:[self droppableEquipIdList:esObj] isUse:NO];
            [self.player enableHeroSkillWithSkillIds:[self availableSkillIdList:esObj]];
            break;
            
        case ActionExitGame:
            [self.gameScene backToRoomScene];
            break;
            
        default:
            break;
    }
}

- (NSArray *)gameResultsFromEsObjects:(NSArray *)esObjects
{
    NSMutableArray *results = [NSMutableArray array];
    for (EsObject *esObj in esObjects) {
        [results addObject:[BGGameResult gameResultWithEsObject:esObj]];
    }
    return results;
}

#pragma mark - Public message
/*
 * Send public message to room (public message can contain a chat message and an optional EsObject)
 */
- (void)sendPublicMessage:(NSString *)message withEsObject:(EsObject *)esObj
{
    EsPublicMessageRequest *pmr = [[EsPublicMessageRequest alloc] init];
    pmr.zoneId = _zoneId;
    pmr.roomId = _roomId;
    pmr.message = message;
    pmr.esObject = esObj;
    [_es.engine sendMessage:pmr];
    
    NSLog(@"Send public message: %@", message);
    NSLog(@"Send public message: %@", esObj);
}

- (void)sendPublicMessage:(NSString *)message
{
    [self sendPublicMessage:message withEsObject:nil];
}

/*
 * Receive public message event. Broadcast it to all players in the same room.
 */
- (void)onPublicMessageEvent:(EsPublicMessageEvent *)e
{
    NSLog(@"Public message sender user: %@", e.userName);
    NSLog(@"Receive public message: %@", e.esObject);
    
    EsObject *esObj = e.esObject;
    NSInteger action = [esObj intWithKey:kAction];
    self.gameScene.action = action;
    self.gameScene.state = [esObj intWithKey:kGameState];
    self.gameScene.damageSourceName = [self damageSourceName:esObj];
    
//  Receive the player who send the public message and Set the target player names.
    BGPlayer *senderPlayer = [self.gameScene playerWithName:e.userName];
    senderPlayer.targetPlayerNames = [[self targetPlayerNames:esObj] mutableCopy];
    
//  Action handling
    if (!senderPlayer.isMe && esObj && !self.gameScene.isWaitingDispel) [senderPlayer removeProgressBar];
    switch (action) {
        case ActionStartTurn:
            self.gameScene.turnOwnerName = e.userName;
        case ActionPlayCard:
            self.gameScene.damageSourceName = e.userName;
            self.gameScene.activePlayerName = e.userName;
            if ([esObj boolWithKey:kParamIsNewWaiting]) [self.gameScene resetValueAfterResolved];
        case ActionDiscardCard:
        case ActionChooseCardToUse:
        case ActionChooseColor:
        case ActionChooseSuits:
        case ActionChooseCardToGet:
        case ActionChooseCardToGive:
        case ActionChooseCardToRemove:
        case ActionChooseCardToAssign:
        case ActionChooseCardToDrop:
        case ActionChooseTargetPlayer:
        case ActionChooseOkayOrCancel:
            if (!senderPlayer.isMe) [senderPlayer addProgressBar];
            break;
            
        case ActionDrawCard:
            if (!senderPlayer.isMe) [senderPlayer drawCardWithCardCount:[self cardCount:esObj]];
            break;
            
        case ActionUseHandCard:
        case ActionChoseCardToUse:
            self.gameScene.activePlayerName = e.userName;
            senderPlayer.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            senderPlayer.equipmentId = [esObj intWithKey:kParamEquipmentId];
            [senderPlayer launchHeroSkillWithSkillId:senderPlayer.heroSkillId];
            [senderPlayer launchEquipmentWithCardId:senderPlayer.equipmentId];
            if (!senderPlayer.isMe) [senderPlayer useHandCardWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionOkay:
            senderPlayer.isStrengthened = [esObj boolWithKey:kParamIsStrengthened];
            senderPlayer.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            senderPlayer.equipmentId = [esObj intWithKey:kParamEquipmentId];
            [senderPlayer.equipmentCard resolveOkay];
            [senderPlayer launchHeroSkillWithSkillId:senderPlayer.heroSkillId];
            [senderPlayer launchEquipmentWithCardId:senderPlayer.equipmentId];
            break;
            
        case ActionChoseTargetPlayer:
            self.gameScene.activePlayerName = e.userName;
            if (!senderPlayer.isMe) [senderPlayer showTargetLinePath];
        case ActionUseHeroSkill:
        case ActionUseEquipment:
            senderPlayer.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            senderPlayer.equipmentId = [esObj intWithKey:kParamEquipmentId];
            senderPlayer.transformedCardId = [esObj intWithKey:kParamTransformedCardId];
            [senderPlayer launchHeroSkillWithSkillId:senderPlayer.heroSkillId];
            break;
            
        case ActionPlayerSkillOrEquipmentTriggered:
            // Just play sound, don't set skillId which will affect tip text.
            [senderPlayer launchHeroSkillWithSkillId:[esObj intWithKey:kParamHeroSkillId]];
            [senderPlayer launchEquipmentWithCardId:[esObj intWithKey:kParamEquipmentId]];
            break;
            
        case ActionClearHeroSkill:
            senderPlayer.heroSkillId = HeroSkillNull;
            break;
            
        case ActionClearEquipment:
            senderPlayer.equipmentId = PlayingCardNull;
            break;
            
        case ActionChoseColor:
            senderPlayer.selectedColor = [esObj intWithKey:kParamSelectedColor];
            break;
            
        case ActionChoseSuits:
            senderPlayer.selectedSuits = [esObj intWithKey:kParamSelectedSuits];
            break;
            
        case ActionChoseCardToDrop:
        case ActionOkayToDiscard:
            self.gameScene.activePlayerName = e.userName;
            senderPlayer.heroSkillId = [esObj intWithKey:kParamHeroSkillId];
            senderPlayer.equipmentId = [esObj intWithKey:kParamEquipmentId];
            [senderPlayer.equipmentCard resolveUse];
            [senderPlayer launchHeroSkillWithSkillId:senderPlayer.heroSkillId];
            if (!senderPlayer.isMe) [self.table showPlayedCardWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionPlayerUpdateHandCard:
            senderPlayer.updateReason = [self playerUpdateReason:esObj];
            senderPlayer.handCardCount = [self handCardCount:esObj];
            break;
            
        case ActionPlayerUpdateEquipment:
            senderPlayer.updateReason = [self playerUpdateReason:esObj];
            senderPlayer.plusDistance = [esObj intWithKey:kParamPlusDistance];
            senderPlayer.minusDistance = [esObj intWithKey:kParamMinusDistance];
            senderPlayer.attackRange = [esObj intWithKey:kParamAttackRange];
            break;
            
        case ActionPlayerUpdateHero:
            senderPlayer.hpChanged = [esObj intWithKey:kParamHeroHpChanged];
            senderPlayer.spChanged = [esObj intWithKey:kParamHeroSpChanged];
            [senderPlayer updateHeroWithHealthPoint:[esObj intWithKey:kParamHeroHealthPoint] skillPoint:[esObj intWithKey:kParamHeroSkillPoint]];
            break;
            
        case ActionTableFaceDownCardFromDeck:
            [self.table faceDownCardFromDeckWithCount:[self cardCount:esObj]];
            break;
            
        case ActionTableFaceUpTheCard:
            [self.table faceUpTableCardWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionPlayerGetCardFromTable:
            [senderPlayer getCardFromTableWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionChoseCardToGet:
            [senderPlayer getCardFromPlayerWithCardIds:[self cardIdList:esObj] cardCount:[self cardCount:esObj]];
            break;
            
        case ActionChoseCardToGive:
            [senderPlayer giveCardToPlayerWithCardIds:[self cardIdList:esObj] cardCount:[self cardCount:esObj]];
            break;
            
        case ActionChoseCardToRemove:
            [senderPlayer removeCardToTableWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionTableRevealOneCardFromDeck:
            [self.table revealCardFromDeckWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionTableShowAssignedCard:
            if (!senderPlayer.isMe) [self.table showAssignedCardWithCardIds:[self cardIdList:esObj]];
            break;
            
        case ActionPlayerIsDying:
            [senderPlayer askForHelp];
            break;
            
        case ActionPlayerIsDead:
            senderPlayer.isFirstBlood = [esObj boolWithKey:kParamIsFirstBlood];
            [senderPlayer diedAndShowRole:[esObj intWithKey:kParamCardId]];
            break;
            
        case ActionPlayerIsEscaped:
            [senderPlayer escapedFromGame];
            break;
            
        default:
            break;
    }
    
//  Add chat bubble
    id runningNode = (self.gameScene) ? self.gameScene : self.roomScene;
    if (e.message.length > 0) {
        [runningNode showMessage:e.message ofUserName:e.userName];
        [BGChatPopup addHistory:e.message byUser:[self userByUserName:e.userName].nickName];
        [[runningNode chatPopup] reloadTableViewData];
    }
    
//  Add game history(牌局历史记录)
    if (ActionTableShowAssignedCard == action) {     // 分配完明置的牌，记录每个玩家得到的牌
        if (![self cardIdList:esObj]) return;
        NSArray *cardList = [NSArray arrayWithArray:[self cardIdList:esObj]];
        [senderPlayer.targetPlayers enumerateObjectsUsingBlock:^(BGPlayer *player, NSUInteger idx, BOOL *stop) {
            [esObj setIntArray:@[cardList[idx]] forKey:kParamCardIdList];
            [player addGameHistoryWithEsObject:esObj];
        }];
    } else {
        if (esObj) [senderPlayer addGameHistoryWithEsObject:esObj];
    }
}

/*
 * Receive private message event when user rejoin the game
 */
- (void)onPrivateMessageEvent:(EsPrivateMessageEvent *)e
{
    NSLog(@"Private message sender user: %@", e.userName);
    NSLog(@"Receive private message: %@", e.esObject);
    
    EsObject *esObj = e.esObject;
    NSInteger action = [esObj intWithKey:kAction];
    self.gameScene.action = action;
    self.gameScene.state = [esObj intWithKey:kGameState];
    
    BGPlayer *senderPlayer = [self.gameScene playerWithName:e.userName];
    
    switch (action) {
        case ActionPlayerUpdateHero:
            [senderPlayer updateHeroWithHealthPoint:[esObj intWithKey:kParamHeroHealthPoint] skillPoint:[esObj intWithKey:kParamHeroSkillPoint]];
            break;
            
        case ActionPlayerUpdateHandCard:
            senderPlayer.handCardCount = [self handCardCount:esObj];
            break;
            
        case ActionPlayerUpdateEquipment:
            senderPlayer.updateReason = [self playerUpdateReason:esObj];
            senderPlayer.plusDistance = [esObj intWithKey:kParamPlusDistance];
            senderPlayer.minusDistance = [esObj intWithKey:kParamMinusDistance];
            senderPlayer.attackRange = [esObj intWithKey:kParamAttackRange];
            [senderPlayer updateEquipmentWithCardIds:[self equipmentIdList:esObj] reason:UpdateReasonRejoin];
            break;
            
        case ActionPlayerIsDying:
            [senderPlayer askForHelp];
            break;
            
        case ActionPlayerIsDead:
            senderPlayer.handCardCount = 0;
            [senderPlayer updateEquipmentWithCardIds:@[] reason:UpdateReasonDefault];
            [senderPlayer diedAndShowRole:[esObj intWithKey:kParamCardId]];
            break;
            
        case ActionPlayerIsEscaped:
            [senderPlayer escapedFromGame];
            break;
            
        default:
            break;
    }
}

@end
