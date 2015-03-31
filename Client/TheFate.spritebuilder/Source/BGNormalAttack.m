//
//  BGNormalAttack.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGNormalAttack.h"
#import "BGGameScene.h"

@implementation BGNormalAttack

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    NSUInteger halfCount = floor(player.game.alivePlayerCount/2.0);
    NSInteger distance = (player.seatIndex <= halfCount) ?
        player.distance+player.plusDistance+player.seatIndex-1 :
        player.distance+player.plusDistance+player.game.alivePlayerCount-player.seatIndex-1;
    
    return (!player.isMe && (NSInteger)self.player.attackRange >= distance);
}

- (NSString *)targetTipText
{
    for (BGHeroSkill *skill in self.player.character.heroSkills) {
        if (HeroSkillStrygwyrsThirst == skill.skillId && 1 == self.player.character.healthPoint)    // 嗜血
            return skill.targetTipText;
    }
    
    if (PlayingCardEyeOfSkadi == self.player.equipment.weapon.cardEnum)     // 冰魄之眼
        return self.player.equipment.weapon.targetTipText;
    
    return [BGUtil textWith:_targetTipText parameters:self.player.game.tipParameters];
}

@end
