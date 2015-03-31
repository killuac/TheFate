//
//  BGCardInformation.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardInformation.h"
#import "BGGameScene.h"

@implementation BGCardInformation

- (CGSize)bgSize
{
    CGFloat width = _background.contentSize.width;
    CGFloat height = _background.contentSize.height;
    // return CGSizeMake(width*_background.scaleX, height*_background.scaleY);  // Doesn't work
    return CGSizeMake(width/_textLabel.scaleX, height/_textLabel.scaleY);
}

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

- (void)showInformationFromTouch:(CGPoint)touchPos
{
//  CardInformation background node can't exceed screen boundary, need set correct anchor point.
    CGFloat xAnchor = (touchPos.x+self.bgSize.width > SCREEN_WIDTH) ? 1.0f : 0.0f;
    CGFloat yAnchor = (touchPos.y+self.bgSize.height > SCREEN_HEIGHT) ? 1.0f : 0.0f;
    
    [self showInformationFromTouch:touchPos anchorPoint:ccp(xAnchor, yAnchor)];
}

- (void)showInformationFromTouch:(CGPoint)touchPos anchorPoint:(CGPoint)anchor
{
    [self.runningSceneNode removeChildByName:@"cardInfo"];
    
    _background.anchorPoint = anchor;
    self.position = touchPos;
    [self.runningSceneNode addChild:self z:400 name:@"cardInfo"];
}

@end
