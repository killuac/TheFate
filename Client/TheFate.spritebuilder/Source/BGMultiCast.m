//
//  BGMultiCast.m
//  FateClient
//
//  Created by Killua Liu on 12/28/13.
//
//

#import "BGMultiCast.h"
#import "BGPlayer.h"

@implementation BGMultiCast

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    if (!self.player.preTargetPlayerNames) {
        self.player.preTargetPlayerNames = [self.player.targetPlayerNames copy];
    }
    
    self.player.requiredSelCardCount = 0;   // Don't need select card again
    self.player.requiredTargetCount = self.player.maxTargetCount = self.player.firstUsedCard.targetCount;
    [self.player.targetPlayerNames removeAllObjects];
    return [self.player.firstUsedCard checkPlayerEnablement:player];
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return [self.player.firstUsedCard checkNextPlayerEnablement:player];
}

@end
