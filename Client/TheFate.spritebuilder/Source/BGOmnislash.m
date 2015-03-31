//
//  BGOmnislash.m
//  TheFate
//
//  Created by Killua Liu on 5/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGOmnislash.h"
#import "BGGameScene.h"

@implementation BGOmnislash

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    BGHandCard *transformedCard = [BGHandCard handCardWithCardId:self.transformedCardId ofPlayer:self.player];
    return [transformedCard checkPlayerEnablement:player];
}

- (void)playSound
{
    if (GameStateBoolChoosing == self.player.game.state) {
        [super playSound];
    } else {
        BGHeroSkill *bladeDance = self.player.character.heroSkills.lastObject;
        [bladeDance playSound];
    }
}

- (NSString *)historyText
{
    return (GameStateBoolChoosing == self.player.game.state) ? _historyText : _historyText2;
}

@end
