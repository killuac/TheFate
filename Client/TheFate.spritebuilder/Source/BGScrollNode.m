//
//  BGScrollNode.m
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGScrollNode.h"


@implementation BGScrollNode

- (void)setPosition:(CGPoint)position
{
    [super setPosition:position];
    
//  The scroll view contentOffset is setted when first loading(Transition)
//  So need reset the contentOffset with CGPointZero later
#if __CC_PLATFORM_IOS
    UIScrollView *scrollView = [CCDirector sharedDirector].view.subviews.firstObject;
    scrollView.contentOffset = ccp(-position.x*SCALE_MULTIPLIER, 0.0f);
#endif
}

@end
