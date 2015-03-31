//
//  BGAssigningPopup.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGAssigningPopup.h"
#import "BGConstant.h"
#import "BGCardButton.h"
#import "BGCardInformation.h"

@implementation BGAssigningPopup {
    BOOL _isPanGesture;
    CCTouch *_touch;
    CCNode *_pannedNode;
    CGPoint _pannedNodePos;
    NSInteger _pannedNodeZOrder;    // Determine order of child nodes in card Array
}

- (void)didLoadFromCCB
{
    self.userInteractionEnabled = YES;
    self.multipleTouchEnabled = NO;
}

- (NSArray *)cardButtons
{
    NSMutableArray *cardButtons = [NSMutableArray array];
    for (CCNode *node in self.children) {
        if ([self isCardButtonOfNode:node])
            [cardButtons addObject:node];
    }
    return cardButtons;
}

- (BOOL)isCardButtonOfNode:(CCNode *)node
{
    return ([node isKindOfClass:[BGCardButton class]] && node.name.length > 0);
}

- (NSInteger)maximumZOrder
{
    NSInteger zOrder = 0;
    for (CCNode *node in self.children) {
        zOrder = MAX(zOrder, node.zOrder);
    }
    return zOrder;
}

- (void)setOtherCardsWithColor:(CCColor *)color
{
    for (CCNode *cardNode in self.children) {
        if ([self isCardButtonOfNode:cardNode] && ![cardNode.name isEqual:_pannedNode.name]) {
            [cardNode setColorWith:color isRecursive:YES];
        }
    }
}

- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    _isPanGesture = NO;
    _touch = touch;
    [self scheduleOnce:@selector(panBegan) delay:DURATION_MINIMUM_PAN_PRESS];
}

- (void)touchMoved:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    if (_isPanGesture) {
        _pannedNode.position = ccpAdd(_pannedNode.position, [touch offsetPosition]);
    }
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    [self unschedule:@selector(panBegan)];
    
    CGPoint touchPos = [self convertToNodeSpace:[touch glLocationInTouchedView]];
    CCNode *targetCard = [self getCardByTouchedPosition:touchPos];
    
    if (!_isPanGesture && targetCard && touch.tapCount > 0) {
        [self showInformationForCardNode:targetCard];
        return;
    }
    
    if (!_isPanGesture) return;
    
    [self setOtherCardsWithColor:NORMAL_COLOR];
    if (targetCard) {
        _pannedNode.zOrder = targetCard.zOrder;
        targetCard.zOrder = _pannedNodeZOrder;
    }
    
    [_pannedNode runEaseMoveScaleWithDuration:DURATION_PANNED_CARD_MOVE
                                     position:(targetCard) ? targetCard.position : _pannedNodePos
                                        scale:CARD_DEFAULT_SCALE
                                        block:nil];
    
    if (!targetCard) return;
    [targetCard runEaseMoveScaleWithDuration:DURATION_PANNED_CARD_MOVE
                                    position:_pannedNodePos
                                       scale:CARD_DEFAULT_SCALE
                                       block:nil];
}

- (CCNode *)getCardByTouchedPosition:(CGPoint)touchPos
{
    for (CCNode *cardNode in self.children) {
        if ([self isCardButtonOfNode:cardNode] && CGRectContainsPoint(cardNode.boundingBox, touchPos)) {
            return cardNode;
        }
    }
    return nil;
}

- (void)panBegan
{
    for (CCNode *cardNode in self.children) {
        if ([self isCardButtonOfNode:cardNode] && [_touch isTouchedOnNode:cardNode]) {
            _isPanGesture = YES;
            _pannedNode = cardNode;
            _pannedNodePos = cardNode.position;
            _pannedNodeZOrder = cardNode.zOrder;
            _pannedNode.zOrder = [self maximumZOrder]+1;    // Make the panned card at the topmost
            [self setOtherCardsWithColor:DISABLED_COLOR];
            [cardNode runScaleWithDuration:DURATION_SELECTED_CARD_SCALE scale:CARD_SCALE_UP block:nil];
            break;
        }
    }
    
    if (!_isPanGesture) [[CCDirector sharedDirector].responderManager discardCurrentEvent];
}

- (void)showInformationForCardNode:(CCNode *)cardNode
{
    BGCardInformation *cardInfo = (BGCardInformation *)[CCBReader load:kCcbiCardInformation];
    BGPlayingCard *card = [BGPlayingCard cardWithCardId:cardNode.name.integerValue];
    cardInfo.textLabel.string = card.cardDesc;
    [cardInfo showInformationFromTouch:[_touch glLocationInTouchedView]];
}

@end
