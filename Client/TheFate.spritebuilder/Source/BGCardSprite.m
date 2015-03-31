//
//  BGCardSprite.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardSprite.h"
#import "BGConstant.h"

@implementation BGCardSprite

- (void)setSelected:(BOOL)selected
{
    _selectionHalo.visible = _selected = selected;
}

@end
