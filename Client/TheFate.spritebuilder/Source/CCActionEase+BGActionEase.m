//
//  CCActionEase+BGActionEase.m
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCActionEase+BGActionEase.h"

@implementation CCActionEase (BGActionEase)

@end


#pragma mark -
#pragma mark Ease Exponential

//
// EaseActionExponentialIn
//
@implementation CCActionEaseExponentialIn
-(void) update: (CCTime) t
{
	[_inner update: (t==0) ? 0 : powf(2, 10 * (t/1 - 1)) - 1 * 0.001f];
}

- (CCActionInterval*) reverse
{
	return [CCActionEaseExponentialOut actionWithAction: [_inner reverse]];
}
@end

//
// EaseActionExponentialOut
//
@implementation CCActionEaseExponentialOut
-(void) update: (CCTime) t
{
	[_inner update: (t==1) ? 1 : (-powf(2, -10 * t/1) + 1)];
}

- (CCActionInterval*) reverse
{
	return [CCActionEaseExponentialIn actionWithAction: [_inner reverse]];
}
@end

//
// EaseActionExponentialInOut
//
@implementation CCActionEaseExponentialInOut
-(void) update: (CCTime) t
{
	t /= 0.5f;
	if (t < 1)
		t = 0.5f * powf(2, 10 * (t - 1));
	else
		t = 0.5f * (-powf(2, -10 * (t -1) ) + 2);
    
	[_inner update:t];
}
@end