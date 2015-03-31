//
//  BGRoomSetting.m
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoomSetting.h"
#import "BGRoomScene.h"

@implementation BGRoomSetting {
    EsRoom *_esRoom;
}

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    
    _esRoom = [BGClient sharedClient].esRoom;
    _playTime = _esRoom.playTime;
    _isNoChatting = _esRoom.isNoChatting;
    
    _noChattingButton.selected = _isNoChatting;
    _passwordText.string = _esRoom.roomPassword;
    _passwordText.delegate = self;
    
    for (CCButton *button in _playTimeButtonBox.children) {
        if (button.name.integerValue == _playTime)
            button.selected = YES;
    }
}

- (void)okay:(CCButton *)sender
{
    [self dismiss];
    
    _esRoom.playTime = _playTime;
    _esRoom.isNoChatting = _isNoChatting;
    _esRoom.roomPassword = _passwordText.string;
    [self.runningSceneNode updateRoomDisplay];
    
    [[BGClient sharedClient] sendUpdateRoomDetailsRequest];
    [[BGClient sharedClient] sendUpdateRoomVariableRequest];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    [self okay:_okayButton];
}

@end
