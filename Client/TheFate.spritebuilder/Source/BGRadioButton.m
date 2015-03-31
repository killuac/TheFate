//
//  BGRadioButton.m
//  TheFate
//
//  Created by Killua Liu on 4/10/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRadioButton.h"


@implementation BGRadioButton

- (id)init
{
    if (self = [super init]) {
        _dot = [CCSprite spriteWithImageNamed:kImageRadioButtonDot];
        _dot.anchorPoint = CGPointZero;
        _dot.visible = NO;
        [self addChild:_dot];
    }
    return self;
}

- (void)setSelected:(BOOL)selected
{
    for (id node in self.parent.children) {
        if ([node isKindOfClass:[BGRadioButton class]])
            [node dot].visible = NO;
    }
    
    _dot.visible = YES;
    [super setSelected:selected];
}

@end
