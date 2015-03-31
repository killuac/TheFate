//
//  BGFateTask.m
//  TheFate
//
//  Created by Killua Liu on 4/13/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGFateTask.h"
#import "BGGameScene.h"

@implementation BGFateTask {
    BOOL _isLongPressed;
}

- (void)didLoadFromCCB
{
    self.userInteractionEnabled = YES;
}

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    _touchedPos = [touch glLocationInTouchedView];
    _beganTimestamp = touch.timestamp;
    [self scheduleOnce:@selector(showInformation) delay:DURATION_MINIMUM_LONG_PRESS];
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [self unschedule:@selector(showInformation)];
    [[self runningSceneNode] removeChildByName:@"cardInfo"];
}

- (void)touchCancelled:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [[self runningSceneNode] removeChildByName:@"cardInfo"];
}

- (void)showInformation
{
    BGCardInformation *cardInfo = (BGCardInformation *)[CCBReader load:kCcbiCardInformation];
    BGFateCard *card = [BGFateCard cardWithCardId:[[self runningSceneNode] fateCardId]];
    cardInfo.textLabel.string = card.cardDesc;
    cardInfo.scale = 0.9f;
    _touchedPos = ccp(_touchedPos.x, _touchedPos.y*0.9f);  // Don't exceed screen top
    [cardInfo showInformationFromTouch:_touchedPos anchorPoint:ccp(0.5f, 0.0f)];
}

@end
