//
//  BGNaturesAttendants.m
//  FateClient
//
//  Created by Killua Liu on 12/17/13.
//
//

#import "BGNaturesAttendants.h"
#import "BGPlayer.h"


@implementation BGNaturesAttendants {
    NSUInteger _selTargetCount;
}

- (void)checkHandCardEnablement
{
    self.player.maxTargetCount = self.player.character.skillPoint;
    [self.player addTextPromptForSelectedHeroSkill];    // Change tip text according to selected target
    
    if (_selTargetCount > 0) {
        [self.player enableHandCardWithCardIds:self.player.availableCardIds];
        self.player.selectableCardCount = self.player.requiredSelCardCount = _selTargetCount;
    } else {
        [self.player.handArea disableAllHandCardsWithDarkColor];
    }
}

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (player.character.healthPoint < player.character.hpLimit);
}

- (NSString *)useTipText
{
    _selTargetCount = self.player.targetPlayerNames.count;
    
    if (_selTargetCount > 0) {
        NSString *cardCount = @(_selTargetCount).stringValue;
        return [BGUtil textWith:_triggerTipText parameter:cardCount];
    } else {
        NSUInteger skillPoint = self.player.character.skillPoint;
        NSString *targetCount = (skillPoint > 1) ? [NSString stringWithFormat:@"1~%tu", skillPoint] : @(skillPoint).stringValue;
        return [BGUtil textWith:_useTipText parameter:targetCount];
    }
}

@end
