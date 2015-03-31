//
//  BGBreatheFire.m
//  FateClient
//
//  Created by Killua Liu on 12/16/13.
//
//  选中X个其他角色作为目标后，弃X+1张相同花色的手牌

#import "BGBreatheFire.h"
#import "BGPlayer.h"


@implementation BGBreatheFire {
    NSUInteger _selTargetCount;
}

- (void)checkHandCardEnablement
{
    [self.player addTextPromptForSelectedHeroSkill]; // Change tip text according to selected target
    
    if (0 == _selTargetCount) {
        [self.player.handArea disableAllHandCardsWithDarkColor];
    }
    else if (self.player.handArea.selectedCards.count > 0) {
        BGHandCard *selCard = self.player.handArea.selectedCards.lastObject;
        NSMutableArray *cardIds = [NSMutableArray array];
        for (BGHandCard *card in self.player.handArea.handCards) {
            // Available card must be same suits
            if (card.isSelected || (card.cardSuits == selCard.cardSuits))
                [cardIds addObject:@(card.cardId)];
        }
        
        [self.player enableHandCardWithCardIds:cardIds];
        self.player.selectableCardCount = self.player.requiredSelCardCount = _selTargetCount+1;
    }
    else {
        [self.player.handArea enableAllHandCards];
    }
}

- (NSString *)useTipText
{
    _selTargetCount = self.player.targetPlayerNames.count;
    NSString *cardCount = @(_selTargetCount+1).stringValue;
    return (_selTargetCount > 0) ? [BGUtil textWith:_triggerTipText parameter:cardCount] : _useTipText;
}

@end
