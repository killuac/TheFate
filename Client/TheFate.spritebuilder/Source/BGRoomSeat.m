//
//  BGRoomSeat.m
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoomSeat.h"
#import "BGClient.h"

@implementation BGRoomSeat

- (void)didLoadFromCCB
{
    self.userInteractionEnabled = YES;
    [self setUserVisible:NO];
}

- (void)setUserAvatar:(CCSprite *)userAvatar
{
    if (!userAvatar) {
        [_userAvatar useGrayscaleShaderByRecursively:NO];
        return;
    }
    
    for (CCNode *node in self.children) {
        node.zOrder = 10;
    }
    
    userAvatar.positionType = _userAvatar.positionType;
    userAvatar.position = _userAvatar.position;
    userAvatar.scale = _userAvatar.scale;
    [self addChild:userAvatar z:0];
    
    [_userAvatar removeFromParent];
    _userAvatar = userAvatar;
}

- (void)setIsReady:(BOOL)isReady
{
    _readyMark.visible = isReady;
    [self.userObject runAnimationsForSequenceNamed:@"ReadyMark"];
}

- (void)setIsRoomOwner:(BOOL)isRoomOwner
{
    if (!_roomOwnerMark.visible) {
        [self.userObject runAnimationsForSequenceNamed:@"RoomOwnerMark"];
    }
    _readyMark.visible = NO;
    _roomOwnerMark.visible = isRoomOwner;
}

- (void)setUserVisible:(BOOL)isVisible
{
    _nickNameLabel.string = _levelLabel.string = _winRateLabel.string = @"";
    _userAvatar.visible = isVisible;
    _readyMark.visible = NO;
    _roomOwnerMark.visible = NO;
    _vipMark.visible = NO;
    _pickedHeroMark.visible = NO;
    
    for (CCNode *node in self.children) {
        if ([node isKindOfClass:[CCLabelTTF class]])
            node.visible = isVisible;
    }
}

- (void)showPickedHeroMark
{
    _pickedHeroMark.visible = YES;
    [self.userObject runAnimationsForSequenceNamed:@"PickedHeroMark"];
}

#pragma mark - Touch event
- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
//  For trigger touchEnded
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    EsUser *selfUser = [BGClient sharedClient].esUser;
    if (selfUser.isRoomOwner && !_user.isMe && touch.tapCount == 2) {
        [[BGClient sharedClient] sendKickUserRequest:_user.userName];
    }
}

@end
