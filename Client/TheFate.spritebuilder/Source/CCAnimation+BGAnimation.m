//
//  CCAnimation+BGAnimation.m
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCAnimation+BGAnimation.h"

@implementation CCAnimation (BGAnimation)

+ (CCAnimation*)animationWithFrameName:(NSString*)frameName frameCount:(NSUInteger)frameCount delay:(float)delay
{
	CCSpriteFrameCache* frameCache = [CCSpriteFrameCache sharedSpriteFrameCache];
	NSMutableArray* frames = [NSMutableArray arrayWithCapacity:frameCount];
	for (int i = 0; i < frameCount; i++)
	{
		NSString* file = [NSString stringWithFormat:@"%@%tu.png", frameName, i];
		CCSpriteFrame* frame = [frameCache spriteFrameByName:file];
		[frames addObject:frame];
	}
	return [CCAnimation animationWithSpriteFrames:frames delay:delay];
}

@end
