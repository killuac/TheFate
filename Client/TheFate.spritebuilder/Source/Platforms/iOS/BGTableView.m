//
//  BGTableView.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGTableView.h"
#import "BGRoomScene.h"

@implementation BGTableView

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.showsHorizontalScrollIndicator = NO;
        self.showsVerticalScrollIndicator = YES;
        self.backgroundColor = [UIColor clearColor];
        self.bounces = ([UIDevice currentDevice].systemVersion.doubleValue >= 7.0);
        
        UISwipeGestureRecognizer *swipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(swipeGesture:)];
        [self addGestureRecognizer:swipe];
    }
    return self;
}

- (void)swipeGesture:(UISwipeGestureRecognizer *)swipe
{
    if (UISwipeGestureRecognizerDirectionLeft == swipe.direction) {
        [[[self runningSceneNode] chatPopup] dismiss];
    }
    
    if (UISwipeGestureRecognizerDirectionRight == swipe.direction &&
        [[self runningSceneNode] respondsToSelector:@selector(historyPopup)]) {
        [[[self runningSceneNode] historyPopup] dismiss];
    }
}

- (id)runningSceneNode
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}

@end
