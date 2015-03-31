//
//  CCSlider+BGSlider.m
//  TheFate
//
//  Created by Killua Liu on 4/23/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCSlider+BGSlider.h"

@implementation CCSlider (BGSlider)

- (CCSprite *)hlBackground
{
    return (CCSprite *)[self getChildByName:@"hlBackground" recursively:NO];
}

- (void)addHighlightedBackground
{
    CCSprite *slider = [CCSprite spriteWithImageNamed:kImageSliderHighlighted];
    slider.positionType = CCPositionTypeNormalized;
    slider.position = ccp(0.005, 0.5);
    slider.anchorPoint = ccp(0, 0.5);
    slider.scaleX = self.sliderValue;
    [self addChild:slider z:0 name:@"hlBackground"];
    
    self.handle.zOrder = 1;
}

@end
