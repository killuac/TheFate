//
//  BGMislead.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//  First Player A. Next Player B.

#import "BGMislead.h"
#import "BGPlayer.h"

@implementation BGMislead

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (player.character.skillPoint > 0);
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return (player.character.skillPoint < player.character.spLimit);
}

- (NSString *)dispelTipText
{
    BGPlayer *playerA = self.player.targetPlayers.firstObject;
    BGPlayer *playerB = self.player.targetPlayers.lastObject;
    
    NSString *heroName = self.player.heroName;
    NSString *heroNameA = (playerA.isMe) ? HERO_NAME_YOU : playerA.heroName;
    NSString *heroNameB = (playerB.isMe) ? HERO_NAME_YOU : playerB.heroName;
    if ([playerA.heroName isEqual:heroName]) heroNameA = HERO_NAME_SELF;
    if ([playerB.heroName isEqual:heroName]) heroNameB = HERO_NAME_SELF;
    
    NSArray *parameters = @[heroName, heroNameA, heroNameB];
    return [BGUtil textWith:_dispelTipText parameters:parameters];
}

@end
