//
//  BGSpikedCarapace.m
//  FateClient
//
//  Created by Killua Liu on 12/26/13.
//
//

#import "BGSpikedCarapace.h"
#import "BGGameScene.h"

@implementation BGSpikedCarapace

- (NSString *)triggerTipText
{
    BGPlayer *turnOwner = self.player.game.turnOwner;
    NSString *heroName = turnOwner.heroName;
    return [BGUtil textWith:_triggerTipText parameter:heroName];
}

@end
