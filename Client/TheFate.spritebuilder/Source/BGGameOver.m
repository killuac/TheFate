//
//  BGGameOver.m
//  TheFate
//
//  Created by Killua Liu on 3/28/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGGameOver.h"
#import "BGRoomScene.h"

@implementation BGGameOver

- (void)showInNode:(id)parentNode
{
    [super showInNode:parentNode];    // Back from game scene that is running scene, so need call showInNode.
    
//  Victory players' results
    CCTableView *vicTableView = [CCTableView node];
    vicTableView.userInteractionEnabled = NO;
    vicTableView.anchorPoint = ccp(0, 1);
    vicTableView.positionType = CCPositionTypeNormalized;
    vicTableView.position = ccp(0.19f, _victoryFrame.position.y);
    [_rootNode addChild:vicTableView];
    vicTableView.dataSource = [BGResultTable resultTableWithGameResults:[parentNode victoryResults]];
    
    NSUInteger rowCount = [parentNode victoryResults].count;
    _victoryFrame.scaleY *= rowCount;
    _victoryMark.scaleY /= rowCount;
    
//  Failure players' results(Calculate position according to victory talbe's row count)
    CCTableView *failTableView = [CCTableView node];
    failTableView.userInteractionEnabled = NO;
    failTableView.anchorPoint = ccp(0, 1);
    failTableView.positionType = CCPositionTypeNormalized;
    CGFloat percentage = (ROW_HEIGHT/_rootNode.contentSizeInPoints.height) * (rowCount+1);
    failTableView.position = ccpSub(vicTableView.position, ccp(0.0f, percentage));
    [_rootNode addChild:failTableView];
    failTableView.dataSource = [BGResultTable resultTableWithGameResults:[parentNode failureResults]];
    
    rowCount = [parentNode failureResults].count;
    _failureFrame.scaleY *= rowCount;
    _failureMark.scaleY /= rowCount;
}

@end
