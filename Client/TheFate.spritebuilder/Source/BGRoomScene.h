//
//  BGRoomScene.h
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "CCNode.h"
#import "BGRoomSeat.h"
#import "BGRoomSetting.h"
#import "BGStoreScene.h"
#import "BGGameScene.h"
#import "BGGameOver.h"

@interface BGRoomScene : CCNode <BGPopupDelegate> {
    CCLabelTTF *_roomNoLabel;
    CCLabelTTF *_playTimeLabel;
    CCSprite *_noChattingMark;
    CCButton *_readyStartButton;
    CCButton *_cancelReadyButton;
    CCButton *_startGameButton;
    CCButton *_chatButton;
    CCButton *_roomSettingButton;
}

@property (nonatomic, strong, readonly) CCEffectNode *effectNode;
@property (nonatomic, strong, readonly) CCLabelTTF *roomNoLabel;
@property (nonatomic, readonly) BOOL isChangeTable;
@property (nonatomic, strong, readonly) BGChatPopup *chatPopup;
@property (nonatomic, strong, readonly) BGGameOver *gameOverPopup;

@property (nonatomic, strong) NSArray *victoryResults;
@property (nonatomic, strong) NSArray *failureResults;

- (BGGameScene *)showGameScene;
- (void)showGameResults;
- (void)backToZoneScene;
- (BGStoreScene *)showStoreWithAvailableHeroIds:(NSArray *)heroIds;

- (void)changeRoom;
- (void)rejoinRoom;

- (void)updateRoomDisplay;
- (void)showPickedHeroMark;
- (void)addUser:(EsUser *)user;
- (void)updateUser:(EsUser *)user;
- (void)removeUserByUserName:(NSString *)userName;

- (void)showMessage:(NSString *)message ofUserName:(NSString *)userName;

- (void)back:(CCButton *)sender;
- (void)readyStart:(CCButton *)sender;
- (void)cancelReady:(CCButton *)sender;
- (void)startGame:(CCButton *)sender;
- (void)changeTable:(CCButton *)sender;
- (void)chat:(CCButton *)sender;
- (void)roomSetting:(CCButton *)sender;
- (void)pickHero:(CCButton *)sender;

@end
