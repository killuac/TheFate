//
//  BGBubble.m
//  TheFate
//
//  Created by Killua Liu on 4/4/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGBubble.h"
#import "BGConstant.h"

@implementation BGBubble

- (void)setMessage:(NSString *)message
{
    _messageLabel.string = message;
    
    [self runFadeOutWithDuration:DELAY_BUBBLE_DISMISS block:^{
        [self removeFromParent];
    }];
}

- (void)showInNode:(CCNode *)node
{
    CCNode *bubble = [node getChildByName:@"bubble" recursively:NO];
    [bubble removeFromParent];
    
    [node addChild:self z:10 name:@"bubble"];
}

@end
