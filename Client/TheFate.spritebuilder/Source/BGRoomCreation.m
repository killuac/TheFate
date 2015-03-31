//
//  BGRoomCreation.m
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoomCreation.h"
#import "BGZoneScene.h"

@implementation BGRoomCreation

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    _passwordText.platformTextField.hidden = YES;
    _passwordText.platformTextField.secureTextEntry = YES;
    _passwordText.platformTextField.placeholder = PLACEHOLDER_ROOM_PASSWORD;
    _passwordText.delegate = self;
    
    if (_defaultRoomTypeButton) {
        _defaultRoomTypeButton.selected = YES;
        self.capacity = _defaultRoomTypeButton.name.integerValue;
    }
    
    _defaultPlayTimeButton.selected = YES;
    self.playTime = _defaultPlayTimeButton.name.integerValue;
}

- (void)okay:(CCButton *)sender
{
    sender.enabled = NO;
    [[self runningSceneNode] addLoading];
    
    EsObject *gameDetails = [[EsObject alloc] init];
    [gameDetails setInt:(int32_t)_playTime forKey:kParamPlayTime];
    [gameDetails setBool:_isNoChatting forKey:kParamIsNoChatting];
    [gameDetails setString:_passwordText.string forKey:kParamRoomPassword];
    [[BGClient sharedClient] sendCreateRoomRequestWithPassword:_passwordText.string gameDetails:gameDetails];
    
    [super okay:sender];
}

- (void)setCapacity:(NSUInteger)capacity
{
    _capacity = capacity;
    NSUInteger userLevel = [BGClient sharedClient].esUser.level;
    
    switch (capacity) {
        case 2:
            [BGClient sharedClient].gameType = (userLevel < 5) ? GAMETYPE_NEWBIE : GAMETYPE_VERSUS;
            break;
            
        case 6:
            [BGClient sharedClient].gameType = (userLevel < 10) ? GAMETYPE_JUNIOR_SIX : GAMETYPE_SENIOR_SIX;
            break;
            
        default:
            [BGClient sharedClient].gameType = (userLevel < 10) ? GAMETYPE_JUNIOR_EIGHT : GAMETYPE_SENIOR_EIGHT;
            break;
    }
}

- (void)setPlayTime:(NSUInteger)playTime
{
    _playTime = playTime;
}

- (BGZoneScene *)zoneScene
{
    return self.runningSceneNode;
}

#pragma mark - Button selector
- (void)capacityChecked:(CCButton *)sender
{
    self.capacity = sender.name.intValue;
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

- (void)playTimeChecked:(CCButton *)sender
{
    self.playTime = sender.name.intValue;
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

- (void)noChattingChecked:(CCButton *)sender
{
    _isNoChatting = sender.selected;
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    [self okay:_okayButton];
}

@end
