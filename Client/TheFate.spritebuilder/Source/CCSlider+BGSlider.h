//
//  CCSlider+BGSlider.h
//  TheFate
//
//  Created by Killua Liu on 4/23/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCSlider.h"

@interface CCSlider (BGSlider)

@property (nonatomic, strong, readonly) CCSprite *hlBackground;

- (void)addHighlightedBackground;

@end
