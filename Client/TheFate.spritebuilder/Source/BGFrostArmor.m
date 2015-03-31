//
//  BGFrostArmor.m
//  TheFate
//
//  Created by Killua Liu on 5/2/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGFrostArmor.h"
#import "BGGameScene.h"

@implementation BGFrostArmor

- (NSString *)triggerTipText
{
    BGPlayer *damageSource = self.player.game.damageSource;
    NSString *heroName = damageSource.heroName;
    return [BGUtil textWith:_triggerTipText parameter:heroName];
}

@end
