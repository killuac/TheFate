//
//  CCAnimation+BGAnimation.h
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCAnimation.h"

@interface CCAnimation (BGAnimation)

/* Creates a CCAnimation from individual sprite frames. Assumes the sprite frames have already been loaded.
 * The name is the base name of the files which must be suffixed with consecutive numbers.
 * For example: ship0.png, ship1.png, ship2.png ... (name:@"ship" frameCount:3)
 */
+ (CCAnimation*)animationWithFrameName:(NSString*)frameName frameCount:(NSUInteger)frameCount delay:(float)delay;

@end
