//
//  CCTouch+BGTouch.m
//  TheFate
//
//  Created by Killua Liu on 11/5/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCTouch+BGTouch.h"

@implementation CCTouch (BGTouch)

- (CGPoint)glLocationInTouchedView
{
    return [[CCDirector sharedDirector] convertToGL:[self locationInView:self.view]];
}

- (CGPoint)glPreviousLocationInTouchedView
{
    return [[CCDirector sharedDirector] convertToGL:[self previousLocationInView:self.view]];
}

- (CGPoint)offsetPosition
{
    return ccpSub([self glLocationInTouchedView], [self glPreviousLocationInTouchedView]);
}

- (BOOL)isTouchedOnNode:(CCNode *)node
{
    CGPoint touchPos = [node.parent convertToNodeSpace:[self glLocationInTouchedView]];
    return CGRectContainsPoint(node.boundingBox, touchPos);
}

@end
