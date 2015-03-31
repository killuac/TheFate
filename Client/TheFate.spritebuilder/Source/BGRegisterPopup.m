//
//  BGRegisterPopup.m
//  TheFate
//
//  Created by Killua Liu on 3/26/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRegisterPopup.h"
#import "BGLoginScene.h"

@implementation BGRegisterPopup

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    [self setAllUIViewsHidden:YES];
    
    _userNameText.platformTextField.placeholder = PLACEHOLDER_NEW_USER_NAME;
    _userNameText.platformTextField.keyboardType = BGKeyboardTypeASCIICapable;
    _userNameText.platformTextField.autocorrectionType = BGTextAutocorrectionTypeNo;
    _userNameText.platformTextField.autocapitalizationType = BGTextAutocapitalizationTypeNone;
    _userNameText.delegate = self;
    
    _passwordText.platformTextField.placeholder = PLACEHOLDER_NEW_PASSWORD;
    _passwordText.platformTextField.keyboardType = BGKeyboardTypeASCIICapable;
    _passwordText.platformTextField.autocorrectionType = BGTextAutocorrectionTypeNo;
    _passwordText.platformTextField.autocapitalizationType = BGTextAutocapitalizationTypeNone;
    _passwordText.delegate = self;
    
    _okayButton.enabled = NO;
    
#if __CC_PLATFORM_ANDROID
    _okayButton.enabled = YES;
#endif
}

- (BGLoginScene *)loginSceneNode
{
    return self.runningSceneNode;
}

- (void)okay:(CCButton *)sender
{
    sender.enabled = NO;
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
    
    if (![self userNameIsLegal]) {
        [self showBubbleWithText:TIP_USER_NAME_ILLEGAL inNode:_userNameText]; return;
    }
    
    if (_passwordText.string.length == 0) _passwordText.string = _userNameText.string;
    if (_passwordText.string.length < 6) {
        [self showBubbleWithText:TIP_PASSWORD_LENGTH inNode:_passwordText]; return;
    }
    
    [[self loginSceneNode] addLoading];
    [[BGClient sharedClient] loginWithUserName:_userNameText.string
                                      password:_passwordText.string
                                    isRegister:YES];
}

- (BOOL)userNameIsLegal
{
    NSCharacterSet *illegalCharSet = [[NSCharacterSet alphanumericCharacterSet] invertedSet];
    BOOL isLegal = ([_userNameText.string rangeOfCharacterFromSet:illegalCharSet].location == NSNotFound);
    
    NSString *firstChar = [_userNameText.string substringToIndex:1];
    BOOL firstIsLetter = ([firstChar rangeOfCharacterFromSet:[NSCharacterSet letterCharacterSet]].location != NSNotFound);
    
    return (isLegal && firstIsLetter);
}

- (void)setAllUIViewsHidden:(BOOL)hidden
{
    _userNameText.platformTextField.hidden = _passwordText.platformTextField.hidden = hidden;
}

- (void)showWrongPrompt:(EsObject *)esObj
{
    _okayButton.enabled = (_userNameText.string.length > 0);
    [[self loginSceneNode] removeLoading];
    if ([_userNameText.string isEqual:_passwordText.string]) _passwordText.string = @"";
    
    [self showBubbleWithText:TIP_USER_NAME_TAKEN inNode:_userNameText];
}


#pragma mark - CCTextField delegate
- (void)platformTextFieldDidEndEditing:(CCPlatformTextField *)platformTextField
{
    _okayButton.enabled = (_userNameText.string.length > 0);
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    CCTextField *textFiled = (CCTextField *)platformTextField.delegate;
    if ([textFiled.name isEqualToString:_userNameText.name]) {
        [_passwordText.platformTextField becomeFirstResponder];
    } else {
        [self okay:_okayButton];
    }
}

@end
