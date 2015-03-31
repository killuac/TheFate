//
//  BGRoomFinder.m
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoomFinder.h"
#import "BGZoneScene.h"

@implementation BGRoomFinder

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    _roomNoText.platformTextField.hidden = YES;
    _roomNoText.platformTextField.placeholder = PLACEHOLDER_ROOM_NUMBER;
    _roomNoText.platformTextField.keyboardType = BGKeyboardTypeNumberPad;
    _roomNoText.delegate = self;
    
    _passwordText.platformTextField.hidden = YES;
    _passwordText.platformTextField.placeholder = PLACEHOLDER_FIND_ROOM_PWD;
    _passwordText.platformTextField.secureTextEntry = YES;
    _passwordText.delegate = self;
    
    _okayButton.enabled = NO;
}

- (void)okay:(CCButton *)sender
{
    sender.enabled = NO;
    [[self runningSceneNode] addLoading];
    
    [[BGClient sharedClient] sendFindRoomRequestWithRoomNumber:_roomNoText.string password:_passwordText.string];
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)showWrongPrompt:(EsObject *)esObj
{
    _okayButton.enabled = (_roomNoText.string.length > 0);
    [[self runningSceneNode] removeLoading];
    
    if (RoomFindResultNotFound == [esObj intWithKey:kRoomFindResult]) {
        [self showBubbleWithText:TIP_ROOM_NOT_FOUND inNode:_roomNoText];
    } else {
        if (_passwordText.string.length > 0) {
            [self showBubbleWithText:TIP_WRONG_ROOM_PASSWORD inNode:_passwordText];
        } else {
            [self showBubbleWithText:TIP_INPUT_PASSWORD inNode:_passwordText];
        }
    }
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidEndEditing:(CCPlatformTextField *)platformTextField
{
    CCTextField *textFiled = (CCTextField *)platformTextField.delegate;
    if (![textFiled.name isEqualToString:_roomNoText.name]) return;
    
    NSCharacterSet *charactersToRemove = [[NSCharacterSet decimalDigitCharacterSet] invertedSet];
    platformTextField.string = [[platformTextField.string componentsSeparatedByCharactersInSet:charactersToRemove] componentsJoinedByString:@""];
    _okayButton.enabled = (_roomNoText.string.length > 0);
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    CCTextField *textFiled = (CCTextField *)platformTextField.delegate;
    if ([textFiled.name isEqualToString:_roomNoText.name]) {
        [_passwordText.platformTextField becomeFirstResponder];
    } else {
        [self okay:_okayButton];
    }
}

@end
