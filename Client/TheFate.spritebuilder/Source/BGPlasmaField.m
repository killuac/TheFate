//
//  BGPlasmaField.m
//  TheFate
//
//  Created by Killua Liu on 5/2/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGPlasmaField.h"
#import "BGGameScene.h"

@implementation BGPlasmaField {
    NSUInteger _selTargetCount;
}

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    NSUInteger halfCount = floor(player.game.alivePlayerCount/2.0);
    NSInteger distance = (player.seatIndex <= halfCount) ?
    player.distance+player.plusDistance+player.seatIndex-1 :
    player.distance+player.plusDistance+player.game.alivePlayerCount-player.seatIndex-1;
    
    return (!player.isMe && (NSInteger)self.player.attackRange >= distance);
}

- (void)checkHandCardEnablement
{
    [self.player addTextPromptForSelectedHeroSkill];    // Change tip text according to selected target
    
    if (0 == _selTargetCount) {
        [self.player.handArea disableAllHandCardsWithDarkColor];
    }
    else if (self.player.handArea.selectedCards.count > 0) {
        NSMutableArray *selCardSuits = [NSMutableArray arrayWithCapacity:4];
        for (BGHandCard *card in self.player.handArea.selectedCards) {
            [selCardSuits addObject:@(card.cardSuits)];
        }
        NSMutableArray *cardIds = [NSMutableArray array];
        for (BGHandCard *card in self.player.handArea.handCards) {
            // Available card must be different suits
            if (card.isSelected || ![selCardSuits containsObject:@(card.cardSuits)])
                [cardIds addObject:@(card.cardId)];
        }
        
        [self.player enableHandCardWithCardIds:cardIds];
        self.player.requiredSelCardCount = _selTargetCount;
        self.player.selectableCardCount = self.maxTargetCount;
    }
    else {
        [self.player.handArea enableAllHandCards];
    }
}

- (NSString *)useTipText
{
    _selTargetCount = self.player.targetPlayerNames.count;
    NSString *cardCount = @(_selTargetCount).stringValue;
    return (_selTargetCount > 0) ? [BGUtil textWith:_triggerTipText parameter:cardCount] : _useTipText;
}

@end
