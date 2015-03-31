//
//  BGDisarm.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGDisarm.h"
#import "BGPlayer.h"

@implementation BGDisarm

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (player.equipment.equippedCards.count > 0);
}

@end
