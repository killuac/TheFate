//
//  BGInformation.h
//  TheFate
//
//  Created by Killua Liu on 4/23/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGInformation : CCNode {
    CCLabelTTF *_messageLabel;
}

- (void)setMessage:(NSString *)message;
- (void)show;
- (void)showInNode:(CCNode *)node;

@end
