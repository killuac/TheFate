//
//  BGLayoutBox.h
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGLayoutBox : CCLayoutBox

@property (nonatomic) BOOL animated; 

- (void) addChild:(CCNode *)node animated:(BOOL)animated;

@end
