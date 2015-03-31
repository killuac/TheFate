//
//  BGLoginScene.h
//  PROJECTNAME
//
//  Created by Viktor on 10/10/13.
//  Copyright (c) 2013 Apportable. All rights reserved.
//

#import "CCNode.h"
#import "BGRegisterPopup.h"
#import "BGZoneScene.h"

#define kUserName           @"userName"
#define kPassword           @"password"
#define kIsAutoLogin        @"isAutoLogin"

#define kParamIsRegister    @"isRegister"
#define kParamIPAddress     @"ipAddress"
#define kParamLoginResult   @"loginResult"

typedef NS_ENUM(NSInteger, BGLoginResult) {
    UserNameEmpty,
    UserNameTaken,
    UserNotFound,
    WrongPassword
};

@interface BGLoginScene : CCNode <BGPlatformTextFieldDelegate, BGPopupDelegate> {
    CCTextField *_userNameText;
    CCTextField *_passwordText;
    CCButton *_loginButton;
    BGCheckBox *_autoLoginButton;
}

@property (nonatomic, strong, readonly) CCEffectNode *effectNode;
@property (nonatomic, strong, readonly) CCTextField *userNameText;
@property (nonatomic, strong, readonly) CCTextField *passwordText;
@property (nonatomic, readonly) BOOL isAutoLogin;

- (BGZoneScene *)showZoneScene;
- (void)showWrongPrompt:(EsObject *)esObj;

- (void)login;
- (void)signIn:(CCButton *)sender;
- (void)signUp:(CCButton *)sender;
- (void)autoLoginChecked:(CCButton *)sender;

@end
