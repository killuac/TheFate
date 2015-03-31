//
//  BGShallowGrave.m
//  TheFate
//
//  Created by Killua Liu on 4/17/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGShallowGrave.h"
#import "BGGameScene.h"

@implementation BGShallowGrave

- (NSString *)triggerTipText
{
    BGPlayer *dyingPlayer = self.player.game.dyingPlayer;
    NSString *dyingHeroName = (dyingPlayer.isMe) ? HERO_NAME_SELF : dyingPlayer.heroName;
    
    return [BGUtil textWith:_triggerTipText parameter:dyingHeroName];
}

- (NSString *)dispelTipText
{
    BGPlayer *activePlayer = self.player.game.activePlayer;
    BGPlayer *dyingPlayer = self.player.game.dyingPlayer;
    
    NSString *actHeroName = (activePlayer.isMe) ? HERO_NAME_YOU : activePlayer.heroName;
    NSString *dyingHeroName = (dyingPlayer.isMe) ? HERO_NAME_YOU : dyingPlayer.heroName;
    if ([activePlayer isEqual:dyingPlayer]) dyingHeroName = HERO_NAME_SELF;
    
    return [BGUtil textWith:_dispelTipText parameters:@[actHeroName, dyingHeroName]];
}

@end
