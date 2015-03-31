//
//  BGGreed.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGGreed.h"
#import "BGGameScene.h"

@implementation BGGreed

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe && (player.handCardCount > 0 || player.equipment.equippedCards.count > 0));
}

@end
