//
//  CCEffectNode+BGEffectNode.m
//  TheFate
//
//  Created by Killua Liu on 11/5/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCEffectNode+BGEffectNode.h"

@implementation CCEffectNode (BGEffectNode)

- (void)blurEffect
{
    self.effect = [CCEffectBlur effectWithBlurRadius:6];
}

- (void)clearEffect
{
    self.effect = nil;
}

@end
