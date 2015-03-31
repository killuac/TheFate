//
//  BGLifeBreak.m
//  TheFate
//
//  Created by Killua Liu on 5/4/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGLifeBreak.h"
#import "BGPlayer.h"

@implementation BGLifeBreak

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe && player.handCardCount > 0);
}

@end
