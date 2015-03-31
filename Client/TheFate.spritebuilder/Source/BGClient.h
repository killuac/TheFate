//
//  BGClient.h
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//
//  This class will handle all interactions including request/response/event between client and elctroserver.

#import <Foundation/Foundation.h>
#import "BGUtil.h"
#import "BGMacro.h"
#import "BGConstant.h"
#import "BGFileConstant.h"
#import "BGVarConstant.h"
#import "BGPluginConstant.h"
#import "BGTransitionManager.h"

@interface BGClient : NSObject

@property (nonatomic, strong, readonly) ElectroServer *es;
@property (nonatomic, strong, readonly) EsUser *esUser;
@property (nonatomic, strong, readonly) EsRoom *esRoom;
@property (nonatomic, copy) NSString *gameType;

- (EsUser *)userByUserName:(NSString *)userName;

+ (BGClient *)sharedClient;

- (void)conntectServer;
- (void)loginWithUserName:(NSString *)userName password:(NSString *)password isRegister:(BOOL)isRegister;
- (void)sendLogoutRequest;

- (void)sendShowMerchandisesRequest;
- (void)sendShowPickingHerosRequest;
- (void)sendBuyGoldCoinRequestWithReceipt:(NSString *)receiptStr;
- (void)sendBuyHeroCardRequest;
- (void)sendBuyVIPCardRequest;

- (void)sendQuickJoinGameRequest;
- (void)sendCreateRoomRequestWithPassword:(NSString *)password gameDetails:(EsObject *)gameDetails;
- (void)sendLeaveRoomRequest;
- (void)sendUpdateRoomDetailsRequest;
- (void)sendUpdateRoomVariableRequest;
- (void)sendUpdateUserInfoVariableRequest;
- (void)sendUpdateUserStatusVariableRequest;
- (void)sendFindRoomRequestWithRoomNumber:(NSString *)roomNumber password:(NSString *)password;
- (void)sendPickOneHeroRoomRequest;
- (void)sendKickUserRequest:(NSString *)userName;
- (void)sendFeedbackRequestWithTitle:(NSString *)title content:(NSString *)content;

- (void)sendStartGameRequest;
- (void)sendStartRoundRequest;
- (void)sendExitGameRequest;

- (void)sendSelectHeroIdRequest;
- (void)sendUseHandCardRequest;
- (void)sendUseHeroSkillRequest;
- (void)sendUseEquipmentRequest;
- (void)sendCancelHeroSkillRequest;
- (void)sendCancelEquipmentRequest;
- (void)sendStartDiscardRequest;
- (void)sendOkayToDiscardRequest;

- (void)sendChoseCardToCompareRequest;
- (void)sendChoseCardToUseRequest;
- (void)sendChoseCardToGetRequest;
- (void)sendChoseCardToGiveRequest;
- (void)sendChoseCardToRemoveRequest;
- (void)sendChoseCardToDropRequest;
- (void)sendChoseColorRequest;
- (void)sendChoseSuitsRequest;
- (void)sendAsignedCardRequest;
- (void)sendOkayRequest;
- (void)sendCancelRequest;
- (void)sendChoseTargetPlayerRequest;
- (void)choseViewPlayerRoleRequest;

- (void)sendPublicMessage:(NSString *)message;

@end
