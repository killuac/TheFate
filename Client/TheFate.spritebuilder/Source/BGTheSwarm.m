//
//  BGTheSwarm.m
//  FateClient
//
//  Created by Killua Liu on 12/17/13.
//
//

#import "BGTheSwarm.h"
#import "BGGameScene.h"


@implementation BGTheSwarm {
    NSUInteger _selTargetCount;
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return YES;
}

- (void)checkHandCardEnablement
{
    [self.player addTextPromptForSelectedHeroSkill]; // Change tip text according to selected target
    
    if (_selTargetCount >= 2) {
        [self.player enableHandCardWithCardIds:self.player.availableCardIds];
        self.player.selectableCardCount = self.player.requiredSelCardCount = _selTargetCount;
    } else {
        [self.player.handArea disableAllHandCardsWithDarkColor];
    }
}

- (NSString *)useTipText
{
    _selTargetCount = self.player.targetPlayerNames.count;
    NSString *cardCount = @(_selTargetCount).stringValue;
    return (_selTargetCount >= 2) ? [BGUtil textWith:_triggerTipText parameter:cardCount] : _useTipText;
}

@end
