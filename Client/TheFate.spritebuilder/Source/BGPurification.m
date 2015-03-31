//
//  BGPurification.m
//  FateClient
//
//  Created by Killua Liu on 12/24/13.
//
//

#import "BGPurification.h"
#import "BGGameScene.h"

@implementation BGPurification

- (NSString *)triggerTipText
{
    BGPlayer *damagedPlayer = self.player.game.damagedPlayer;
    NSString *heroName = (damagedPlayer.isMe) ? HERO_NAME_SELF : damagedPlayer.heroName;
    return [BGUtil textWith:_triggerTipText parameter:heroName];
}

- (NSString *)dispelTipText
{
    BGGameScene *game = self.player.game;
    
    NSString *heroName = self.player.heroName;
    NSString *heroNameA = (game.damagedPlayer.isMe) ? HERO_NAME_YOU : game.damagedPlayer.heroName;
    NSString *heroNameB = (game.activePlayer.isMe) ? HERO_NAME_YOU : game.activePlayer.heroName;
    if ([game.damagedPlayer.heroName isEqual:heroName]) heroNameA = HERO_NAME_SELF;
    if ([game.activePlayer.heroName isEqual:heroName]) heroNameB = HERO_NAME_SELF;
    
    NSArray *parameters = @[heroName, heroNameA, heroNameB];
    return [BGUtil textWith:_dispelTipText parameters:parameters];
}

@end
