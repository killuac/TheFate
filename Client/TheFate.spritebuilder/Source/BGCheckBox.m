//
//  BGCheckBox.m
//  TheFate
//
//  Created by Killua Liu on 4/10/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCheckBox.h"


@implementation BGCheckBox

- (id)init
{
    if (self = [super init]) {
        _tick = [CCSprite spriteWithImageNamed:kImageCheckBoxTick];
        _tick.anchorPoint = CGPointZero;
        _tick.visible = NO;
        [self addChild:_tick];
    }
    return self;
}

- (void)setSelected:(BOOL)selected
{
    _tick.visible = selected;
    [super setSelected:selected];
}

@end
