//
//  BGHealingSpell.m
//  TheFate
//
//  Created by Killua Liu on 5/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGHealingSpell.h"
#import "BGPlayer.h"

@implementation BGHealingSpell

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe && player.character.healthPoint < player.character.hpLimit);
}

@end
