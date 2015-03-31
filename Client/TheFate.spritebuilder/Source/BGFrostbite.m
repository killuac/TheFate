//
//  BGFrostbite.m
//  FateClient
//
//  Created by Killua Liu on 12/27/13.
//
//

#import "BGFrostbite.h"
#import "BGGameScene.h"

@implementation BGFrostbite

- (NSString *)triggerTipText
{
    BGPlayer *activePlayer = self.player.game.activePlayer;
    NSString *heroName = activePlayer.heroName;
    return [BGUtil textWith:_triggerTipText parameter:heroName];
}

- (NSString *)dispelTipText
{
    BGPlayer *prePlayer = self.player.game.preActivePlayer;
    NSString *heroName = (prePlayer.isMe) ? HERO_NAME_YOU : prePlayer.heroName;
    NSArray *parameters = @[self.player.game.tipParameters.firstObject, heroName];
    return [BGUtil textWith:_dispelTipText parameters:parameters];
}

@end
