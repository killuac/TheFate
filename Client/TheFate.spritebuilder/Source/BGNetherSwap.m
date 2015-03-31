//
//  BGNetherSwap.m
//  TheFate
//
//  Created by Killua Liu on 5/12/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGNetherSwap.h"
#import "BGPlayer.h"

@implementation BGNetherSwap

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe && player.handCardCount > 0);
}

@end
