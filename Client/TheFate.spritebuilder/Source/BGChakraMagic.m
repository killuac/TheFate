//
//  BGChakraMagic.m
//  FateClient
//
//  Created by Killua Liu on 12/28/13.
//
//

#import "BGChakraMagic.h"
#import "BGPlayer.h"

@implementation BGChakraMagic

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
//  The Chakra skill is used for self by default
    if (0 == self.player.targetPlayerNames.count) {
        [self.player.targetPlayerNames addObject:self.player.playerName];
    }
    return (!player.isMe);
}

@end
