//
//  BGFervor.m
//  TheFate
//
//  Created by Killua Liu on 5/14/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGFervor.h"
#import "BGPlayer.h"

@implementation BGFervor

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    BGHandCard *transformedCard = [BGHandCard handCardWithCardId:self.transformedCardId ofPlayer:self.player];
    return [transformedCard checkPlayerEnablement:player];
}

@end
