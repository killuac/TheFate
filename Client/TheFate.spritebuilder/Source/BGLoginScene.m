//
//  BGLoginScene.m
//  PROJECTNAME
//
//  Created by Viktor on 10/10/13.
//  Copyright (c) 2013 Apportable. All rights reserved.
//

#import "BGLoginScene.h"
#import "BGClient.h"

@implementation BGLoginScene {
    BGPopup *_popup;
}

- (void)didLoadFromCCB
{
    _userNameText.platformTextField.placeholder = PLACEHOLDER_USER_NAME;
    _userNameText.platformTextField.textColor = [CCColor lightGrayColor];
    _userNameText.platformTextField.autocorrectionType = BGTextAutocorrectionTypeNo;
    _userNameText.platformTextField.autocapitalizationType = BGTextAutocapitalizationTypeNone;
    _userNameText.delegate = self;
    
    _passwordText.platformTextField.placeholder = PLACEHOLDER_PASSWORD;
    _passwordText.platformTextField.textColor = [CCColor lightGrayColor];
    _passwordText.platformTextField.secureTextEntry = YES;
    _passwordText.delegate = self;
    
    [self loadAccountInfo];
    
    _loginButton.enabled = [self isLoginButtonEnabled];
    
#if __CC_PLATFORM_ANDROID
    _loginButton.enabled = YES;
#endif
}

- (BOOL)isLoginButtonEnabled
{
    return (_userNameText.string.length > 0 && _passwordText.string.length > 0);
}

- (BOOL)isAutoLogin
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:kIsAutoLogin];
}

- (void)loadAccountInfo
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    _userNameText.string = [userDefaults stringForKey:kUserName];
    _passwordText.string = [userDefaults stringForKey:kPassword];
    _autoLoginButton.selected = [userDefaults boolForKey:kIsAutoLogin];
}

- (void)saveAccountInfo
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:_userNameText.string forKey:kUserName];
    [userDefaults setObject:_passwordText.string forKey:kPassword];
    [userDefaults setBool:_autoLoginButton.selected forKey:kIsAutoLogin];
    [userDefaults synchronize];
}

#pragma mark - Button selector
- (void)signIn:(CCButton *)sender
{
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
    [self login];
}

- (void)login
{
    [self addLoadingMaskNode];
    [[BGClient sharedClient] loginWithUserName:_userNameText.string
                                      password:_passwordText.string
                                    isRegister:NO];
}

- (void)signUp:(CCButton *)sender
{
    [self addPopupMaskNode];
    [self setNodeVisible:NO];
    
    _popup = (BGPopup *)[CCBReader load:kCcbiRegisterPopup];
    [_popup show];
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (void)autoLoginChecked:(CCButton *)sender
{
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

/*
 * Show zone scene after receive login successful response
 */
- (BGZoneScene *)showZoneScene
{
    [self saveAccountInfo];     // Login successfull
    return [BGTransitionManager transitSceneToLeft:kCcbiZoneScene];
}

- (void)setNodeVisible:(BOOL)isVisible
{
    _userNameText.visible = _passwordText.visible = isVisible;
    _autoLoginButton.visible = isVisible;
}

- (void)showWrongPrompt:(EsObject *)esObj
{
    [self removeLoadingMaskNode];
    
    BGBubble *bubble = (BGBubble *)[CCBReader load:kCcbiBubble];
    bubble.positionType = CCPositionTypeNormalized;
    bubble.position = ccp(0.97f, 0.95f);
    
    BGLoginResult result = [esObj intWithKey:kParamLoginResult];
    if (UserNotFound == result) {
        [bubble setMessage:TIP_USER_NOT_FOUND];
        [bubble showInNode:_userNameText];
    }
    else if (WrongPassword == result) {
        [bubble setMessage:TIP_WRONG_PASSWORD];
        [bubble showInNode:_passwordText];
    }
    else if (UserNameTaken == result) {
        [_popup showWrongPrompt:esObj];
    }
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidEndEditing:(CCPlatformTextField *)platformTextField
{
    _loginButton.enabled = [self isLoginButtonEnabled];
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    CCTextField *textFiled = (CCTextField *)platformTextField.delegate;
    if ([textFiled.name isEqualToString:_userNameText.name]) {
        [_passwordText.platformTextField becomeFirstResponder];
    } else {
        [self signIn:_loginButton];
    }
}

#pragma mark - Popup delegate
- (void)didDismissPopup:(BGPopup *)popup
{
    [_effectNode clearEffect];
    [self removePopupMaskNode];
    [self setNodeVisible:YES];
}

@end
