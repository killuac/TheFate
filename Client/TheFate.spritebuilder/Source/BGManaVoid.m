//
//  BGManaVoid.m
//  FateClient
//
//  Created by Killua Liu on 12/26/13.
//
//

#import "BGManaVoid.h"
#import "BGPlayer.h"

@implementation BGManaVoid

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (player.handCardCount < player.character.handSizeLimit);
}

@end
