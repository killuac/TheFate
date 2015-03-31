//
//  BGShrapnel.m
//  FateClient
//
//  Created by Killua Liu on 12/26/13.
//
//

#import "BGShrapnel.h"
#import "BGPlayer.h"

@implementation BGShrapnel

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    BGHandCard *card = [BGHandCard handCardWithCardId:self.transformedCardId ofPlayer:self.player];
    return [card checkPlayerEnablement:player];
}

@end
