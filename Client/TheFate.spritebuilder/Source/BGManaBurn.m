//
//  BGManaBurn.m
//  TheFate
//
//  Created by Killua Liu on 5/4/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGManaBurn.h"
#import "BGPlayer.h"

@implementation BGManaBurn

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe && player.handCardCount > 0);
}

@end
