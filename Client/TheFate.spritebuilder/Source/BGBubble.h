//
//  BGBubble.h
//  TheFate
//
//  Created by Killua Liu on 4/4/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGBubble : CCSprite {
    CCLabelTTF *_messageLabel;
}

- (void)setMessage:(NSString *)message;
- (void)showInNode:(CCNode *)node;

@end
