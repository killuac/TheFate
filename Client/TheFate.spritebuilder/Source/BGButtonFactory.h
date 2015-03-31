//
//  BGButtonFactory.h
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BGCardButton.h"
#import "BGHeroButton.h"

@interface BGButtonFactory : NSObject

+ (CCLayoutBox *)createCardButtonBoxWithCards:(NSArray *)cards forTarget:(id)target;
+ (CCLayoutBox *)createCardBackBoxWithCount:(NSUInteger)count forTarget:(id)target;

+ (BGCardButton *)createCardButtonWithCard:(id)card forTarget:(id)target;
+ (BGCardButton *)createEquipmentButtonWithCard:(BGPlayingCard *)card forTarget:(id)target;

+ (NSArray *)createCardButtonsWithCards:(NSArray *)cards forTarget:(id)target;
+ (NSArray *)createCardBackButtonsWithCount:(NSUInteger)count forTarget:(id)target;

@end
