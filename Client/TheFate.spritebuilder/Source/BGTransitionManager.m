//
//  BGTransitionManager.m
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGTransitionManager.h"
#import "BGConstant.h"

@implementation BGTransitionManager

+ (id)transitSceneToLeft:(NSString *)ccbFileName
{
    return [self transitScene:ccbFileName withDirection:CCTransitionDirectionLeft];
}

+ (id)transitSceneToRight:(NSString *)ccbFileName
{
    return [self transitScene:ccbFileName withDirection:CCTransitionDirectionRight];
}

+ (id)transitSceneToUp:(NSString *)ccbFileName
{
    return [self transitScene:ccbFileName withDirection:CCTransitionDirectionUp];
}

+ (id)transitSceneToDown:(NSString *)ccbFileName
{
    return [self transitScene:ccbFileName withDirection:CCTransitionDirectionDown];
}

+ (id)transitScene:(NSString *)ccbFileName withDirection:(CCTransitionDirection)direction
{
    CCScene *scene = [CCBReader loadAsScene:ccbFileName];
    CCTransition *transition = [CCTransition transitionPushWithDirection:direction duration:DURATION_SCENE_TRANSITION];
    [[CCDirector sharedDirector] replaceScene:scene withTransition:transition];
    return scene.children.lastObject;
}

@end
