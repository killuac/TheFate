//
//  BGZoneScene.m
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGZoneScene.h"
#import "BGClient.h"
#import "BGPlayer.h"

@implementation BGZoneScene {
    EsUser *_user;
    BGPopup *_popup;
}

- (void)didLoadFromCCB
{    
    _user = [BGClient sharedClient].esUser;
    _nickNameLabel.string = _user.nickName;
    _levelLabel.string = @(_user.level).stringValue;
    _winRateLabel.string = [NSString stringWithFormat:@"%.1f%%", _user.winRate];
    [self setUserAvatar:_user.avatar];
}

- (void)setUserAvatar:(NSData *)avatarData
{
    if (avatarData && avatarData.length > 0) {
        [_userAvatarButton setBackgroundSpriteFrame:nil forState:CCControlStateNormal];
        CCSprite *sprite = [CCSprite spriteWithImageData:avatarData key:_nickNameLabel.string];
        sprite.positionType = _userAvatarButton.positionType;
        sprite.position = _userAvatarButton.position;
        [_userAvatarButton.parent addChild:sprite];
    }
}

- (BGRoomScene *)showRoomScene
{
    NSString *fileName = FILE_FULL_NAME(kCcbiRoomScene, [BGClient sharedClient].esRoom.capacity, kFileTypeCCBI);
    return [BGTransitionManager transitSceneToLeft:fileName];
}

- (BGStoreScene *)showStoreScene
{
    BGStoreScene *storeScene = [BGTransitionManager transitSceneToUp:kCcbiStoreScene];
    [storeScene loadData];
    return storeScene;
}

- (void)showWrongPrompt:(EsObject *)esObj
{
    [_popup showWrongPrompt:esObj];
}

#pragma mark - Button selector
- (void)back:(CCButton *)sender
{
    [self addPopupMaskNode];
    [BGUtil showAlertPopupWithMessage:ALERT_LOGOUT];
    
    [[BGAudioManager sharedAudioManager] playGameExit];
}

- (void)zoneSelected:(CCButton *)sender
{
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
    
    if ([sender.name isEqual:GAMETYPE_SENIOR_SIX]) {
        if (_user.level < 10) {
            NSString *message = [BGUtil textWith:INFO_LEVEL_NOT_ENOUGH parameter:@(10).stringValue];
            [BGUtil showInformationWithMessage:message]; return;
        }
    }
    
    [self addLoadingMaskNode];
    [BGClient sharedClient].gameType = sender.name;
    [[BGClient sharedClient] sendQuickJoinGameRequest];
}

- (void)createRoom:(CCButton *)sender
{
    [self addPopupMaskNode];
    _popup = (BGPopup *)[CCBReader load:kCcbiRoomCreation];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)findRoom:(CCButton *)sender
{
    [self addPopupMaskNode];
    _popup = (BGPopup *)[CCBReader load:kCcbiRoomFinder];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)store:(CCButton *)sender
{
    [self addLoadingMaskNode];
    [[BGClient sharedClient] sendShowMerchandisesRequest];
    
    [[BGAudioManager sharedAudioManager] playStoreClick];
}

- (void)feedback:(CCButton *)sender
{
    [self addPopupMaskNode];
    _popup = (BGPopup *)[CCBReader load:kCcbiFeedback];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)setting:(CCButton *)sender
{
    [self addPopupMaskNode];
    _popup = (BGPopup *)[CCBReader load:kCcbiGameSetting];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)userAvatarTouched:(CCButton *)sender
{
    [self addPopupMaskNode];
    
    self.userInteractionEnabled = YES;
    _popup = (BGPopup *)[CCBReader load:kCcbiUserInformation];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

#pragma mark - Touch event
- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [_popup touchBegan:touch withEvent:event];
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [_popup touchEnded:touch withEvent:event];
}

#pragma mark - Popup delegate
- (void)popupOkay:(BGPopup *)popup
{
    if ([popup isKindOfClass:[BGAlertPopup class]]) {
        [[BGClient sharedClient] sendLogoutRequest];
        [BGTransitionManager transitSceneToRight:kCcbiLoginScene];
    }
    else if ([_popup isKindOfClass:[BGUserInformation class]]) {
        if (((BGUserInformation *)_popup).isDataChanged) {
            _nickNameLabel.string = [BGClient sharedClient].esUser.nickName;
            [self setUserAvatar:[BGClient sharedClient].esUser.avatar];
            [[BGClient sharedClient] sendUpdateUserInfoVariableRequest];
        }
    }
}

- (void)didDismissPopup:(BGPopup *)popup
{
    _popup = nil;
    [self removePopupMaskNode];
    [_effectNode clearEffect];
}

@end
