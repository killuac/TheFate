//
//  BGInformation.m
//  TheFate
//
//  Created by Killua Liu on 4/23/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGInformation.h"
#import "BGConstant.h"

@implementation BGInformation

- (void)setMessage:(NSString *)message
{
    _messageLabel.string = message;
    
    [self runFadeOutWithDuration:DELAY_INFORMATION_DISMISS block:^{
        [self removeFromParent];
    }];
}

- (void)show
{
    id runningSceneNode = [CCDirector sharedDirector].runningScene.children.lastObject;
    [self showInNode:runningSceneNode];
}

- (void)showInNode:(CCNode *)node
{
    [node removeChildByName:@"info"];
    self.position = SCREEN_CENTER;
    [node addChild:self z:1000 name:@"info"];
}

@end
