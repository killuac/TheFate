//
//  BGRegisterPopup.h
//  TheFate
//
//  Created by Killua Liu on 3/26/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@interface BGRegisterPopup : BGPopup {
    CCTextField *_userNameText;
    CCTextField *_passwordText;
}

@property (nonatomic, strong, readonly) CCTextField *userNameText;
@property (nonatomic, strong, readonly) CCTextField *passwordText;

@end
