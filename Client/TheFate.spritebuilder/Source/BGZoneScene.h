//
//  BGZoneScene.h
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "CCNode.h"
#import "BGRoomScene.h"
#import "BGStoreScene.h"
#import "BGAlertPopup.h"
#import "BGRoomFinder.h"
#import "BGRoomCreation.h"
#import "BGGameSetting.h"
#import "BGInformation.h"
#import "BGUserInformation.h"

@interface BGZoneScene : CCNode <BGPopupDelegate> {
    CCButton *_userAvatarButton;
    CCLabelTTF *_nickNameLabel;
    CCLabelTTF *_levelLabel;
    CCLabelTTF *_winRateLabel;
}

@property (nonatomic, strong, readonly) CCEffectNode *effectNode;

- (BGRoomScene *)showRoomScene;
- (BGStoreScene *)showStoreScene;
- (void)showWrongPrompt:(EsObject *)esObj;

- (void)back:(CCButton *)sender;
- (void)zoneSelected:(CCButton *)sender;
- (void)createRoom:(CCButton *)sender;
- (void)findRoom:(CCButton *)sender;
- (void)store:(CCButton *)sender;
- (void)feedback:(CCButton *)sender;
- (void)setting:(CCButton *)sender;

- (void)userAvatarTouched:(CCButton *)sender;

@end
