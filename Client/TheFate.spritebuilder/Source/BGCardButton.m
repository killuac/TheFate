//
//  BGCardButton.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardButton.h"
#import "BGCardInformation.h"
#import "BGConstant.h"

@implementation BGCardButton

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

- (void)setSelected:(BOOL)selected
{
    _selectionHalo.visible = selected;
    [super setSelected:selected];
}

- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    _touchedPos = [touch glLocationInTouchedView];
    _beganTimestamp = touch.timestamp;
    [self scheduleOnce:@selector(showInformation) delay:DURATION_MINIMUM_LONG_PRESS];
    [super touchBegan:touch withEvent:event];
}

- (void)touchMoved:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [super touchMoved:touch withEvent:event];
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [self unschedule:@selector(showInformation)];
    [[self runningSceneNode] removeChildByName:@"cardInfo"];
    
    _isLongPressed = (touch.timestamp-_beganTimestamp >= DURATION_MINIMUM_LONG_PRESS);
    
    if (_isLongPressed) {
        [super touchCancelled:touch withEvent:event];
    } else {
        [super touchEnded:touch withEvent:event];
    }
}

- (void)touchCancelled:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [super touchCancelled:touch withEvent:event];
}

- (void)showInformation
{
    BGCardInformation *cardInfo = (BGCardInformation *)[CCBReader load:kCcbiCardInformation];
    BGPlayingCard *card = [BGPlayingCard cardWithCardId:self.name.integerValue];
    cardInfo.textLabel.string = card.cardDesc;
    [cardInfo showInformationFromTouch:_touchedPos];
}

@end
